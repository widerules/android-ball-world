package com.winbomb.ballworld.game.impl;

import java.util.ArrayList;
import java.util.List;

import com.winbomb.ballworld.Ball;
import com.winbomb.ballworld.BallWorld;
import com.winbomb.ballworld.Hole;
import com.winbomb.ballworld.common.Vec2;
import com.winbomb.ballworld.game.WorldCreator;

public class StandardCreator implements WorldCreator {

	BallWorld world;

	float worldWidth;
	float worldHeight;

	int holeRowCnt;
	int holeColCnt;
	int ballCnt;

	public StandardCreator(BallWorld world) {
		this.world = world;
		this.worldWidth = world.getWorldWidth();
		this.worldHeight = world.getWorldHeight();

		this.holeRowCnt = 5;
		this.holeColCnt = 4;
		this.ballCnt = 20;
	}

	@Override
	public List<Ball> createBalls() {

		List<Ball> balls = new ArrayList<Ball>();
		float r = Ball.MIDDLE_BALL_RADIUS;

		int ballNum = 0;
		while (ballNum < ballCnt) {
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

	@Override
	public Hole[] createHoles() {

		// X,Y方向上的间距
		float deltaX = worldWidth / holeColCnt;
		float deltaY = worldHeight / holeRowCnt;

		float x0 = deltaX / 2;
		float y0 = deltaY / 2;

		Hole[] holes = new Hole[holeRowCnt * holeColCnt];
		for (int i = 0; i < holeRowCnt; i++) {
			for (int j = 0; j < holeColCnt; j++) {
				float x = x0 + deltaX * j;
				float y = y0 + deltaY * i;

				holes[holeColCnt * i + j] = new Hole(x, y);
			}
		}

		return holes;
	}

	@Override
	public int getBallCount() {
		return this.ballCnt;
	}

	@Override
	public int getHoleCount() {
		return this.holeRowCnt * this.holeColCnt;
	}

}
