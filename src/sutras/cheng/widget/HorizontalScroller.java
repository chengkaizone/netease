package sutras.cheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 此控件下的每一个子布局都会完全填充屏幕
 * 
 * @author chengkai
 */
public class HorizontalScroller extends ViewGroup {
	// 压缩滚动器
	private Scroller mScroller;
	// 速率追踪器
	private VelocityTracker mVelocityTracker;
	// 当前屏幕
	private int mCurScreen;
	// 默认屏幕
	private int mDefaultScreen = 0;
	// 屏幕数量
	private int maxScreen = 0;
	// 触摸静止状态
	private static final int TOUCH_STATE_REST = 0;
	// 处于滚动状态
	private static final int TOUCH_STATE_SCROLLING = 1;
	// 瞬时速率
	private static final int SNAP_VELOCITY = 600;
	// 初始化触摸状态为静止状态
	private int mTouchState = TOUCH_STATE_REST;
	// 触摸事件响应的最小像素值
	private int mTouchSlop;
	// 最后动作所处的X坐标
	private float mLastMotionX;
	// 最后动作所处的Y坐标
	private float mLastMotionY;
	private boolean isAllowOutside = true;
	private float outsideRate = 0.5f;
	private boolean hasMaxScreen = false;

	public HorizontalScroller(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// 初始化压缩滚动器
		mScroller = new Scroller(context);
		// 初始化当前屏幕为0
		mCurScreen = mDefaultScreen;
		// 获取两点间的触摸距离--滚动的像素--slop溢出
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	// 当控件使用xml文件布局时调用此构造器
	public HorizontalScroller(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 初始化压缩滚动器
		mScroller = new Scroller(context);
		// 初始化当前屏幕为0
		mCurScreen = mDefaultScreen;
		// 获取两点间的触摸距离--滚动的像素--slop溢出
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	/**
	 *  在onMeasure(该方法内无法获取视图的宽高)方法之后调用；为子视图分配位置时回调此方法该方法为每一个子视图分配全屏
	 *  ---只调用一次、此时已经为所有的视图计算好宽高---在computerScroll之前调用
	 */
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!hasMaxScreen) {
			this.maxScreen = this.getChildCount();
		}
		// 如果有新视图改变--那么计算它的位置
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					// 为每一个子控件分配位置由于是水平滚动；所以只处理横坐标
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	// 父类请求重新绘图时回调此方法
	@Override
	public void computeScroll() {
		// 如果返回true表示滚动动画未结束；位置将应当被新的位置变更
		if (mScroller.computeScrollOffset()) {
			// 回调滚动改变事件
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 当invalidate被调用时通知前一个通知无效改变
			postInvalidate();
		}
	}

	@Override
	/** 此方法计算尺寸---需要调用measure方法来分配尺寸--水平空间--垂直空间
	 *  当视图宽高改变时会调用此方法
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 通过指定定模式获得尺寸
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		// 获得计算尺寸的模式---三种模式--1尽可能最小--2尽可能最大--3精确
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// 如果是精确尺寸---布局文件中明确指定的尺寸--将抛出异常
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"HorizontalScroll only canmCurScreen run at EXACTLY mode!");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		// 同宽的算法一致
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"HorizontalScroll only can run at EXACTLY mode!");
		}
		// 给定相同的宽高 ---获取子视图总数
		final int count = getChildCount();
		// 为每一个视图分配宽高
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		// 定向到指定位置
		scrollTo(mCurScreen * width, 0);
	}

	// 滚动到目标页---计算当前布局的坐标
	public void snapToDestination() {

		// 屏幕宽度
		final int screenWidth = getWidth();
		/*
		 * 计算目标屏幕//getScrollX()方法返回的是当前视图左边缘的X坐标--算法表示是否超过半屏 返回可能0,1
		 */
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		// 指引到目标屏幕
		snapToScreen(destScreen);
	}

