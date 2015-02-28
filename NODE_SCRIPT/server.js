var WebSocketServer = require('ws').Server
, wss = new WebSocketServer({port: 2222});

var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/droidBase');

var db = mongoose.connection;
var schema;
var profileImageModel;
var idModel;
var groupModel;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function (callback) {
	
	schema = new mongoose.Schema(
		{username: String, password: String, 
		status: {type: String, default: 'No Status'}, 
		phone: String,
		picDate: {type: String, default: ''}, 
		group: {type: String, default: ''}, 
		lat: {type: Number, default: 0}, 
		lon: {type: Number, default: 0},
		friends: {type: Array}});
	idModel = mongoose.model('Id', schema);	
	schema = new mongoose.Schema({group_id: String});
	groupModel = mongoose.model("Group", schema);
});

wss.on('connection', function(ws) {
	ws.on('message', function(message) {
		var data = JSON.parse(message);
		
		if(!ws.username){
			switch(data.type){
				case 'login':
					authenticateLogin(data, ws);
					return;
				case 'register':
					confirmRegistration(data, ws);
					return;
				default:
					sendLoginStatus(ws, '', '', 'fail');
					return;
			}
		}
		
		switch(data.type){
			case 'id':
				//ws.username = data.username;
				idModel.update(
					{username: data.username},
					{ $set: data},
					{upsert: true},
					checkUpd);
				break;
			case 'req':
				var query;
				if(data.group == ''){
					query = idModel
					.where('lat').gte(data.minLat)
					.where('lat').lte(data.maxLat)
					.where('lon').gte(data.minLon)
					.where('lon').lte(data.maxLon)
					.select('id lat lon');
				}
				else{
					query = idModel
						.where('group').equals(data.group)
						.select('id lat lon');
				}
				
				mapQuery(query, ws, data.type);
				break;
			case 'search':
				searchQuery(data, ws);
				break;
			case 'newgroup':
				createGroup(data, ws);
				break;
			case 'leavegroup':
				leaveGroup(data, ws);
				break;
			case 'updategroup':
				updateGroup(data, ws, data.group);
				break;
			case 'invite':
				sendInvite(data);
				break;
			case 'chat':
				broadcastChat(data);
				break;
			case 'login':
				authenticateLogin(data, ws);
				break;
			case 'register':
				confirmRegistration(data, ws);
				break;
			case 'image':
				saveImage(data, ws);				
				break;
			case 'imagedownload':
				getImage(data, ws);
				break;
			case 'viewprofile':
				getProfile(data, ws);
				break;
			case 'addfriend':
				addFriend(data, ws);
				break;
			case 'removefriend':
				removeFriend(data, ws);
				break;
			case 'viewfriends':
				sendFriendList(data, ws);
				break;
		}
		
	});
});

function processQuery(query, socket, type){
	query.exec(function(err, arr){
		
		if(!err){
			var response = {};
			response['type'] = type;
			response['array'] = [].concat(arr);

			socket.send(JSON.stringify(response));
		}
	});
}

function searchQuery(data, socket){
	var response = {};
	response.type = data.type;
	response.people = [];
	var query = idModel.find()
	.or(data.people)
	.select('id username status picDate');
	query.exec(function(err, docs){
		if(err){
			console.log(err);
			return;
		}
		for(var i = 0; i < docs.length; i++){
			response.people.push(
			{username: docs[i].username, 
			_id: docs[i].id, 
			status: docs[i].status, 
			picDate: docs[i].picDate});
		}
		socket.send(JSON.stringify(response));
	});
}

function findNearBy(data, socket){
	
}

function mapQuery(query, socket, type){
	query.exec(function(err, arr){
		if(!err){
			var response = {};
			response.type = type;
			response.people = [].concat(arr);
			
			socket.send(JSON.stringify(response));
		}
	});
}

function broadcastChat(data){
	//GET SENDER'S ID AND PROFILE PIC
	var senderId, senderPicDate;
	var query = idModel.findOne()
	.where('username').equals(data.username)
	.select('id picDate');
	query.exec(function(err, doc){
		if(!err){
			senderId = doc.id;
			senderPicDate = doc.picDate;
		}
		else{
			return;
		}
	});
	//SEND OUT MESSAGE
	var query = idModel
				.where('group').equals(data.group)
				.select('username');
	query.exec(function(err, arr){
		if(!err){
			for(var i = 0; i < arr.length; i++){
				var msgObj = 
					{type: 'chat',
					username: data.username,
					text: data.message, 
					id: senderId, 
					picDate: senderPicDate};
				for(var l = 0; l < wss.clients.length; l++){
					if(wss.clients[l].username === arr[i].username){						
						wss.clients[l].send(JSON.stringify(msgObj));
						break;
					}
				}		
			}	
		}
	});
}

