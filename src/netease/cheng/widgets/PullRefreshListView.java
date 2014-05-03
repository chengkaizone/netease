package netease.cheng.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Scroller;
/**
 * 参考类未使用
 * @author Administrator
 *
 */
public class PullRefreshListView extends FrameLayout {
	//空闲状态
	public static final int REFRESH_STATE_IDLE = 0;
	//处于准备刷新状态
	public static final int REFRESH_STATE_PREPARE_REFRESHING = 2;
	//正在刷新状态
	public static final int REFRESH_STATE_REFRESHING = 3;
	//显示状态
	public static final int REFRESH_STATE_SHOWING = 1;
	//开始刷新线程
	private Runnable mBeginRefreshRunnable;
	//结束线程
	private Runnable mFinishRefreshRunnable;
	//状态改变的线程类
	private Runnable mStateChangedRunnable;
	//阻止拖拉
	private boolean mForbidPull = false;
	//解释器触摸
	private boolean mInterpreterTouch;
	//手势最后的Y坐标
	private int mLastMotionY;
	//最后滚动的Y坐标
	private int mLastScrollY;
	//刷新回调接口
	private PullRefreshCallback mListView;
	//刷新监听
	private OnRefreshListener mOnRefreshListener;
	//刷新状态
	private int mRefreshState = 0;
	//刷新的视图
	private View mRefreshView;
	//帧布局子类作为包含器
	private RefreshViewContainer mRefreshViewContainer;
	//压缩滚动的滚动器
	private Scroller mScroller;
	//目标字符
	private String mTag;

	public PullRefreshListView(Context paramContext) {
		super(paramContext);
		init();
	}

	public PullRefreshListView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	private boolean callListViewTouchEvent(MotionEvent paramMotionEvent) {
		View localView = (View) this.mListView;
		paramMotionEvent.setLocation(
				paramMotionEvent.getX() - localView.getLeft(),
				paramMotionEvent.getY() - localView.getTop() + getScrollY());
		return this.mListView.onTouch(paramMotionEvent);
	}

	private void callRefreshTask() {
		if (this.mOnRefreshListener == null)
			refreshDone();
		else
			this.mOnRefreshListener.doRefresh(this.mTag);
	}
	//清除ListView的触摸事件
	private void clearListViewTouchEvent(MotionEvent event) {
		View localView = (View) this.mListView;
		float f1 = event.getX() - localView.getLeft();
		float f2 = event.getY() - localView.getTop() + getScrollY();
		MotionEvent localMotionEvent = MotionEvent.obtain(event);
		//设置取消动作
		localMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
		
		localMotionEvent.setLocation(f1, f2);
		//清除触摸事件就是调用父类的触摸事件---事件设置为取消动作在调用父类方法
		this.mListView.clearTouch(localMotionEvent);
	}

	private int getCurrentTop() {
		return getMinTop() + getMoveDistance();
	}

	private int getMaxTop() {
		return 2 * getRefreshViewHeight();
	}

	private int getMinTop() {
		return -getRefreshViewHeight();
	}

	private int getRefreshHeight() {
		return (int) (3 * getRefreshViewHeight() / 2.5F);
	}

	private int getRefreshState() {
		int j=0;
		if (!isRefreshing()) {
			int i = getMoveDistance();
			j = 0;
			if (i <= getRefreshHeight())
				if (i > 0)
					j = 1;
				else
					j = 2;
		} else {
			j = 3;
		}
		return j;
	}
	//获得布局计算的高度
	private int getRefreshViewHeight() {
		return this.mRefreshViewContainer.getMeasuredHeight();
	}
	//获得移动的总距离
	private int getTotalMoveDistance() {
		return getMaxTop() - getMinTop();
	}

	private boolean hasInterpreterTouchEvent() {
		if (getMoveDistance() == 0){
			return false;
		}
		return true;
	}
	//解析xml文件或创建实例是初始化压缩滚动器
	private void init() {
		this.mScroller = new Scroller(getContext());
	}
	//是否处于滚动状态
	private boolean isRefreshing() {
		if (this.mBeginRefreshRunnable == null)
			return false;
		return true;
	}

