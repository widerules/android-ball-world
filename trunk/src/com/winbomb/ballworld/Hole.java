package com.winbomb.ballworld;

import com.winbomb.ballworld.common.Vec2;

public class Hole {

	public static final float SMALL_HOLE = 2f;

	public static final float MIDDLE_HOLE = 6f;

	public static final float LARGE_HOLE = 10f;

	/** 球洞的位置 */
	private Vec2 pos;

	/** 球洞的半径 */
	private float radius = MIDDLE_HOLE;

	public Hole(float x, float y) {
		this.pos = new Vec2(x, y);
	}

	public Hole(Vec2 pos) {
		this.pos = pos;
	}

	public Hole(float x, float y, float radius) {
		this(x, y);
		this.radius = radius;
	}

	public Hole(Vec2 pos, float radius) {
		this(pos);
		this.radius = radius;
	}

	public Vec2 getPos() {
		return pos;
	}

	public void setPos(Vec2 pos) {
		this.pos = pos;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getX() {
		return this.pos.x;
	}

	public float getY() {
		return this.pos.y;
	}
}
