package com.winbomb.ballworld.cocobox;

import com.winbomb.ballworld.HighScore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HighScoreAdapter extends BaseAdapter {

	private HighScore[] highScores;
	private LayoutInflater mInflater;

	public HighScoreAdapter(Context context, HighScore[] scores) {
		this.highScores = scores;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (highScores != null) {
			return highScores.length;
		}

		return 0;
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

		holder.txtNo.setText(String.valueOf(position));
		holder.txtTimeCost.setText(String.valueOf((score.getTimeCost() / 1000)));
		holder.txtPlayer.setText(score.getPlayer());
		holder.txtDate.setText("2011-07-11 22:45:00");

		return convertView;
	}

	static class HighScoreViewHolder {
		TextView txtNo, txtTimeCost, txtPlayer, txtDate;
	}

}
