package netease.cheng.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
//画廊
public class MyGallery extends Gallery {
	// 自动滚动的线程
	private Runnable auto = new Runnable() {
		public void run() {
			scrollRight();// 滚动到右边
			if (autoTime > 0) {
				postDelayed(this, autoTime);// 加入滚动队列；此用法使其循环
			}
		}
	};
	private int autoTime = 0;
	private Runnable plusRunable;

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyGallery(Context context) {
		super(context);
		init();
	}

	private void init() {
		// 设置所有的选项透明度一样
		this.setUnselectedAlpha(1.0f);
		// 设置间隔一样
		this.setSpacing(0);
		// 设置无阴影
		this.setFadingEdgeLength(0);
		// 不设置声音效果
		this.setSoundEffectsEnabled(false);
	}

	// 向左调用按下左键的效果==继承的方法--移动到左边哪一个图片
	private void scrollLeft() {
		onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
	}

	// 向左调用按下右键的效果==继承的方法--移动到右边哪一个图片
	private void scrollRight() {
		onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	}

	// 画廊的X中心位置
	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	// 指定图片的中心位置
	private static int getCenterOfView(View paramView) {
		return paramView.getLeft() + paramView.getWidth() / 2;
	}

	public int getAutoTime() {
		return autoTime;
	}

	public void setAutoTime(int autoTime) {
		this.autoTime = autoTime;
		if (autoTime <= 0) {
			startAutoScroll(false);
		} else {
			// 设置动画持续时间
			setAnimationDuration(1000);
			startAutoScroll(true);
		}
	}

	public void startAutoScroll(boolean paramBoolean) {
		removeCallbacks(this.auto);
		if ((!paramBoolean) || (this.autoTime <= 0)) {
			return;
		}
		// 先移除线程在添加到消息队列
		postDelayed(this.auto, this.autoTime);
	}

	@Override
	// 计算滚动
	public void computeScroll() {
		super.computeScroll();
		Runnable tmp = this.plusRunable;
		if (tmp == null) {
			return;
		}
		if (getSelectedView() != null) {
			// 如果画廊的中心位置不等于被选图片的和中心点
			if (getCenterOfGallery() != getCenterOfView(getSelectedView())) {
				return;
			}
			this.plusRunable = null;
			post(tmp);
		} else {
			this.plusRunable = null;
		}
	}

	@Override
	protected boolean getChildStaticTransformation(View paramView,
			Transformation paramTransformation) {
		return false;
	}

	@Override
	// 重写分开窗口显示
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		startAutoScroll(false);// 设置不自动滚动--false；
	}

	@Override
	// 重写手指触摸事件
	public boolean onFling(MotionEvent e1, MotionEvent e2, float f1, float f2) {
		if (e2.getX() <= e1.getX()) {
			scrollRight();
		} else {
			scrollLeft();
		}
		return true;
	}

	@Override
	// 当发生触摸动作时设置自动滚动
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		startAutoScroll(true);
		return super.onTouchEvent(paramMotionEvent);
	}

	@Override
	// 此方法自动回调
	protected void onWindowVisibilityChanged(int paramInt) {
		super.onWindowVisibilityChanged(paramInt);
		// 当窗口处于隐藏或者gone时===isShown（）判断父类或主窗口的可见性在设置是否滚动
		if ((paramInt != 0) || (!isShown())) {
			// 设置不自动滚动
			startAutoScroll(false);
		} else {
			startAutoScroll(true);
		}
	}

	public Runnable getPlusRunable() {
		return plusRunable;
	}

	public void setPlusRunable(Runnable plusRunable) {
		if (plusRunable != null) {
			this.removeCallbacks(plusRunable);
		}
		this.plusRunable = plusRunable;
	}
}
