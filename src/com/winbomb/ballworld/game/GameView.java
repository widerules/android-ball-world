package com.winbomb.ballworld.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.winbomb.ballworld.BallWorld;
import com.winbomb.ballworld.Setting;
import com.winbomb.ballworld.game.impl.StandardCreator;
import com.winbomb.ballworld.input.AccHandler;

public class GameView extends SurfaceView implements Runnable {

	private SurfaceHolder mHolder;
	private AccHandler mAccHandler;
	private Thread mThread = null;

	private BallWorld world;
	private WorldCreator creator;
	private WorldRender worldRender;
	private Handler mHandler;
	private Rect dstRect;

	volatile boolean running = true;

	/** 游戏上次暂停时间 */
	private long lastPause;

	/** 游戏所花时间(单位：毫秒） */
	private long costTime;

	public GameView(Context context) {
		super(context);
		this.setKeepScreenOn(true);

		mHolder = this.getHolder();
		mAccHandler = new AccHandler(context);

		// 初始化Ball world
		world = new BallWorld(Setting.WORLD_WIDTH, Setting.WORLD_HEIGHT);

		creator = new StandardCreator(world);
		world.setBallList(creator.createBalls());
		world.setHoles(creator.createHoles());

		// 初始化WorldRender
		worldRender = new WorldRender(world, Cocobox.SCREEN_WIDTH, Cocobox.SCREEN_HEIGHT);
		dstRect = worldRender.getDestRect();

		lastPause = System.currentTimeMillis();
		costTime = 0;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
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

			// float accX = (float) Math.random() * 20f - 10f;
			// float accY = (float) Math.random() * 20f - 10f;

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
			world.setBallList(creator.createBalls());
		}

		mThread = new Thread(this);
		mThread.start();
	}
}
