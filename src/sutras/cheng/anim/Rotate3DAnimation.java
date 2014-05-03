package sutras.cheng.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * 3d动画的旋转原理---使用一个摄像机类围绕目标拍摄； 然后将过程记录在Matrix对象中； 之后直接操作这个Matrix对象以实现3d特效
 * 
 * @author chengkai
 * 
 */
public class Rotate3DAnimation extends Animation {
	// 默认起始翻转角度---from
	public static final float PRE_FROMDEGREE = 0.0f;
	// 默认起始翻转角度---to
	public static final float PRE_TODEGREE = 90.0f;
	// 默认紧随翻转角度---from
	public static final float NEXT_FROMDEGREE = -90.0f;
	// 默认紧随翻转角度---to
	public static final float NEXT_TODEGREE = 0.0f;
	// 默认翻转时间
	public static final long DURATION = 250L;
	// 默认保持翻转后的状态
	public static final boolean FILLAFTER = true;
	// 默认加速插值器
	public static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
	// 摄像机类
	private Camera mCamera;
	// X中心坐标
	private final float mCenterX;
	// Y中心坐标
	private final float mCenterY;
	// 动画转换深度
	private final float mDepthZ;
	// 起始角度
	private final float mFromDegrees;
	// 结束角度
	private final float mToDegrees;
	// 是否允许显示背面
	private final boolean mReverse;

	public Rotate3DAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthz, boolean bool) {
		this.mFromDegrees = fromDegrees;
		this.mToDegrees = toDegrees;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.mDepthZ = depthz;
		this.mReverse = bool;
	}

	// 在初始化函数调用后被调用
	protected void applyTransformation(float time, Transformation tran) {
		// time是一个系统内置时间；时间间隔很小；因此系统默认的跳转时间都很短
		Matrix matrix = tran.getMatrix();
		// 新的转角
		float newDegrees = mFromDegrees + time * (mToDegrees - mFromDegrees);
		mCamera.save();// 旋转前保存当前画面
		if (!this.mReverse) {
			mCamera.translate(0.0F, 0.0F, this.mDepthZ * (1.0F - time));
		} else {
			mCamera.translate(0.0F, 0.0F, time * this.mDepthZ);
		}
		// 饶着Y轴旋转新计算的角度
		mCamera.rotateY(newDegrees);
		// 传递矩阵对象到底层；获得底层matrix的实例
		mCamera.getMatrix(matrix);
		// 摄像机恢复原状
		mCamera.restore();
		// 操作Matrix对象准备平移
		matrix.preTranslate(-mCenterX, -mCenterY);
		// 传递平移
		matrix.postTranslate(mCenterX, mCenterY);
	}

	/**
	 * 获取默认的加速插值器
	 * 
	 * @return
	 */
	public static Interpolator getDefaultInterpolator() {
		return INTERPOLATOR;
	}

	/**
	 * 获得默认的动画实例
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getDefaultInstance(ViewGroup container) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(PRE_FROMDEGREE,
				PRE_TODEGREE, container.getWidth() / 2.0f,
				container.getHeight() / 2.0f, 0.0f, true);
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(INTERPOLATOR);
		return d3;
	}

	/**
	 * 返回默认的起始跳转的动画
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getDefaultPreAnim(ViewGroup container) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(PRE_FROMDEGREE,
				PRE_TODEGREE, container.getWidth() / 2.0f,
				container.getHeight() / 2.0f, 0.0f, true);
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(INTERPOLATOR);
		return d3;
	}

	/**
	 * 返回默认的紧随跳转动画
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getDefaultNextAnim(ViewGroup container) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(NEXT_FROMDEGREE,
				NEXT_TODEGREE, container.getWidth() / 2.0f,
				container.getHeight() / 2.0f, 0.0f, true);
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(INTERPOLATOR);
		return d3;
	}

	/**
	 * 动态创建动画
	 * 
	 * @param container
	 * @param fromDegrees
	 * @param toDegrees
	 * @param duration
	 * @param fillAfter
	 * @param interpolator
	 * @return
	 */
	public static Rotate3DAnimation get3DAnim(ViewGroup container,
			float fromDegrees, float toDegrees, long duration,
			boolean fillAfter, Interpolator interpolator) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(fromDegrees, toDegrees,
				container.getWidth() / 2.0f, container.getHeight() / 2.0f,
				0.0f, true);
		d3.setDuration(duration);
		d3.setFillAfter(fillAfter);
		d3.setInterpolator(interpolator);
		return d3;
	}

	// 方法会在applyTransformation（）后调用
	@Override
	public boolean getTransformation(long currentTime,
			Transformation outTransformation) {
		return super.getTransformation(currentTime, outTransformation);
	}

	// 3d动画前的初始化操作
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// 必须调用
		super.initialize(width, height, parentWidth, parentHeight);
		// 初始化摄像头
		mCamera = new Camera();
	}
}