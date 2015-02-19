package team.artyukh.project;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MonkeyView extends View{
	private Movie monkey;
	private InputStream is;
	private long timer;
	
	public MonkeyView(Context context, InputStream stream) {
		super(context);

		this.is = stream;
		monkey = Movie.decodeStream(is);
	}

	public MonkeyView(Context context) {
		super(context);
		initializeView();
	}

	public MonkeyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeView();
	}

	private void initializeView() {
		is = getContext().getResources().openRawResource(
				R.drawable.youfoundthemonkey);
		monkey = Movie.decodeStream(is);

	}
	public MonkeyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeView();
    }
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		super.onDraw(canvas);
		final long now = SystemClock.uptimeMillis();

		if (timer == 0) {
			timer = now;
		}

		final int relTime = (int) ((now - timer) % monkey.duration());
		monkey.setTime(relTime);
		monkey.draw(canvas, (getWidth() - monkey.width()) / 2, (getHeight() - monkey.height()) / 2);
		this.invalidate();
	}

	
}
