var WebSocketServer = require('ws').Server
, wss = new WebSocketServer({port: 2222});

var async = require('async');

var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/droidBase');

var db = mongoose.connection;
var schema;
var profileImageModel;
var idModel, markerModel, groupModel, catModel;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function (callback) {
	
	var markerSchema = new mongoose.Schema(
	{title: {type: String, default: 'No Title'},
	description: {type: String, default: 'No Description'},
	address: {type: String, default: ''},
	loc: {type: [Number], index: '2dsphere'},
	picDate: {type: String, default: ''},
	current: {type: Boolean, default: false},
	saved: {type: Boolean, default: false}});
	markerModel = mongoose.model('Markers', markerSchema);
	
	var friendCatSchema = new mongoose.Schema(
	{title: {type: String, default: 'No Title'},
	friends: {type: Array}});
	catModel = mongoose.model('FriendCategories', friendCatSchema);
	
	schema = new mongoose.Schema(
	{username: String,
	password: String, 
	status: {type: String, default: 'No Status'}, 
	picDate: {type: String, default: ''}, 
	group: {type: String, default: ''}, 
	lat: {type: Number, default: 0}, 
	lon: {type: Number, default: 0},
	friends: {type: Array},
	friendCats: [friendCatSchema],
	markers: [markerSchema],
	appearOffline: {type: Boolean, default: false},
	online: {type: Boolean, default: false},
	muteSound: {type: Boolean, default: false},
	blockMessages: {type: Boolean, default: false},
	blockInvites: {type: Boolean, default: false}});
	idModel = mongoose.model('Id', schema);
	
	schema = new mongoose.Schema({group_id: String});
	groupModel = mongoose.model('Group', schema);
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
				modifyProfile(data, ws);
				break;
			case 'req':
				mapQuery(data, ws);
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
			case 'setmarker':
				setMarker(data, ws);
				break;
			case 'newmarker':
				newMarker(data, ws);
				break;
			case 'hidemarker':
				hideMarker(data, ws);
				break;
			case 'removemarker':
				removeMarker(data, ws);
				break;
			case 'savemarker':
				saveMarker(data, ws);
				break;
			case 'viewmarkers':
				viewPlaces(data, ws);
				break;
			case 'personalmessage':
				sendPersonalMessage(data, ws);
				break;
			case 'newfriendcategory':
				makeFriendCategory(data, ws);
				break;
			case 'viewfriendcategories':
				viewFriendCategories(data, ws);
				break;
			case 'removefriendcategory':
				removeFriendCategory(data, ws);
				break;
			case 'addtocategory':
				addFriendToCategory(data, ws);
				break;
			case 'removefromcategory':
				removeFriendFromCategory(data, ws);
				break;
			case 'viewfriendcategory':
				viewFriendCategory(data, ws);
				break;
		}
		
	});
	
	ws.on('close', function(){
		setOnlineFlags();
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

function modifyProfile(data, socket){
	idModel.findOneAndUpdate({username: data.username}, {$set: data})
	.exec(function(err, doc){
		sendProfileInfo(doc, socket);
		if(data.group && data.group != ''){
			sendMemberList(data.group, socket);
		}
	});
}

function sendProfileInfo(doc, socket){
	var response = {};
	response.type = 'myprofileupdate';
	response.status = doc.status;
	response.appearOffline = doc.appearOffline;
	response.muteSound = doc.muteSound;
	response.blockInvites = doc.blockInvites;
	response.blockMessages = doc.blockMessages;
	socket.send(JSON.stringify(response));
	
}

function searchQuery(data, socket){
	var response = {};
	response.type = data.type;
	response.people = [];
	var query = idModel.find()
	.or(data.people)
	.select('id username status picDate online appearOffline');
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
			picDate: docs[i].picDate,
			online: (docs[i].online && !docs[i].appearOffline)});
		}
		socket.send(JSON.stringify(response));
	});
}