function authenticateLogin(data, socket){
	var query = idModel
				.where('username').equals(data.username)
				.where('password').equals(data.password)
				.select('id group');
	query.exec(function(err, arr){
		if(!err){
			if(arr.length == 1){
				var index = wss.clients.indexOf(socket);
				wss.clients[index].username = data.username;
				sendLoginStatus(socket, arr[0].id, data.username, 'success');
				sendMemberList('', wss.clients[index]);
			}
			else{
				sendLoginStatus(socket, '', '', 'fail');
			}
		}
	});
}

function sendLoginStatus(socket, userId, username, status){
	var response = {};
	response.type = 'login';
	response.id = userId;
	response.username = username;
	response.status = status;
	
	socket.send(JSON.stringify(response));
}

function confirmRegistration(data, socket){
	var query = idModel
	.where('username').equals(data.username)
	.select('username');
	query.exec(function(err, arr){
		if(!err){
			var response = {type: data.type};
			if(arr.length > 0){
				response.status = 'fail';
			}
			else{
				response.status = 'success';
				idModel.create(data);
			}
			
			socket.send(JSON.stringify(response));
		}
	});
}

function updateGroup(data, socket, groupId){
	idModel.update(
				{username: data.username},
				{$set: {group: groupId}},
				{upsert: true},
				checkUpd);

	sendMemberList(groupId, socket);
}

function sendMemberList(groupId, socket){
	var result = {type: 'newgroup', group_id: groupId, members: []};
	var members = [];
	
	//IF GROUP ID IS SUPPLIED - SEND TO ALL GROUP MEMBERS
	if(groupId != ''){
		var query = idModel
		.where('group').equals(groupId)
		.select('id username status picDate');
		query.exec(function(err, arr){
			if(!err){
				for(var i = 0; i < arr.length; i++){
					members.push(
					{username: arr[i].username, 
					id: arr[i].id, 
					status: arr[i].status, 
					picDate: arr[i].picDate});
				}
							
				result.members = members;
				//console.log(result);		
				for(var i = 0; i < arr.length; i++){
					for(var l = 0; l < wss.clients.length; l++){
						if(wss.clients[l].username === arr[i].username){
							wss.clients[l].send(JSON.stringify(result));
							break;
						}
					}
					//wss.clients[connId.indexOf(arr[i].username)].send(JSON.stringify(result));
				}
				return true;
			}
		});
	}
	//IF NO GROUP ID IS SUPPLIED - CHECK IF USER IS IN GROUP, THEN SEND TO THAT GROUP
	//IF NO GROUP - SEND USER'S INFO JUST TO THE USER
	else {
		var query = idModel.findOne({username: socket.username}, function(err, doc){
			if(!err){
				if(doc.group != ''){
					sendMemberList(doc.group, socket);
					return;
				}
				
				members.push(
				{username: doc.username,
				id: doc.id,
				status: doc.status,
				picDate: doc.picDate});
				
				result.members = members;
				socket.send(JSON.stringify(result));
			}
		});
		
		return true;
	}
}

function createGroup(data, socket){
	leaveGroup(data, socket);
	var newGroup = new groupModel({group_id: data.username});
	
	newGroup.save(function(err, group){
		if(!err){
			updateGroup(data, socket, group.id);
		}
	});
}

function leaveGroup(data, socket){
	var oldGroup;
	idModel.findOne({username: data.username}, function(err, doc){
		if(!err){
			oldGroup = doc.group;
			doc.group = '';
			doc.save(function(err){
				sendMemberList(oldGroup, socket);
				sendMemberList('', socket);
				doGroupCleanup(oldGroup);
			});
			//updateGroup(data, socket, "");
			
		}
	});
}

function doGroupCleanup(groupId){
	var query = idModel
		.where('group').equals(groupId)
		.select('username');
	query.exec(function(err, arr){
		if(!err){
			if(arr.length == 0){
				groupModel.remove({_id: groupId}, function(err){});
			}
		}
	});
}