	private void move(int paramInt) {
		if (paramInt == 0)
			return;
		int i = getRefreshState();
		int j = getRefreshState();
		if ((paramInt > 0) && (!isRefreshing())) {
			j = getTotalMoveDistance();
			paramInt = (int) (Math.pow(1.0F - getMoveDistance() / j, 1.5D) * paramInt);
		}
		offsetView(paramInt);
		if (i == j)
			return;
		updateRereshState(j, true);
	}

	private void offsetView(int paramInt) {
		if (paramInt == 0)
			return;
		int i = getCurrentTop();
		i = Math.min(getMaxTop(), Math.max(getMinTop(), i + paramInt)) - i;
		if (i == 0)
			return;
		scrollBy(0, -i);
		if (!isRefreshing())
			return;
		i = getMoveDistance();
		if ((i != getRefreshViewHeight())
				|| (this.mBeginRefreshRunnable == null)) {
			if (i != 0)
				return;
			resetInternal();
		} else {
			this.mScroller.forceFinished(true);
			post(this.mBeginRefreshRunnable);
		}
	}
	//处理按下事件
	private void onDown(MotionEvent event) {
		//滚动结束并且移动的距离==0
		if ((this.mScroller.isFinished()) && (!hasInterpreterTouchEvent()))
			//如果滚动结束---解释触摸设置为false---没有发生触摸事件解---释结束
			this.mInterpreterTouch = false;
		else
			this.mInterpreterTouch = true;
		if (!isRefreshing()) {
			this.mScroller.forceFinished(true);
			updateRereshState(getRefreshState(), true);
		}
		//最终的Y坐标---事件发生的Y坐标点
		this.mLastMotionY = (int) event.getY();
	}

	private void onMove(MotionEvent paramMotionEvent) {
		int j = (int) paramMotionEvent.getY();
		int i = j - this.mLastMotionY;
		boolean bool = this.mInterpreterTouch;
		if (!bool) {
			bool = willInterpreterTouchEvent(i);
			if (bool) {
				clearListViewTouchEvent(paramMotionEvent);
				this.mInterpreterTouch = true;
			}
		}
		if ((bool) && (!isRefreshing()))
			move(i);
		this.mLastMotionY = j;
	}