function mapQuery(data, socket){
	var response = {};
	response.type = 'req';
	response.people = [];
	response.markers = [];
	var functCalls = [];
	
	var usrquery = idModel.findOne()
		.where('username').equals(data.username)
		.select('id lat lon friends')
		.select({markers: {$elemMatch: {current: true}}});
	
	usrquery.exec(function(err, doc){
		
		response.people.push({_id: doc.id, lat: doc.lat, lon: doc.lon});
		if(doc.markers[0]){
			response.markers.push(formMarker(doc.markers[0]));
		}
		
		if (data.getfriends === true){
			var friendquery = idModel.find()
					.where('_id').in(doc.friends)
					.and([{online: 'true'}, {appearOffline: 'false'}])
					.select('id lat lon');
					
			functCalls.push(function(callback){		
				friendquery.exec(function(err, docs){
					response.people = response.people.concat(docs);
					// console.log("FRIENDS");
					// console.log(docs);
					callback(null, true);
				});
			});
		}
		if(data.getgroup === true && data.group !== ''){
			var groupquery = idModel.find()
					.where('group').equals(data.group)
					.where('username').ne(data.username)
					.and([{online: 'true'}, {appearOffline: 'false'}])
					.select('id lat lon')
					.select({markers: {$elemMatch: {current: true}}});
					
			functCalls.push(function(callback){		
				groupquery.exec(function(err, docs){
					docs.forEach(function(member){
						response.people.push({_id: member.id, lat: member.lat, lon: member.lon});
						if(!member.markers) return;
						if(member.markers[0]){
							response.markers.push(formMarker(member.markers[0]));
						}
					});
					// console.log("GROUP");
					// console.log(docs);
					callback(null, true);
				});
			});
		}
		if(data.getnearby === true){
			var nearbyquery = idModel.find()
			.where('lat').gte(data.minLat)
			.where('lat').lte(data.maxLat)
			.where('lon').gte(data.minLon)
			.where('lon').lte(data.maxLon)
			.and([{online: 'true'}, {appearOffline: 'false'}])
			.select('id lat lon');
			
			functCalls.push(function(callback){
				nearbyquery.exec(function(err, docs){
					response.people = response.people.concat(docs);
					// console.log("NEARBY");
					// console.log(docs);
					callback(null, true);
				});
			});
		}
		
		async.parallel(functCalls, function(err, result){
			socket.send(JSON.stringify(response));
		});
	});
}

