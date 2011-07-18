package com.winbomb.ballworld.game;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GraphicsLoader {

	public static Bitmap imgBall;
	public static Bitmap imgBg;

	private static GraphicsLoader instance;

	private GraphicsLoader(Context context) {

		InputStream input = null;
		try {
			input = context.getAssets().open("img_ball.png");
			Bitmap imgBallBig = BitmapFactory.decodeStream(input);
			imgBall = Bitmap.createScaledBitmap(imgBallBig, 24, 24, false);

			input = context.getAssets().open("img_bg.jpg");
			imgBg = BitmapFactory.decodeStream(input);

		} catch (IOException ex) {

		} finally {
			closeQuietly(input);
		}

	}

	public synchronized static GraphicsLoader getInstance(Context context) {

		if (instance == null) {
			instance = new GraphicsLoader(context);
		}

		return instance;
	}

	private void closeQuietly(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (Exception ex) {

			}
		}
	}

}
