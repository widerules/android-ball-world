package com.winbomb.ballworld.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.winbomb.ballworld.Ball;
import com.winbomb.ballworld.BallWorld;
import com.winbomb.ballworld.Hole;
import com.winbomb.ballworld.Setting;
import com.winbomb.ballworld.common.Vec2;
import com.winbomb.ballworld.input.AccHandler;

public class GameView extends SurfaceView implements Runnable {

	private static final float DELAY = 0.005f;

	private SurfaceHolder mHolder;
	private AccHandler mAccHandler;
	private Thread mThread = null;

	private BallWorld world;
	private WorldRender worldRender;
	private Handler mHandler;
	private Rect dstRect;

	private static final int NUMBER_OF_BALLS = 24;
	volatile boolean running = true;

	/** 游戏上次暂停时间 */
	private long lastPause;

	/** 游戏所花时间(单位：毫秒） */
	private long costTime;

	FPSCounter fpsCounter;

	public GameView(Context context) {
		super(context);
		this.setKeepScreenOn(true);

		mHolder = this.getHolder();
		mAccHandler = new AccHandler(context);

		// 初始化Ball world
		world = new BallWorld(Setting.WORLD_WIDTH, Setting.WORLD_HEIGHT);
		world.setBallList(createBallList());
		//world.setBallList(createTestBallList());
		world.setHoles(createHoles());

		// 初始化WorldRender
		worldRender = new WorldRender(world, Cocobox.SCREEN_WIDTH, Cocobox.SCREEN_HEIGHT);
		dstRect = worldRender.getDestRect();

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
				float x = 40 + 80 * j;
				float y = 40 + 80 * i;

				holes[4 * i + j] = new Hole(x, y);
			}
		}

		return holes;
	}

	private List<Ball> createBallList() {

		List<Ball> balls = new ArrayList<Ball>();
		float r = Ball.MIDDLE_BALL_RADIUS;

		int ballNum = 0;
		while (ballNum < NUMBER_OF_BALLS) {
			int x = (int) (Math.random() * (world.getWorldWidth() - r * 2) + r);
			int y = (int) (Math.random() * (world.getWorldHeight() - r * 2) + r);

			Ball ball = new Ball(r);
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

	private List<Ball> createTestBallList() {

		List<Ball> balls = new ArrayList<Ball>();
		float r = Ball.MIDDLE_BALL_RADIUS;

		float x0 = r;
		float y0 = 480 - r;
		for (int i = 0; i < 1; i++) {
			float y = y0 - 2 * i * r;

			Ball ball = new Ball(r);
			ball.setPosition(x0, y);

			balls.add(ball);
		}

		return balls;
	}

	@Override
	public void run() {
		lastPause = System.currentTimeMillis();
		long startTime = System.nanoTime();

		while (running) {

			float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
			startTime = System.nanoTime();

			deltaTime = (deltaTime < Setting.MAX_DELTA_TIME) ? deltaTime : Setting.MAX_DELTA_TIME;

			costTime += System.currentTimeMillis() - lastPause;
			lastPause = System.currentTimeMillis();

			if (!mHolder.getSurface().isValid()) {
				continue;
			}

			Canvas canvas = mHolder.lockCanvas();

			float accX = -mAccHandler.getAccelX();
			float accY = mAccHandler.getAccelY();

			world.setGravity(accX, accY);
			world.step(deltaTime);

			Bitmap worldFrame = worldRender.drawWorldFrame(costTime);
			canvas.drawBitmap(worldFrame, null, dstRect, null);

			mHolder.unlockCanvasAndPost(canvas);

			if (world.isFinished()) {

				Message msg = mHandler.obtainMessage();

				Bundle data = new Bundle();
				data.putLong("TIME", costTime);
				msg.setData(data);

				mHandler.sendMessage(msg);
				costTime = 0;
				pause();
			}
		}
	}

	public void pause() {
		running = false;
		while (true) {
			try {
				mThread.join();
				break;
			} catch (InterruptedException ex) {
				// retry
			}
		}
	}

	public void resume(boolean resetBall) {
		running = true;
		lastPause = System.currentTimeMillis();
		if (resetBall) {
			costTime = 0;
			world.setBallList(createBallList());
		}

		mThread = new Thread(this);
		mThread.start();
	}
}
