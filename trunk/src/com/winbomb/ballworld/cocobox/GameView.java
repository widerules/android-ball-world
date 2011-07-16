package com.winbomb.ballworld.cocobox;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.winbomb.ballworld.Ball;
import com.winbomb.ballworld.BallWorld;
import com.winbomb.ballworld.Hole;
import com.winbomb.ballworld.common.Vec2;
import com.winbomb.ballworld.input.AccHandler;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final float DELAY = 0.002f;

	private SurfaceHolder mHolder;
	private Paint mPaint;
	private AccHandler mAccHandler;
	private Thread mThread = new Thread(this);

	private BallWorld world;
	private WorldRender worldRender;
	private Handler mHandler;

	private static final float RADIUS = 0.3f;
	private static final int NUMBER_OF_BALLS = 4;

	private boolean isFrozen = false;
	private boolean isRestart = false;

	/** 游戏上次暂停时间 */
	private long lastPause;
	/** 游戏所花时间 */
	private long costTime;

	public GameView(Context context) {
		super(context);
		this.setKeepScreenOn(true);

		mHolder = this.getHolder();
		mHolder.addCallback(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color.RED);

		mAccHandler = new AccHandler(context);

		world = new BallWorld(8, 12);
		world.setBallList(createBallList());
		world.setHoles(createHoles());

		worldRender = new WorldRender(world, context);

		lastPause = System.currentTimeMillis();
		costTime = 0;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	private Hole[] createHoles() {

		Hole[] holes = new Hole[4 * 6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				float x = 1 + 2 * j;
				float y = 1 + 2 * i;

				holes[4 * i + j] = new Hole(x, y);
			}
		}

		return holes;
	}

	private List<Ball> createBallList() {

		List<Ball> balls = new ArrayList<Ball>();
		//
		// Ball ball = new Ball(RADIUS);
		// ball.setPosition(new Vec2(1, 2));
		// balls.add(ball);
		//
		// return balls;

		int ballNum = 0;
		while (ballNum < NUMBER_OF_BALLS) {
			int x = (int) (Math.random() * (world.getWorldWidth() - RADIUS * 2) +
					RADIUS);
			int y = (int) (Math.random() * (world.getWorldHeight() - RADIUS * 2)
					+ RADIUS);

			Ball ball = new Ball(RADIUS);
			ball.setPosition(new Vec2(x, y));
			ball.setVelocity(new Vec2(0.f, 0.f));

			boolean bCollide = false;
			for (Ball destBall : balls) {
				if (ball.isCollideBall(destBall)) {
					bCollide = true;
				}
			}

			if (!bCollide) {
				balls.add(ball);
				ballNum++;
			}
		}

		return balls;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (mThread.getState() == State.NEW) {
			mThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {

		lastPause = System.currentTimeMillis();

		while (true) {

			Canvas canvas = mHolder.lockCanvas();

			if (canvas == null) {
				break;
			}

			float accX = -mAccHandler.getAccelX() * 98;
			float accY = -mAccHandler.getAccelY() * 98;

			if (!isFrozen) {
				world.setGravity(accX, accY);
				world.step(DELAY);
			}

			worldRender.setCanvas(canvas);
			worldRender.drawWorld();

			mHolder.unlockCanvasAndPost(canvas);

			if (!isFrozen && world.isFinished()) {

				isFrozen = true;

				Message msg = mHandler.obtainMessage();

				Bundle data = new Bundle();
				costTime += System.currentTimeMillis() - lastPause;
				data.putLong("TIME", costTime);
				msg.setData(data);

				mHandler.sendMessage(msg);
				costTime = 0;
			}
		}
	}

	public void pause() {
		isFrozen = true;
		costTime += System.currentTimeMillis() - lastPause;
	}

	public void resume() {
		isFrozen = false;
		lastPause = System.currentTimeMillis();
	}

	public void restart() {
		world.setBallList(createBallList());
		isFrozen = false;
	}
}