	private void onUp(MotionEvent paramMotionEvent) {
		if ((!this.mInterpreterTouch) || (isRefreshing()))
			return;
		int i = this.mRefreshState;
		if (i != 2) {
			if (i != 1)
				return;
			updateRereshState(0, true);
		} else {
			updateRereshState(3, true);
		}
	}
	//重设内部属性---所有的回到未初始化状态
	private void resetInternal() {
		this.mBeginRefreshRunnable = null;
		this.mFinishRefreshRunnable = null;
		this.mStateChangedRunnable = null;
		//强制完成滚动
		this.mScroller.forceFinished(true);
		//移动到0,0位置
		scrollTo(0, 0);
		if (!this.mListView.hasRefreshView())
			return;
		//清理刷新的列表项
		this.mListView.clearRefreshViewForList();
		//包含器重新添加刷新的view控件
		this.mRefreshViewContainer.addView(this.mRefreshView);
	}
	//平滑滚动
	private void smoothScroll(int paramInt1, int paramInt2) {
		if (!this.mScroller.isFinished())
			//强制完成
			this.mScroller.forceFinished(true);
		if (paramInt2 == 0)return;
		int i;
		if (paramInt2 <= 0)
			i = Math.max(paramInt2, -getMoveDistance());
		else
			i = Math.min(paramInt2, getTotalMoveDistance() - getMoveDistance());
		this.mLastScrollY = paramInt1;
		this.mScroller.startScroll(0, paramInt1, 0, i, 400);
		invalidate();
	}
	//更新刷新状态
	private void updateRereshState(int paramInt, boolean paramBoolean) {
		if (this.mRefreshState == paramInt){
			return;
		}
		if ((paramInt == 3)&& (((this.mOnRefreshListener == null) 
			|| (!this.mOnRefreshListener.onPrepareRefresh(this.mTag))))){
			paramInt = 0;
		}
		if (this.mRefreshState == paramInt){
			return;
		}
		int j = this.mRefreshState;
		this.mRefreshState = paramInt;
		switch (paramInt) {
		case 0:
			if (this.mStateChangedRunnable == null) {
				if (isRefreshing()) {
					if ((!paramBoolean) || (this.mForbidPull)
							|| (!this.mListView.hasRefreshView())
							|| (getListView().getFirstVisiblePosition() != 0)
							|| (getListView().getChildCount() <= 0))
						resetInternal();
					else
						scrollTo(0, -getListView().getChildAt(0).getBottom());
					if (this.mListView.hasRefreshView()) {
						this.mListView.clearRefreshViewForList();
						this.mRefreshViewContainer.addView(this.mRefreshView);
					}
				}
				int i = -getMoveDistance();
				if (i != 0);
//					post(new Runnable(getCurrentTop(), i) {
//						public void run() {
//							smoothScroll(this.val$start, this.val$distance);
//						}
//					});
			} else {
				this.mStateChangedRunnable = null;
			}
			break;
		case 3:
			if ((!this.mForbidPull) && (paramBoolean)) {
				this.mStateChangedRunnable = new Runnable() {
					public void run() {
						mStateChangedRunnable = null;
//						smoothScroll(
//								PullRefreshListView
//										.access$2(PullRefreshListView.this),
//								PullRefreshListView
//										.access$3(PullRefreshListView.this)
//										- PullRefreshListView.this
//												.getMoveDistance());
						mBeginRefreshRunnable = new Runnable() {
							public void run() {
								scrollTo(0, 0);
								mRefreshViewContainer
										.removeAllViews();
								mListView
										.setRefreshViewForList(mRefreshView);
								callRefreshTask();
							}
						};
					}
				};
				if (getHeight() != 0)
					this.mStateChangedRunnable.run();
			} else {
				callRefreshTask();
			}
		case 1:
		case 2:
		}
		if ((this.mOnRefreshListener == null) || (this.mRefreshState == j))
			return;
		this.mOnRefreshListener.updateRefreshView(this.mTag, this.mRefreshView,
				j, paramInt);
	}
	
