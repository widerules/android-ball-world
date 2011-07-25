package com.winbomb.ballworld.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;

import com.winbomb.ballworld.Ball;
import com.winbomb.ballworld.BallWorld;
import com.winbomb.ballworld.Hole;

/**
 * WorldRender类负责将World进行渲染
 * 
 * @author winbomb
 * 
 */
public class WorldRender {

	private BallWorld world;

	private Paint holePaint;

	private Canvas canvas;
	private Bitmap frameBuffer;

	/**
	 * 游戏的分辨率为320*480，对于不同尺寸的屏幕，需要根据屏幕分辨率的
	 * 
	 * 不同进行不同的缩放。例如在480*800分辨率的屏幕上，就需要将320*480
	 * 
	 * 扩大到480*720，并且上下各留出40个像素(renderOffsetY = 40)让画面
	 * 
	 * 保持在屏幕中间。
	 */

	/** Ball World 的宽度 */
	private float worldWidth;
	/** Ball World 的高度 */
	private float worldHeight;
	/** 画布的宽度 */
	private float canvasWidth;
	/** 画布的高度 */
	private float canvasHeight;
	/** 渲染的宽度 */
	private float renderWidth;
	/** 渲染的高度 */
	private float renderHeight;
	/** 渲染时在x轴的偏移 */
	private float offsetX;
	/** 渲染时在y轴的偏移 */
	private float offsetY;
	/** 缩放比例 */
	private float scale;

	public WorldRender(BallWorld world, int canvasWidth, int canvasHeight) {

		this.world = world;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.worldWidth = world.getWorldWidth();
		this.worldHeight = world.getWorldHeight();

		float ratioX = (float) canvasWidth / worldWidth;
		float ratioY = (float) canvasHeight / worldHeight;

		if (ratioY >= ratioX) {
			scale = ratioX;
			renderWidth = canvasWidth;
			renderHeight = this.worldHeight * scale;
			offsetX = 0;
			offsetY = (canvasHeight - renderHeight) / 2;
		} else {
			scale = ratioY;
			renderHeight = canvasHeight;
			renderWidth = this.worldWidth * scale;
			offsetY = 0;
			offsetX = (canvasWidth - renderWidth) / 2;
		}

		this.frameBuffer = Bitmap.createBitmap((int) renderWidth, (int) renderHeight, Config.RGB_565);
		this.canvas = new Canvas(frameBuffer);

		holePaint = new Paint();
		holePaint.setStyle(Style.FILL);
		holePaint.setColor(Color.BLACK);
	}

	public Bitmap drawWorldFrame() {

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

		return frameBuffer;
	}

	private void drawBackground() {
		// clear background
		canvas.drawColor(Color.WHITE);

		// draw background
		canvas.drawBitmap(Resources.imgBg, 0, 0, null);
	}

	private void drawBall(Ball ball) {
		float x = (ball.getX() - ball.getRadius()) * scale;
		float y = (ball.getY() - ball.getRadius()) * scale;

		if (ball.getTexture() == null) {
			int dstSize = (int) (ball.getRadius() * 2 * scale);
			Bitmap texture = Bitmap.createScaledBitmap(Resources.imgBall, dstSize, dstSize, false);

			ball.setTexture(texture);
		}

		canvas.drawBitmap(ball.getTexture(), x, y, null);
	}

	private void drawHole(Hole hole) {
		float cx = hole.getX() * scale;
		float cy = hole.getY() * scale;
		float cr = hole.getRadius() * scale;
		canvas.drawCircle(cx, cy, cr, holePaint);
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public Rect getDestRect() {
		float left = getOffsetX();
		float top = getOffsetY();
		float right = canvasWidth - getOffsetX();
		float bottom = canvasHeight - getOffsetY();

		return new Rect((int) left, (int) top, (int) right, (int) bottom);
	}
}
