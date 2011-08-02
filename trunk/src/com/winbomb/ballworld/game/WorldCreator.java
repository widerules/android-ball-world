package com.winbomb.ballworld.game;

import java.util.List;

import com.winbomb.ballworld.Ball;
import com.winbomb.ballworld.Hole;

public interface WorldCreator {

	public Hole[] createHoles();

	public List<Ball> createBalls();

	public int getHoleCount();

	public int getBallCount();
}
