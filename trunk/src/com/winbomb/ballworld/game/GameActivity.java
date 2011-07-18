package com.winbomb.ballworld.game;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;

import com.winbomb.ballworld.HighScore;

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

		// 获取屏幕的宽度和高度
		Cocobox.SCREEN_WIDTH = localDisplayMetrics.widthPixels;
		Cocobox.SCREEN_HEIGHT = localDisplayMetrics.heightPixels;

		// 加载资源
		Resources.initResources(this);

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

		// 不是高分，显示结果就可以了
		if (!isHighScore(time)) {

			AlertDialog resultDlg = new AlertDialog.Builder(this)
					.setTitle(R.string.about)
					.setMessage("耗时：" + time / 1000 + " 秒")
					.setPositiveButton("确定", new RestartGameListener())
					.create();

			resultDlg.show();

		} else {

			// 高分，显示Player输入框，并更新highscores
			final View textEntryView = mInflater.inflate(R.layout.alert_dialog_text_entry, null);
			final TextView txtPlayer = (TextView) textEntryView.findViewById(R.id.edtPlayer);
			HighScoreUpdateListener listener = new HighScoreUpdateListener(time, txtPlayer);

			AlertDialog resultDlg = new AlertDialog.Builder(this)
					.setTitle(R.string.about)
					.setView(textEntryView)
					.setMessage("耗时：" + time / 1000 + " 秒")
					.setPositiveButton("确定", listener)
					.create();

			resultDlg.show();
		}

	}

	private boolean isHighScore(long time) {

		if (highScores[highScores.length - 1] == null) {
			return true;
		}

		return time < highScores[highScores.length - 1].getTimeCost();
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

	class HighScoreUpdateListener implements OnClickListener {

		private long time;
		private TextView txtPlayer;

		public HighScoreUpdateListener(long time, TextView txtPalyer) {
			this.time = time;
			this.txtPlayer = txtPalyer;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {

			int pos;
			for (pos = 0; pos < highScores.length; pos++) {
				if (highScores[pos] == null || time < highScores[pos].getTimeCost()) {
					break;
				}
			}

			for (int i = highScores.length - 1; i > pos; i--) {
				highScores[i] = highScores[i - 1];
			}

			HighScore high = new HighScore();
			high.setTimeCost(time);
			high.setPlayTime(new Date());
			if (txtPlayer == null || txtPlayer.getText() == null || "".equals(txtPlayer.getText())) {
				high.setPlayer("匿名");
			} else {
				high.setPlayer(txtPlayer.getText().toString());
			}

			highScores[pos] = high;
			mGameView.restart();
		}
	}

}