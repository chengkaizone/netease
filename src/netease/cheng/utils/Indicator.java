package netease.cheng.utils;

import netease.cheng.main.R;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
/**
 * 参考类未使用
 * @author Administrator
 *
 */
public abstract class Indicator extends LinearLayout implements
		Animation.AnimationListener {
	//可见时间
	private int VISIBLE_TIME = 300;
	//动画
	private Animation mAnimation;
	//自动隐藏的线程
	private Runnable mAutoHide = new Runnable() {
		public void run() {
			if (mAnimation == null) {
				mAnimation = AnimationUtils.loadAnimation(
						getContext(), R.anim.fade_out_fast);
				mAnimation.setAnimationListener(Indicator.this);
			}
			startAnimation(mAnimation);
		}
	};
	//当前选中的项
	private int mCurrentItem = -1;
	
	private Handler mHandler = new Handler();
	//中数量
	private int mTotalItems = -1;
	//可见项
	private int mVisibleTime = -1;

	public Indicator(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public Indicator(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	private void init(Context paramContext) {
		setFocusable(false);
	}

	public int getCurrentItem() {
		return this.mCurrentItem;
	}

	public int getTotalItems() {
		return this.mTotalItems;
	}

	public void move(float paramFloat) {
	}
	@Override
	public void onAnimationEnd(Animation paramAnimation) {
		setVisibility(4);
	}
	@Override
	public void onAnimationRepeat(Animation paramAnimation) {
	}
	@Override
	public void onAnimationStart(Animation paramAnimation) {
	}
	
	protected abstract void onSetCurrentItem();

	protected abstract void onSetTotalItems();
	
	public void setAutoHide(boolean paramBoolean) {
		if (!paramBoolean) {
			this.mVisibleTime = -1;
			setVisibility(0);//设置可见
		} else {
			this.mVisibleTime = this.VISIBLE_TIME;
			setVisibility(4);//设置隐藏
		}
	}
	
	public void setCurrentItem(int paramInt) {
		if ((paramInt < 0) || (paramInt >= this.mTotalItems)
				|| (paramInt == this.mCurrentItem))
			return;
		this.mCurrentItem = paramInt;
		onSetCurrentItem();
		this.mHandler.removeCallbacks(this.mAutoHide);
		if (this.mVisibleTime <= 0)
			return;
		this.mHandler.postDelayed(this.mAutoHide, this.mVisibleTime);
	}
	
	public void setTotalItems(int paramInt) {
		if (paramInt != this.mTotalItems) {
			this.mTotalItems = paramInt;
			onSetTotalItems();
			this.mHandler.removeCallbacks(this.mAutoHide);
			if (this.mVisibleTime > 0)
				this.mHandler.postDelayed(this.mAutoHide, this.mVisibleTime);
		}
		this.mCurrentItem = -1;
	}
}