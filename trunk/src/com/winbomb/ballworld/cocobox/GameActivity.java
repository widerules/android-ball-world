package com.winbomb.ballworld.cocobox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;

import com.winbomb.ballworld.HighScore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

public class GameActivity extends Activity {
	private GameView mGameView;
	private Handler mHandler;
	private LayoutInflater mInflater;

	/** 用于标识菜单按钮点击之后是否应该恢复 */
	private boolean bResume = true;

	/** 显示的高分的个数 */
	private static final int HIGH_SCORE_COUNT = 10;
	private static final String HIGH_SCORE_FILE = "highscore.data";
	private HighScore[] highScores = new HighScore[HIGH_SCORE_COUNT];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);

		Cocobox.windowWidth = localDisplayMetrics.widthPixels;
		Cocobox.windowHeight = localDisplayMetrics.heightPixels;

		calcScaleFactor();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				Bundle data = msg.getData();
				long costTime = data.getLong("TIME");
				showResult(costTime);
			};
		};

		mGameView = new GameView(this);
		mGameView.setHandler(mHandler);

		// 读入高分数据
		loadHighScores();

		setContentView(mGameView);

		mInflater = LayoutInflater.from(this);
	}

	@Override
	public void onDestroy() {

		// 将高分写入文件
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(HIGH_SCORE_FILE, 0);
			oos = new ObjectOutputStream(fos);

			for (HighScore highScore : highScores) {
				oos.writeObject(highScore);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeQuietly(fos);
			closeQuietly(oos);
		}

		super.onDestroy();
	}

	@Override
	public void onPause() {

		super.onPause();
		this.mGameView.pause();
	}

	@Override
	public void onResume() {

		super.onResume();
		this.mGameView.resume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		// 通过MenuInflater将XML 实例化为 Menu Object
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		bResume = true;
		pauseGame();

		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);

		if (bResume) {
			resumeGame();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.resume:
			resumeGame();
			return true;
		case R.id.restart:
			restartGame();
			return true;
		case R.id.quit:
			quitGame();
			return true;
		case R.id.about:
			bResume = false;
			showAboutInfo();
			return true;
		case R.id.highscore:
			bResume = false;
			showHighScores();
			return true;
		default:
			resumeGame();
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent param) {
		super.onTouchEvent(param);

		openOptionsMenu();
		return true;
	}

	private void restartGame() {
		if (this.mGameView != null) {
			mGameView.restart();
		}
	}

	private void showResult(long time) {

		boolean isHighScore = false;

		int pos;
		for (pos = 0; pos < HIGH_SCORE_COUNT; pos++) {
			if (highScores[pos] == null ||
					time < highScores[pos].getTimeCost()) {
				isHighScore = true;
				break;
			}
		}

		if (isHighScore) {
			for (int i = HIGH_SCORE_COUNT - 1; i > pos; i--) {
				highScores[i] = highScores[i - 1];
			}

			HighScore high = new HighScore();
			high.setTimeCost(time);
			high.setPlayer("anonymous");
			high.setPlayTime(new Date());

			highScores[pos] = high;
		}

		final View textEntryView = mInflater.inflate(R.layout.alert_dialog_text_entry, null);
		AlertDialog resultDlg = new AlertDialog.Builder(this)
				.setTitle(R.string.about)
				.setView(textEntryView)
				.setMessage("耗时：" + time / 1000 + " 秒")
				.setPositiveButton("确定", new RestartGameListener())
				.create();

		resultDlg.show();

	}

	private void showAboutInfo() {

		Dialog aboutInfo = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.btn_star)
				.setTitle(R.string.about)
				.setMessage("message")
				.setPositiveButton("确定", new ResumeGameListener())
				.create();

		aboutInfo.show();
	}

	private void loadHighScores() {

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(HIGH_SCORE_FILE);
			ois = new ObjectInputStream(fis);

			HighScore high;
			int i = 0;
			while (i < HIGH_SCORE_COUNT &&
					(high = (HighScore) ois.readObject()) != null) {
				highScores[i++] = high;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeQuietly(fis);
			closeQuietly(ois);
		}

	}

	private void showHighScores() {

		final View highScoreView = mInflater.inflate(R.layout.alert_dialog_high_score, null);
		ListView highScoreList = (ListView) highScoreView.findViewById(R.id.lstHighScore);

		HighScoreAdapter adapter = new HighScoreAdapter(this, highScores);
		highScoreList.setAdapter(adapter);

		AlertDialog highScoreDlg = new AlertDialog.Builder(this)
				.setTitle(R.string.highscore)
				.setView(highScoreView)
				.setPositiveButton("确定", new ResumeGameListener())
				.create();

		highScoreDlg.show();

	}

	private void quitGame() {
		this.finish();
	}

	private void resumeGame() {
		if (this.mGameView != null) {
			mGameView.resume();
		}
	}

	private void pauseGame() {
		if (this.mGameView != null) {
			mGameView.pause();
		}
	}

	private void closeQuietly(InputStream is) {

		if (is != null) {
			try {
				is.close();
			} catch (Exception ex) {

			}
		}
	}

	private void closeQuietly(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (Exception ex) {

			}
		}
	}

	public void calcScaleFactor() {

		Cocobox.renderWidth = Cocobox.windowWidth;
		Cocobox.renderHeight = (int) (Cocobox.SCREEN_HEIGHT * Cocobox.windowWidth / (float) Cocobox.SCREEN_WIDTH);
		Cocobox.renderScale = (float) Cocobox.renderWidth / (float) Cocobox.SCREEN_WIDTH;

		Cocobox.renderOffsetX = 0;
		Cocobox.renderOffsetY = (int) ((Cocobox.windowHeight - Cocobox.renderHeight) / 2f);
	}

	class ResumeGameListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mGameView.resume();
		}
	}

	class RestartGameListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mGameView.restart();
		}
	}

}