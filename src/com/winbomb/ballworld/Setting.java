package com.winbomb.ballworld;

public interface Setting {

	public static final float EPSILON = 1e-5f;

	public static final float ALLOW_PENETRATION = 0.001f;

	public static final float BIAS_FACTOR = 60f;

	/** 1个cm对应着多少个像素 */
	public static final float SCREEN_RATE = 40;

	public static final float VELOCITY_THREADHOLD = -2.0f;

	/** 从孔中能够逃离的速度阈值 */
	public static final float ESCAPE_VELOCITY = 50f;

}
