package com.sharkdx.puppeteer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class MonitorView extends View
{
	Paint paint = new Paint();
	Agent agent;

	public MonitorView(Context context, Agent agent)
	{
		super(context);
		this.agent = agent;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		float ratio = agent.screenHeight / (float)agent.screenWidth;

		int left = (int)(getScreenWidth() * 0.1f);
		int top = (int)(getScreenHeight() * 0.1f);

		int w = getScreenWidth() - left;
		int h = (int)(w * ratio);

		Rect r1 = new Rect(left, top, agent.screenWidth, agent.screenHeight);

		r1 = scaleRectToSize(r1, (int)(getScreenWidth() * 0.8f), (int)(getScreenHeight() * 0.8f));

//		r1.offset(getScreenWidth() / 2 - r1.centerX(), 0);

		paint.setColor(Color.GREEN);
		canvas.drawRect(r1, paint);
	}

	private Rect scaleRectToSize(Rect r, int maxWidth, int maxHeight)
	{
		float ratio = r.height() / (float)r.width();

		if(r.width() > maxWidth)
		{
			r.right = r.left + maxWidth;
			r.bottom = r.top + (int)(maxWidth * ratio);
		}
		if(r.height() > maxHeight)
		{
			r.bottom = r.top + maxHeight;
			r.right = r.left + (int)(maxHeight / ratio);
		}
		return r;
	}

	@Override
	public boolean performClick()
	{
		return super.performClick();
	}

	public static int getScreenWidth() {
		return Resources.getSystem().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight() {
		return Resources.getSystem().getDisplayMetrics().heightPixels;
	}
}