	// 指引到目标屏幕---子视图下标从0开始；snap--对齐
	public void snapToScreen(int whichScreen) {
		int tmp = Math.min(whichScreen, getChildCount() - 1);
		whichScreen = Math.max(0, tmp);
		if (getScrollX() != (whichScreen * getWidth())) {
			final int scrollX = getScrollX();// 获取当前视图左边缘位置
			// 计算X轴方向上滚动到的实际X目标坐标
			final int delta = whichScreen * getWidth() - scrollX;
			mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate();// 通知重绘
		}
	}

	// 设置显示哪个子视图---用于外部调用
	public void setToScreen(int whichScreen) {
		// 此算法计算要显示的视图--->避免出现没有的视图
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		// 滚动到指定的坐标点会回调一个方法；以计算上一个点到指定点的滚动距离
		scrollTo(whichScreen * getWidth(), 0);
	}

	// 返回当前显示的子视图下标
	public int getCurScreen() {
		return mCurScreen;
	}

	// 重写触摸事件
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			// 获得追踪器实例--->obtain获得
			mVelocityTracker = VelocityTracker.obtain();
		}
		// 添加一个用户动作到追踪器---用于追踪用户动作
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// 按下事件强制停止滚动动作
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;
		// 移动事件
		case MotionEvent.ACTION_MOVE:
			// delta是一个单位；用它来表示X的距离
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			if (isAllowOutside) {
				if (mCurScreen == 0 && deltaX <= 0) {
					scrollBy((int) (deltaX * outsideRate), 0);
				} else if (mCurScreen == (maxScreen - 1) && deltaX >= 0) {
					scrollBy((int) (deltaX * outsideRate), 0);
				} else {
					// 水平滚动的总数调用此方法时会回调其它方法来改变移动
					scrollBy(deltaX, 0);
				}
			} else {
				if (mCurScreen == 0 && deltaX <= 0) {
					break;
				} else if (mCurScreen == (maxScreen - 1) && deltaX >= 0) {
					break;
				} else {
					// 水平滚动的总数调用此方法时会回调其它方法来改变移动
					scrollBy(deltaX, 0);
				}
			}
			break;
		// 松手事件
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			// 立即进入速度的极大值参数为最大值
			velocityTracker.computeCurrentVelocity(1000);
			// 获取之前的速率
			int velocityX = (int) velocityTracker.getXVelocity();
			// 速度是矢量有方向---在速度特殊的情况下直接进入另一视图--否则需要计算值来决定
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// 计算牵引到那个视图如果速度超过600并且没有到极端视图则回到前一个视图
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY && mCurScreen < maxScreen - 1) {
				snapToScreen(mCurScreen + 1);
			} else {
				// 依靠值来决定显示的视图
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				// 回收追踪器
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// 触摸静止状态
			mTouchState = TOUCH_STATE_REST;
			break;
		// 取消时为静止状态
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	// 拦截触摸事件；重写该方法将可以看到事件发生的过程；比如切屏效果；如果返回true将拦截子视图的一切动作事件
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		// 处理按下事件//记录当前位置--只处理三种情况
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		// 处理移动事件
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop) {
				// 改变为滚动状态状态
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		// 如果是取消动作和松开事件
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// 触摸停止状态
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	public boolean isAllowOutside() {
		return isAllowOutside;
	}

	public void setAllowOutside(boolean isAllowOutside) {
		this.isAllowOutside = isAllowOutside;
	}

	public float getOutsideRate() {
		return outsideRate;
	}

	public void setOutsideRate(float outsideRate) {
		if (outsideRate >= 1.0f) {
			this.outsideRate = 1.0f;
		} else if (outsideRate < 0.0f) {
			this.outsideRate = 0.0f;
		} else {
			this.outsideRate = outsideRate;
		}
	}

	public int getMaxScreen() {
		return maxScreen;
	}

	public void setMaxScreen(int maxScreen) {
		if (maxScreen > this.getChildCount()) {
			this.maxScreen = this.getChildCount();
		} else if (maxScreen < 1) {
			this.maxScreen = 1;
		} else {
			this.maxScreen = maxScreen;
		}
		hasMaxScreen = true;
	}

}
