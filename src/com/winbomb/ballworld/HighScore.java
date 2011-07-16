package com.winbomb.ballworld;

import java.io.Serializable;
import java.util.Date;

public class HighScore implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	/** 所用时间 */
	private long timeCost;

	/** 成绩创造者 */
	private String player;

	/** 成绩创造时间 */
	private Date playTime;

	public long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(long timeCost) {
		this.timeCost = timeCost;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public Date getPlayTime() {
		return playTime;
	}

	public void setPlayTime(Date playTime) {
		this.playTime = playTime;
	}
}
