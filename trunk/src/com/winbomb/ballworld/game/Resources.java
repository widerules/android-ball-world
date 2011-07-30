package com.winbomb.ballworld.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class Resources {

	private static Context context;
	private static Resources instance = null;
	public static Bitmap imgBall;
	public static Bitmap imgBg;
	public static Bitmap imgHole;

	private Resources(Context ctx) {
		context = ctx;

		imgBall = loadBitmap(R.drawable.img_ball);
		imgBg = loadBitmap(R.drawable.img_bg);
		imgHole = loadBitmap(R.drawable.hole);
	}

	public synchronized static void initResources(Context context) {
		if (instance == null) {
			instance = new Resources(context);
		}
	}

	private static Bitmap loadBitmap(int resId) {
		return ((BitmapDrawable) context.getResources().getDrawable(resId)).getBitmap();
	}
}
