package com.winbomb.ballworld.cocobox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.Log;

import com.winbomb.ballworld.Ball;
import com.winbomb.ballworld.BallWorld;
import com.winbomb.ballworld.Hole;
import com.winbomb.ballworld.Setting;

public class WorldRender {

	private BallWorld world;
	private Canvas canvas;
	private Paint ballPaint;
	private Paint holePaint;
	private Paint bgPaint;
	private Paint blackPaint;

	private Bitmap imgBall;
	private Bitmap imgBg;

	public WorldRender(BallWorld world, Context context) {
		this.world = world;

		ballPaint = new Paint();
		ballPaint.setStyle(Style.FILL);
		ballPaint.setColor(Color.BLUE);

		holePaint = new Paint();
		holePaint.setStyle(Style.FILL);
		holePaint.setColor(Color.BLACK);

		bgPaint = new Paint();
		bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(Color.WHITE);

		blackPaint = new Paint();
		blackPaint.setStyle(Style.FILL);
		blackPaint.setColor(Color.BLACK);

		GraphicsLoader.getInstance(context);
		imgBall = GraphicsLoader.imgBall;
		imgBg = GraphicsLoader.imgBg;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public void drawWorld() {

		if (world == null || canvas == null) {
			Log.w("WORLD RENDER", "World is null or canvas is null. ");
			return;
		}

		drawBackground();

		// draw holes
		if (world.getHoles() != null) {
			for (Hole hole : world.getHoles()) {
				drawHole(hole);
			}
		}

		// draw balls
		if (world.getBallList() != null) {
			for (Ball ball : world.getBallList()) {
				drawBall(ball);
			}
		}
	}

	private void drawBackground() {
		// clear background
		canvas.drawRect(0, 0, 480, 854, blackPaint);

		Rect src = new Rect(0, 0, 320, 480);
		RectF dst = new RectF(Cocobox.renderOffsetX, Cocobox.renderOffsetY, Cocobox.renderOffsetX + Cocobox.renderWidth
				* Cocobox.renderScale, Cocobox.renderHeight * Cocobox.renderScale);
		// draw background
		canvas.drawBitmap(imgBg, src, dst, null);
	}

	private void drawBall(Ball ball) {

		// x & y of ball center
		float x = ((ball.getX() - ball.getRadius()) * Setting.SCREEN_RATE) * Cocobox.renderScale
				+ Cocobox.renderOffsetX;
		float y = (world.getWorldHeight() - ball.getY() - ball.getRadius()) * Setting.SCREEN_RATE * Cocobox.renderScale
				+ Cocobox.renderOffsetY;

		canvas.drawBitmap(imgBall, x, y, null);

	}

	private void drawHole(Hole hole) {

		float cx = hole.getX() * Setting.SCREEN_RATE * Cocobox.renderScale + Cocobox.renderOffsetX;
		float cy = (world.getWorldHeight() - hole.getY()) * Setting.SCREEN_RATE * Cocobox.renderScale
				+ Cocobox.renderOffsetY;

		canvas.drawCircle(cx, cy, hole.getRadius() * Setting.SCREEN_RATE * Cocobox.renderScale, holePaint);
	}
}
