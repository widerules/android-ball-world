package com.winbomb.ballworld;

import java.util.ArrayList;
import java.util.List;

import com.winbomb.ballworld.collision.Contact;
import com.winbomb.ballworld.collision.Wall;
import com.winbomb.ballworld.common.Vec2;

public class BallWorld {

	/** 是否进行冲量的积蓄 */
	private static boolean accumulateImpulses = true;

	/** 是否Warm Starting */
	private static boolean warmStarting = true;

	/** 是否进行位置修正 */
	private static boolean positionCorrection = true;

	/** 重力 */
	private Vec2 gravity;

	/** 迭代次数 */
	private int iterationNum;

	/** 球的列表 */
	private List<Ball> ballList;

	/** 洞的列表 */
	private Hole[] holes;

	/** 宽度（单位：厘米） */
	private float worldWidth;

	/** 高度 （单位：厘米） */
	private float worldHeight;

	/** 冲突列表 */
	private List<Contact> contacts;

	public BallWorld() {
		this.iterationNum = 10;
		this.contacts = new ArrayList<Contact>();
		this.ballList = new ArrayList<Ball>();
		this.gravity = new Vec2();
		this.worldWidth = 0f;
		this.worldHeight = 0f;
	}

	public BallWorld(float width, float height) {
		this();

		this.worldWidth = width;
		this.worldHeight = height;
	}

	public void step(float dt) {

		// 检查是否跌入孔中
		checkForHoles();

		// 首先进行冲突检测，将冲突放入collisionList列表中
		checkForContacts();

		// Integrate forces.
		for (Ball ball : ballList) {
			if (ball.getInvMass() == 0.f) {
				continue;
			}

			// v' = v0 + F*dt/m = v0 + g*dt + f*dt/m
			Vec2 deltaVel = gravity.mul(dt);
			deltaVel.addLocal(ball.getForce().mul(dt));
			ball.getVelocity().addLocal(deltaVel);
		}

		// prepare solve
		for (Contact contact : contacts) {
			contact.prepare();
		}

		// solve contact
		for (int i = 0; i < iterationNum; i++) {
			for (Contact contact : contacts) {
				contact.solve();
			}
		}

		// Integrate positions
		// Integrate Velocities
		for (Ball ball : ballList) {

			ball.getPosition().addLocal(ball.getVelocity().mul(dt));
			ball.clearForce();
		}
	}

	/**
	 * 判断是不是所有球都在洞里
	 * 
	 * @return
	 */
	public boolean isFinished() {

		if (ballList == null) {
			return false;
		}

		for (Ball ball : ballList) {
			if (!ball.isInHole()) {
				return false;
			}
		}

		return true;

	}

	private void checkForHoles() {
		if (holes == null || holes.length == 0) {
			return;
		}

		for (Ball ball : ballList) {

			boolean isInHole = false;

			for (Hole hole : holes) {

				if (ball.isInHole(hole)) {

					ball.setPosition(hole.getX(), hole.getY());

					Vec2 vec = gravity.mul(1);
					vec.addLocal(ball.getVelocity());

					if (Vec2.length(vec) < 30 * 40f) {
						ball.clearVelocity();
					}

					isInHole = true;
					break;
				}
			}

			ball.setInHole(isInHole);
		}
	}

	/**
	 * 检测冲突
	 */
	private void checkForContacts() {

		contacts.clear();

		for (int i = 0; i < ballList.size(); i++) {

			Ball srcBody = ballList.get(i);

			checkForCollideWalls(srcBody);

			// 检查是否和其他的球冲突
			for (int j = i + 1; j < ballList.size(); j++) {
				Ball destBody = ballList.get(j);

				if (srcBody.getInvMass() == 0f && destBody.getInvMass() == 0f) {
					continue;
				}

				if (srcBody.isCollideBall(destBody)) {
					Contact contact = new Contact(srcBody, destBody);
					contacts.add(contact);
				}
			}
		}
	}

	private void checkForCollideWalls(Ball srcBody) {

		// left wall
		if (srcBody.isCollideLeftWall()) {
			Contact contact = new Contact(srcBody, Wall.LEFT_WALL);
			contacts.add(contact);
		}

		// right wall
		if (srcBody.isCollideRightWall()) {
			Contact contact = new Contact(srcBody, Wall.RIGHT_WALL);
			contacts.add(contact);
		}

		// top wall
		if (srcBody.isCollideTopWall()) {
			Contact contact = new Contact(srcBody, Wall.TOP_WALL);
			contacts.add(contact);
		}

		// bottom wall
		if (srcBody.isCollideBottomWall()) {
			Contact contact = new Contact(srcBody, Wall.BOTTOM_WALL);
			contacts.add(contact);
		}
	}

	public void setGravity(float gx, float gy) {
		this.gravity.set(gx, gy);
	}

	/** setters & getters */
	public float getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(float worldWidth) {
		this.worldWidth = worldWidth;
	}

	public float getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(float worldHeight) {
		this.worldHeight = worldHeight;
	}

	public static boolean isAccumulateImpulses() {
		return accumulateImpulses;
	}

	public static void setAccumulateImpulses(boolean accumulateImpulses) {
		BallWorld.accumulateImpulses = accumulateImpulses;
	}

	public static boolean isWarmStarting() {
		return warmStarting;
	}

	public static void setWarmStarting(boolean warmStarting) {
		BallWorld.warmStarting = warmStarting;
	}

	public static boolean isPositionCorrection() {
		return positionCorrection;
	}

	public static void setPositionCorrection(boolean positionCorrection) {
		BallWorld.positionCorrection = positionCorrection;
	}

	public List<Ball> getBallList() {
		return ballList;
	}

	public void setBallList(List<Ball> ballList) {
		this.ballList = ballList;

		for (Ball ball : ballList) {
			ball.setWorld(this);
		}
	}

	public Hole[] getHoles() {
		return holes;
	}

	public void setHoles(Hole[] holes) {
		this.holes = holes;
	}
}
