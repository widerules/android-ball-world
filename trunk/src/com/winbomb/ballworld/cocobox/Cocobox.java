package com.winbomb.ballworld.cocobox;

public class Cocobox {

	public static final int SCREEN_WIDTH = 320;
	public static final int SCREEN_HEIGHT = 480;

	public static final float BALL_RESISTANCE = 0.3f;

	public static final float RATIO = 1f;

	/**
	 * 游戏的分辨率为320*480，对于不同尺寸的屏幕，需要根据屏幕分辨率的
	 * 
	 * 不同进行不同的缩放。例如在480*800分辨率的屏幕上，就需要将320*480
	 * 
	 * 扩大到480*720，并且上下各留出40个像素(renderOffsetY = 40)让画面
	 * 
	 * 保持在屏幕中间。
	 */

	/** 开始渲染的X坐标偏移 */
	public static int renderOffsetX;

	/** 开始渲染的Y坐标偏移 */
	public static int renderOffsetY;

	/** 实际渲染的宽度 */
	public static int renderWidth;

	/** 实际渲染的高度 */
	public static int renderHeight;

	/** 设备屏幕像素数（宽） */
	public static int windowWidth;

	/** 设备屏幕像素数（高） */
	public static int windowHeight;

	/** 屏幕的缩放 */
	public static float renderScale;

}
