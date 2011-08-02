package com.winbomb.ballworld;

public interface Setting {

	public static final float EPSILON = 1e-5f;

	public static final float ALLOW_PENETRATION = 0.001f;

	public static final float BIAS_FACTOR = 10f;

	/** 1个cm对应着多少个像素 */
	public static final float SCREEN_RATE = 40;

	/** 弹性碰撞和非弹性碰撞的临界速度，小于这个速度的就认为是非弹性碰撞 */
	public static final float VELOCITY_THREADHOLD = -200.0f;

	/** 从孔中能够逃离的速度阈值 */
	public static final float ESCAPE_VELOCITY = 100f;

	/** Ball World的默认宽度 */
	public static final int WORLD_WIDTH = 300;

	/** Ball World的默认高度 */
	public static final int WORLD_HEIGHT = 440;

	/** 从重力感应获取的数值与BallWorld使用的gravity之间的比值 */
	public static final float GRAVITY_RATIO = 98 * 2;

	/** 两帧之间的最大间隔时间 */
	public static final float MAX_DELTA_TIME = 1 / 20.f;
}
