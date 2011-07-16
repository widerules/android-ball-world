package com.winbomb.ballworld;

import com.winbomb.ballworld.common.Vec2;

public class Ball {

	private BallWorld world;

	/** 位置 */
	private Vec2 position;

	/** 速度 (线速度 ） */
	private Vec2 velocity;

	/** 所受力 */
	private Vec2 force;

	/** 半径 */
	private float radius;

	/** 摩擦系数 */
	float friction;

	/** 质量 */
	float mass;

	/** 质量的倒数 （方便后续计算） */
	float invMass;

	/** 恢复系数 */
	float restitution;

	/** 是否在洞中 */
	boolean inHole;

	/**
	 * 构造函数
	 */
	public Ball() {

		this.position = new Vec2();
		this.velocity = new Vec2();
		this.force = new Vec2();

		this.radius = 1f;
		this.friction = 0f;
		this.restitution = 1f;

		this.mass = Float.MAX_VALUE;
		this.invMass = 0.0f;
	}

	/**
	 * 构造函数
	 * 
	 * @param radius
	 *            半径
	 */
	public Ball(float radius) {
		this(radius, 1f);
	}

	/**
	 * 构造函数
	 * 
	 * @param radius
	 *            半径
	 * @param mass
	 *            质量
	 */
	public Ball(float radius, float mass) {

		this();
		this.radius = radius;

		if (mass != Float.MAX_VALUE) {
			this.mass = mass;
			this.invMass = 1f / mass;
		}
	}

	public boolean isCollideLeftWall() {
		return position.x < radius + Setting.EPSILON;
	}

	public boolean isCollideRightWall() {
		return position.x + radius > world.getWorldWidth() - Setting.EPSILON;
	}

	public boolean isCollideTopWall() {
		return position.y + radius > world.getWorldHeight() - Setting.EPSILON;
	}

	public boolean isCollideBottomWall() {
		return position.y < radius + Setting.EPSILON;
	}

	public boolean isCollideBall(Ball destBall) {

		if (destBall == null) {
			return false;
		}

		return Vec2.distance(position, destBall.position) - Setting.EPSILON < radius + destBall.radius;
	}

	public boolean isInHole(Hole hole) {
		if (hole == null) {
			return false;
		}

		return Vec2.distance(position, hole.getPos()) - Setting.EPSILON < 0.04;
	}

	public boolean isFallingHole(Hole hole) {
		if (hole == null) {
			return false;
		}

		return Vec2.distance(position, hole.getPos()) - Setting.EPSILON < hole.getRadius();
	}

	public void clearForce() {
		this.force.set(0, 0);
	}

	public void clearVelocity() {
		this.velocity.set(0, 0);
	}

	/*** Getters & Setters ****/

	public BallWorld getWorld() {
		return world;
	}

	public void setWorld(BallWorld world) {
		this.world = world;
	}

	public Vec2 getPosition() {
		return position;
	}

	public void setPosition(Vec2 position) {
		this.position = position;
	}

	public void setPosition(float px, float py) {
		this.position.set(px, py);
	}

	public Vec2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vec2 velocity) {
		this.velocity = velocity;
	}

	public Vec2 getForce() {
		return force;
	}

	public void setForce(Vec2 force) {
		this.force = force;
	}

	public void setForce(float fx, float fy) {
		this.force.set(fx, fy);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getFriction() {
		return friction;
	}

	public void setFriction(float friction) {
		this.friction = friction;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public float getInvMass() {
		return invMass;
	}

	public float getX() {
		return this.position.x;
	}

	public float getY() {
		return this.position.y;
	}

	public float getRestitution() {
		return restitution;
	}

	public void setRestitution(float restitution) {
		this.restitution = restitution;
	}

	public boolean isInHole() {
		return inHole;
	}

	public void setInHole(boolean inHole) {
		this.inHole = inHole;
	}
}
