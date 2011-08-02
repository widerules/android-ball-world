package com.winbomb.ballworld.game;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winbomb.ballworld.HighScore;

public class HighScoreAdapter extends BaseAdapter {

	private HighScore[] highScores;
	private LayoutInflater mInflater;

	private String dateFormat = "yyyy/MM/dd";
	private SimpleDateFormat sdf;

	public HighScoreAdapter(Context context, HighScore[] scores) {
		this.highScores = scores;
		mInflater = LayoutInflater.from(context);

		sdf = new SimpleDateFormat(dateFormat);

	}

	@Override
	public int getCount() {
		if (highScores == null) {
			return 0;
		}

		int count = 0;
		for (count = 0; count < highScores.length; count++) {
			if (highScores[count] == null || count >= 10) {
				break;
			}
		}

		return count;
	}

	@Override
	public Object getItem(int position) {
		if (highScores == null || position > highScores.length) {
			return null;
		}

		return highScores[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HighScore score = (HighScore) getItem(position);
		HighScoreViewHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.high_score_list, null);
			holder = new HighScoreViewHolder();

			holder.txtNo = (TextView) convertView.findViewById(R.id.txtNo);
			holder.txtTimeCost = (TextView) convertView.findViewById(R.id.txtTimeCost);
			holder.txtPlayer = (TextView) convertView.findViewById(R.id.txtPlayer);
			holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);

			convertView.setTag(holder);
		} else {
			holder = (HighScoreViewHolder) convertView.getTag();
		}

		holder.txtNo.setText(String.valueOf(position + 1));
		holder.txtTimeCost.setText(String.valueOf((score.getTimeCost() / 1000f)) + "s");
		holder.txtPlayer.setText(score.getPlayer());

		String dateStr = sdf.format(score.getPlayTime());
		holder.txtDate.setText(dateStr);

		return convertView;
	}

	static class HighScoreViewHolder {
		TextView txtNo, txtTimeCost, txtPlayer, txtDate;
	}

}