function sendInvite(data){
	var invite = {type: 'invite', group: data.group, username: data.username};
	for(var i = 0; i < wss.clients.length; i++){
		if(wss.clients[i].username === data.inviteuser){
			wss.clients[i].send(JSON.stringify(invite));
			break;
		}
	}
	//wss.clients[connId.indexOf(data.inviteuser)].send(JSON.stringify(invite));
}

function saveImage(data, socket){
	db.db.collection('fs.files', function (err, collection) {
		collection.findOne({filename: data.id}, function(err, file){
			var id = new mongoose.Types.ObjectId();
			if(file){
				id = new mongoose.Types.ObjectId(file._id);
				collection.update(
					{filename: data.id},
					{$set: {uploadDate: new Date()}},
					{upsert: true},
					checkUpd);
			}
			var gs = new mongoose.mongo.GridStore(db.db, id, data.id, 'w');
			
			gs.open(function(err,store) {	
				gs.write(data.image, true, function(err,chunk) {
					//console.log(chunk);
					if(data.object === 'person'){
						idModel.update(
						{_id: data.id},
						{ $set: {picDate: chunk.uploadDate.getTime()}},
						{upsert: true},
						function(err, person){
							sendMemberList(data.group, socket);
						});
					}
				});
			});
		});
    });
}

function getImage(data, socket){
	//FILENAME: (FILENAME)OBJECT ID + UPLOAD UNIX DATE
	
	db.db.collection('fs.files', function (err, collection) {
		collection.findOne({filename: data.id}, function(err, file){
			var id = new mongoose.Types.ObjectId();
			if(file){
				var id = new mongoose.Types.ObjectId(file._id);
				var gs = new mongoose.mongo.GridStore(db.db, id, 'r');
				var response = {type: data.type, objId: file.filename, picDate: file.uploadDate.getTime()};
				//console.log(response.picName);
				gs.open(function(err,store) {
					if(err){
						console.log(err);
						return;
					}
					gs.read(function(err, image) {
						if(err){
							console.log(err);
							return;
						}
						//console.log(data.toString('base64'));
						response.image = image.toString();
						//console.log(response.image);
						//'base64'
						socket.send(JSON.stringify(response));
					});
				});
			}
		});
    });
}

function getProfile(data, socket){
	var response = {};
	response.type = data.type;
	var query = idModel.findOne()
	.where('_id').equals(data.id)
	.select('id username status picDate');
	
	query.exec(function(err, doc){
		if(!err){
			response.username = doc.username;
			response.status = doc.status;
			response.id = doc.id;
			response.picDate = doc.picDate;
			
			// console.log(response);
			socket.send(JSON.stringify(response));
		}
	});
}

function addFriend(data, socket){
	var query = idModel.findOne()
	.where('username').equals(data.username)
	.where('friends').ne(data.friendid)
	.select('friends');
	
	query.exec(function(err, doc){
		if(!err && doc){
			// console.log('found user + friend not yet added')
			doc.friends.push(data.friendid);
			doc.save();
			sendFriendIdList(doc.friends, socket);	
		}
	});
}

function removeFriend(data, socket){
	var query = idModel.findOne()
	.where('username').equals(data.username)
	.where('friends').equals(data.friendid)
	.select('friends');
	
	query.exec(function(err, doc){
		if(!err && doc){
			// console.log('found user + friend not yet added')
			var index = doc.friends.indexOf(data.friendid);
			if(index > -1){
				doc.friends.splice(index, 1);
			}
			doc.save();
			sendFriendIdList(doc.friends, socket);	
		}
	});
}

function sendFriendIdList(friends, socket){
	var response = {};
	response.type = 'friendidupdate';
	response.friends = friends;
	
	socket.send(JSON.stringify(response));
}

function sendFriendList(data, socket){
	var response = {};
	response.type = data.type;
	response.friends = [];
	
	idModel.findOne()
	.where('username').equals(data.username)
	.select('friends')
	.exec(function(err, doc){	
		if(!err && doc){
			// console.log('found destination user');
			idModel.find()
			.where('_id').in(doc.friends)
			.select('id username status picDate')
			.exec(function(err, docs){
				if(!err){
					for(var i = 0; i < docs.length; i++){
						// console.log('adding friend ' + docs[i]);
						response.friends.push(docs[i]);
					}
					
					socket.send(JSON.stringify(response));
				}
			});
		}
	});
	
}

function checkUpd(err, upd){
}