function formMarker(marker){
	var mrk = {};
	mrk._id = marker.id;
	mrk.lon = marker.loc[0];
	mrk.lat = marker.loc[1];
	
	return mrk;
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
	var query = idModel.findOne()
				.where('username').equals(data.username)
				.where('password').equals(data.password)
				.select('id group');
	query.exec(function(err, doc){
		if(!err && doc){
			var index = wss.clients.indexOf(socket);
			wss.clients[index].username = data.username;
			sendLoginStatus(socket, doc.id, data.username, 'success');
			sendMemberList('', wss.clients[index]);
			doc.online = true;
			doc.save();
		}
		else {
			sendLoginStatus(socket, '', '', 'fail');
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

function setOnlineFlags(){
	var clients = [];
	wss.clients.forEach(function(c){
		clients.push(c.username);
	});
	idModel.update({username: {$nin: clients}}, {$set:{online: 'false'}}, {}, checkUpd);
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
		.select('id username status picDate online appearOffline');
		query.exec(function(err, arr){
			if(!err){
				for(var i = 0; i < arr.length; i++){
					members.push(
					{username: arr[i].username, 
					id: arr[i].id, 
					status: arr[i].status, 
					picDate: arr[i].picDate,
					online: (arr[i].online && !arr[i].appearOffline)});
				}
				// console.log(members);			
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
						{_id: data.userid},
						{ $set: {picDate: chunk.uploadDate.getTime()}},
						{upsert: true},
						function(err, person){
							sendMemberList(data.group, socket);
						});
					} else if(data.object === 'marker'){
						idModel.findOne()
						.where('_id').equals(data.userid)
						.select('markers')
						.exec(function(err, doc){
							if(!err && doc){
								for(var i = 0; i < doc.markers.length; i++){
									if(doc.markers[i].id === data.id){
										doc.markers[i].picDate = chunk.uploadDate.getTime();
										break;
									}
								}
								doc.save(function(){
									delete data['image'];
									viewPlaces(data, socket);
								});
							}
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
	.select('friends friendCats');
	
	query.exec(function(err, doc){
		if(!err && doc){
			// console.log('found user + friend not yet added')
			var index = doc.friends.indexOf(data.friendid);
			if(index > -1){
				doc.friends.splice(index, 1);
			}
			
			doc.friendCats.forEach(function(cat){
				index = cat.friends.indexOf(data.friendid);
				if(index > -1){
					cat.friends.splice(index, 1);
				}
			});
			
			doc.save(function(){
				sendFriendIdList(doc.friends, socket);
				viewFriendCategories(data, socket);
			});
			
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
			var query = idModel.find()
			.where('_id').in(doc.friends)
			.select('id username status picDate online appearOffline');
			
			if(data.except){
				query.where('_id').nin(data.except);
			}
			
			query.exec(function(err, docs){
				if(!err){
					for(var i = 0; i < docs.length; i++){
						// console.log('adding friend ' + docs[i]);
						var friend = {};
						friend.id = docs[i].id;
						friend.username = docs[i].username;
						friend.status = docs[i].status;
						friend.picDate = docs[i].picDate;
						friend.online = docs[i].online && !docs[i].appearOffline;
						
						response.friends.push(friend);
					}
					socket.send(JSON.stringify(response));
				}
			});
		}
	});
}

function newMarker(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('markers')
	.exec(function(err, doc){
		if(!err && doc){
			var marker = {};
			marker.title = data.title;
			marker.description = data.description;
			marker.address = data.address;
			marker.loc = [data.lon, data.lat];
			marker.saved = true;
			
			doc.markers.push(marker);
			doc.save(function(){
				if(data.image){
					data.id = doc.markers[doc.markers.length - 1].id;
					saveImage(data, socket);
				}
			});
		}
	});
}

function setMarker(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('markers')
	.exec(function(err, doc){
		if(!err && doc){
			
			doc.markers.filter(function(mrk){
				if(data.markerid && mrk.id === data.markerid){
					mrk.current = true;
				}else{
					mrk.current = false;
				}
				
				if(mrk.saved === false){
					mrk.remove();
				}
			});
			
			doc.save(function(err, newdoc){
				if(!data.markerid){
					var marker = {};
					marker.loc = [data.lon, data.lat];
					marker.current = true;
				
					newdoc.save(function(err, doc){
						doc.markers.push(marker);
						doc.save();
					});
					
					mapQuery(data, socket);
				}
				else{
					data.type = 'viewmarkers';
					viewPlaces(data, socket);
				}
			});
		}
	});
}

function viewPlaces(data, socket){
	var response = {};
	response.type = data.type;
	response.markers = [];
	
	idModel.findOne()
	.where('username').equals(data.username)
	.select('markers')
	.exec(function(err, doc){
		if(!err && doc){
			doc.markers.forEach(function(marker){
				var mrk = {};
				mrk.id = marker.id;
				mrk.title = marker.title;
				mrk.description = marker.description;
				mrk.picDate = marker.picDate;
				mrk.current = marker.current;
				mrk.saved = marker.saved;
				mrk.address = marker.address;
				
				response.markers.push(mrk);
			});
			
			socket.send(JSON.stringify(response));
		}
	});
}

function saveMarker(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('markers')
	.exec(function(err, doc){
		if(!err && doc){
			doc.markers.filter(function(mrk){
				if(mrk.id === data.id){
					mrk.saved = true;
					if(data.edit){
						mrk.title = data.title;
						mrk.description = data.description;
						mrk.address = data.address;
						mrk.loc = [data.lon, data.lat];
						
						if(data.image){
							saveImage(data, socket);
						}
					}
					doc.save(function(){
						data.type = 'viewmarkers';
						viewPlaces(data, socket);
					});
					return;
				}
			});
		}
	});
}

function removeMarker(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('markers')
	.exec(function(err, doc){
		if(!err && doc){
			doc.markers.filter(function(mrk){
				if(mrk.id === data.id){
					mrk.remove();
					doc.save(function(){
						data.type = 'viewmarkers';
						viewPlaces(data, socket);
					});
					
					return;
				}
			});
		}
	});
}

function hideMarker(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('markers')
	.exec(function(err, doc){
		if(!err && doc){
			doc.markers.filter(function(mrk){
				if(mrk.id === data.id){
					mrk.current = false;
					doc.save(function(){
						data.type = 'viewmarkers';
						viewPlaces(data, socket);
					});
					
					return;
				}
			});
		}
	});
}

function sendPersonalMessage(data, socket){
	//CHANGE TO FILTER
	var index = -1;
	for(var i = 0; i < wss.clients.length; i++){
		
		if(wss.clients[i].username === data.to){
			index = i;
			break;
		}
	}
	
	if(index >= 0){
		var response = {};
		response.type = data.type;
		response.username = data.username;
		response.text = data.text;
		
		wss.clients[index].send(JSON.stringify(response));
	}
}

function makeFriendCategory(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('friendCats')
	.exec(function(err, doc){
		doc.friendCats.push({title: data.title});
		doc.save(function(err){
			viewFriendCategories(data, socket);
		});
	});
}

function viewFriendCategories(data, socket){
	var response = {};
	response.type = 'viewfriendcategories';
	response.cats = [];
	
	idModel.findOne()
	.where('username').equals(data.username)
	.select('friendCats')
	.exec(function(err, doc){
		doc.friendCats.forEach(function(cat){
			var c = {};
			c.title = cat.title;
			c.id = cat.id;
			if(cat.friends){
				c.count = cat.friends.length;
				if(!c.count){
					c.count = 0;
				}
			}else{
				c.count = 0;
			}
			
			response.cats.push(c);
		});
		socket.send(JSON.stringify(response));
	});
}

function viewFriendCategory(data, socket){
	var response = {};
	response.type = 'viewfriendcategory';
	response.friends = [];
	idModel.findOne()
	.where('username').equals(data.username)
	.select({friendCats: {$elemMatch: {_id: data.catid}}})
	.exec(function(err, doc){
		response.title = doc.friendCats[0].title;
		idModel.find()
		.where('_id').in(doc.friendCats[0].friends)
		.select('id username status picDate online appearOffline')
		.exec(function(err, docs){
			if(!err){
				for(var i = 0; i < docs.length; i++){
					var friend = {};
					friend.id = docs[i].id;
					friend.username = docs[i].username;
					friend.status = docs[i].status;
					friend.picDate = docs[i].picDate;
					friend.online = docs[i].online && !docs[i].appearOffline;
					response.friends.push(friend);
				}
				
				socket.send(JSON.stringify(response));
			}
		});
	});
}

function removeFriendCategory(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('friendCats')
	.exec(function(err, doc){
		doc.friendCats.forEach(function(cat){
			if(cat.id === data.catid){
				cat.remove();
				doc.save(function(){
					viewFriendCategories(data, socket);
				});
			}
		});
	});
}

function addFriendToCategory(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('friendCats')
	.exec(function(err, doc){
		doc.friendCats.forEach(function(cat){
			if(cat.id === data.catid){
				if(cat.friends.indexOf(data.friendid) < 0){
					cat.friends.push(data.friendid);
					doc.save(function(){
						viewFriendCategory(data, socket);
					});
				}
			}
		});
	});
}

function removeFriendFromCategory(data, socket){
	idModel.findOne()
	.where('username').equals(data.username)
	.select('friendCats')
	.exec(function(err, doc){
		doc.friendCats.forEach(function(cat){
			if(cat.id === data.catid){
				var index = cat.friends.indexOf(data.friendid);
				if(index > -1){
					cat.friends.splice(index, 1);
				}
				doc.save(function(err, doc){
					viewFriendCategory(data, socket);
				});
			}
		});
	});
}

function checkUpd(err, upd){
}