	private boolean willInterpreterTouchEvent(int paramInt) {
		//FrameLayout的实现类
		ListView list = this.mListView.getListView();
		if ((!isRefreshing()) && (paramInt > 0)
				&& (list.getFirstVisiblePosition() == 0)
				&& (list.getChildAt(0) != null)
				&& (list.getChildAt(0).getTop() == 0))
			return true;
		return false;
	}
	//计算滚动
	public void computeScroll() {
		super.computeScroll();
		//如果返回true；代表动画没有执行完成；将变更位置
		if (!this.mScroller.computeScrollOffset())
			return;
		move(this.mScroller.getCurrY() - this.mLastScrollY);
		this.mLastScrollY = this.mScroller.getCurrY();
		invalidate();
	}
	//设置是否允许拖拉
	public void forbidPull(boolean paramBoolean) {
		this.mForbidPull = paramBoolean;
		if (!paramBoolean)
			return;
		resetInternal();
	}
	//返回接口的实现类
	public ListView getListView() {
		return this.mListView.getListView();
	}
	//获取Y方向移动的距离
	public int getMoveDistance() {
		return -getScrollY();
	}
	//返回一个动作事件动作---包含坐标点
	public MotionEvent getProperMotionEvent(MotionEvent event) {
		View view = (View) this.mListView;
		//设置动作事件的位置
		event.setLocation(
				event.getX() + view.getLeft(),
				event.getY() + view.getTop() - getScrollY());
		return event;
	}
	//返回布局包含器
	public View getRefreshViewContainer() {
		return this.mRefreshViewContainer;
	}
	//是否接受触摸
	public boolean isInterpreterTouch() {
		return this.mInterpreterTouch;
	}
	/**
	 * 重写父类的方法---这个方法在布局文件解析完成时调用--这个方法执行结束后所有的子视图都被添加进布局中
	 * 构造器结束后被调用
	 */
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.mListView = ((PullRefreshCallback) findViewById(2131493145));
		this.mRefreshViewContainer = new RefreshViewContainer(getContext());
		this.mListView.setPullRefreshListView(this);
		this.mRefreshViewContainer.setPadding(0, 0, 0, getListView()
				.getDividerHeight());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				-1, -2);
		params.gravity = Gravity.CENTER|Gravity.TOP;//48
		addView(this.mRefreshViewContainer, params);
	}
	//传递动作事件
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN){onDown(event);}
		return super.onInterceptTouchEvent(event);
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		((FrameLayout.LayoutParams) this.mRefreshViewContainer
				.getLayoutParams()).topMargin = (-getRefreshViewHeight());
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
		if (this.mStateChangedRunnable == null)
			return;
		post(this.mStateChangedRunnable);
	}

	public boolean onTouch(MotionEvent paramMotionEvent) {
		if (this.mForbidPull)
			this.mInterpreterTouch = false;
		else
			switch (paramMotionEvent.getAction()) {
			case 1:
			case 3:
				onUp(paramMotionEvent);
				break;
			case 2:
				onMove(paramMotionEvent);
			case 0:
			}
		return callListViewTouchEvent(paramMotionEvent);
	}

	public void refresh() {
		updateRereshState(3, true);
	}

	public void refreshDone() {
		updateRereshState(0, true);
	}

	public void refreshDoneNoAnimate() {
		updateRereshState(0, false);
		resetInternal();
	}

	public void refreshNoAnimate() {
		updateRereshState(3, false);
		resetInternal();
	}
	//重设--更新刷新状态
	public void reset() {
		
		updateRereshState(0, false);
		resetInternal();
	}
	//设置刷新监听--设置监听标识符
	public void setOnRefreshListener(String paramString,
			OnRefreshListener paramOnRefreshListener) {
		this.mTag = paramString;
		this.mOnRefreshListener = paramOnRefreshListener;
	}
	//设置刷新的View显示出来
	public void setRefreshView(int resId) {
		this.mRefreshView = inflate(getContext(), resId, null);
		this.mRefreshViewContainer.addView(this.mRefreshView);
	}
	//刷新监听器---三种状态
	public static abstract interface OnRefreshListener {
		public abstract void doRefresh(String paramString);

		public abstract boolean onPrepareRefresh(String paramString);

		public abstract void updateRefreshView(String paramString,
				View paramView, int paramInt1, int paramInt2);
	}
	//自定义的ListView实现的接口实现相关方法
	public static abstract interface PullRefreshCallback {
		public abstract void clearRefreshViewForList();

		public abstract boolean clearTouch(MotionEvent paramMotionEvent);

		public abstract ListView getListView();

		public abstract boolean hasRefreshView();

		public abstract boolean onTouch(MotionEvent paramMotionEvent);

		public abstract void setPullRefreshListView(
				PullRefreshListView paramPullRefreshListView);

		public abstract void setRefreshViewForList(View paramView);
	}
	//刷新的帧包含器
	private class RefreshViewContainer extends FrameLayout {
		public RefreshViewContainer(Context localContext) {
			super(localContext);
		}
		//调度画布绘图
		protected void dispatchDraw(Canvas canvas) {
			super.dispatchDraw(canvas);
			if (getMoveDistance() <= 0)return;
			Drawable divider = getListView().getDivider();
			if (divider == null)
				return;
			Rect rect = divider.copyBounds();
			divider.setBounds(0,
					getHeight() - divider.getIntrinsicHeight(),
					getWidth(), getHeight());
			//先画分隔条在画布上
			divider.draw(canvas);
			//分隔条再重新设置边界---矩形区域
			divider.setBounds(rect);
		}
	}
}