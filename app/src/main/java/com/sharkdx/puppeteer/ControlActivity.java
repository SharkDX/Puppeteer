package com.sharkdx.puppeteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ControlActivity extends AppCompatActivity
{
	public static String TAG = "Puppet";
	public static Agent currentAgent = null;
	public static long UPDATE_INTERVAL_MS = 100;

	public static void StartControllingAgent(Context context, Agent agent)
	{
		ControlActivity.currentAgent = agent;
		Intent intent = new Intent(context, ControlActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		MonitorView mainView = new MonitorView(this, currentAgent);
		setContentView(mainView);

		final GestureDetector gestureDetector = new GestureDetector(mainView.getContext(), new GestureDetector.SimpleOnGestureListener()
		{
			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				onTap();
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e)
			{
				Log.d(TAG, "Long press!!");
			}

			@Override
			public boolean onDoubleTap(MotionEvent e)
			{
				Log.d(TAG, "Double tapped!!");
				return false;
			}
		});

		mainView.setOnTouchListener(new View.OnTouchListener(){

			long lastUptime = SystemClock.uptimeMillis();

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// IMPORTANT: update the gesture detector.
				if(gestureDetector.onTouchEvent(event))
					return true;

				long currentUptime = SystemClock.uptimeMillis();
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					final int x = (int) event.getRawX();
					final int y = (int) event.getRawY();

					onTouchMove((float)(x - v.getLeft()) / v.getWidth(), (float)(y - v.getTop()) / v.getHeight());
					return true;
				}

				if (currentUptime - lastUptime >= UPDATE_INTERVAL_MS && event.getActionMasked() == MotionEvent.ACTION_MOVE)
				{
					final int x = (int) event.getRawX();
					final int y = (int) event.getRawY();

					onTouchMove((float)(x - v.getLeft()) / v.getWidth(), (float)(y - v.getTop()) / v.getHeight());

					lastUptime = currentUptime;
					return true;
				}

				return false;
				//return ControlActivity.super.onTouchEvent(event);
			}
		});
	}

	private void onTap()
	{
		Log.d(TAG, "Tapped!!");
		Agent agent = getAgent();

		agent.Tap();
	}

	private void onTouchDown(float x, float y)
	{
		Log.d(TAG, "Moved: " + x + ", " + y);
		Agent agent = getAgent();

//		agent.SendMoveStart(x, y);
	}

	private void onTouchMove(float x, float y)
	{
		Log.d(TAG, "Moved: " + x + ", " + y);
		Agent agent = getAgent();

		agent.MoveTo(x, y);
	}

	private Agent getAgent()
	{
		return ControlActivity.currentAgent;
	}

}
