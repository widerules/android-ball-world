/**
 * <p>文件名称: Vec2.java </p>
 * <p>文件描述: 无</p>
 * <p>版权所有: 版权所有(C)2001-2020</p>
 * <p>公司名称: 深圳市中兴通讯股份有限公司</p>
 * <p>内容摘要: 无</p>
 * <p>其他说明: 无</p>
 * <p>创建日期：2011-6-10</p>
 * <p>完成日期：2011-6-10</p>
 * <p>修改记录1: // 修改历史记录，包括修改日期、修改者及修改内容</p>
 * <pre>
 *    修改日期：
 *    版 本 号：
 *    修 改 人：
 *    修改内容：
 * </pre>
 * <p>修改记录2：…</p>
 * <p>评审记录1: // 评审历史记录，包括评审日期、评审人及评审内容</p>
 * <pre>
 *    评审日期：
 *    版 本 号：
 *    评 审 人：
 *    评审内容：
 * </pre>
 * <p>评审记录2：…</p>
 * @version 1.0
 * @author Administrator
 */

package com.winbomb.ballworld.common;

/**
 * 功能描述:<br>
 * 
 * 
 * <p>
 * Note:
 * 
 * @author Administrator
 * @version 1.0
 */
public class Vec2 {
	public float x;
	public float y;

	public Vec2() {
		this(0, 0);
	}

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Return the length of this vector. */
	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	/** Return the sum of this vector and another; does not alter either one. */
	public Vec2 add(Vec2 v) {
		return new Vec2(x + v.x, y + v.y);
	}

	/** Return the sum of this vector and another; alters this vector. */
	public Vec2 addLocal(Vec2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	/**
	 * Return the difference of this vector and another; does not alter either
	 * one.
	 */
	public Vec2 sub(Vec2 v) {
		return new Vec2(x - v.x, y - v.y);
	}

	/**
	 * Subtract another vector from this one and return result - alters this
	 * vector.
	 */
	public Vec2 subLocal(Vec2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	/**
	 * Return the difference of this vector and another; does not alter either
	 * one.
	 */
	public Vec2 mul(float s) {
		return new Vec2(s * x, s * y);
	}

	/**
	 * Normalize this vector and return the length before normalization. Alters
	 * this vector.
	 */
	public float normalize() {
		float length = length();
		if (length < 0.001) {
			return 0f;
		}

		float invLength = 1.0f / length;
		x *= invLength;
		y *= invLength;
		return length;
	}

	/** 两个向量的点积 */
	public static float dot(Vec2 a, Vec2 b) {
		return a.x * b.x + a.y * b.y;
	}

	public static Vec2 cross(float s, Vec2 a) {
		return new Vec2(-s * a.y, s * a.x);
	}

	public static Vec2 scalarProduct(float s, Vec2 v) {
		return new Vec2(s * v.x, s * v.y);
	}

	public static float distance(Vec2 a, Vec2 b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;

		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public static float length(Vec2 a) {
		return (float) Math.sqrt(a.x * a.x + a.y * a.y);
	}
}
