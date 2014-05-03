package sutras.cheng.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import sutras.cheng.listener.OnRefreshListener;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 下拉可刷新的ListView---数据不满全屏时单击刷新
 * 
 * @author chengkai
 * 
 */
public class PullClickRefreshListView extends ListView implements
		OnScrollListener {
	// 轻击状态
	private static final int TAP_TO_REFRESH = 1;
	// 下拉状态
	private static final int PULL_TO_REFRESH = 2;
	// 松开状态
	private static final int RELEASE_TO_REFRESH = 3;
	// 正在刷新状态
	private static final int REFRESHING = 4;
	// 图片的最小高度
	private static final int minHeight = 50;
	// 布局解析器
	private LayoutInflater mInflater;
	// 主视图
	private int selected = 1;
	// 所在的上下文
	private Context context;
	// 记录当前滚动的状态
	private int mCurrentScrollState;
	// 记录刷新状态
	private int mRefreshState;
	// 翻转动画
	private RotateAnimation mFlipAnimation;
	// 相反的反转动画
	private RotateAnimation mReverseFlipAnimation;
	// 刷新视图的临界点高度
	private int mRefreshViewHeight;
	// 刷新原始顶部边距
	private int mRefreshOriginalTopPadding;
	// 最后所处的y坐标
	private int mLastMotionY;

	// 下拉移动的像素比率
	private float paddingRatio = 3.0f;
	// 提示刷新标记
	private String hintRefreshLabel = "松开可以刷新";
	// 刷新标记
	private String refreshingLabel = "正在刷新...";
	// 垂直布局容器
	private LinearLayout mRefreshView;
	// 刷新提示语文本控件
	private TextView mRefreshViewText;
	// 用于执行动画效果的图片控件
	private ImageView mRefreshViewImage;
	// 显示刷新进度条(环形)
	private ProgressBar mRefreshViewProgress;
	// 显示本次更新与上次更新的时间段(计算结果)
	private TextView mRefreshViewLastUpdated;
	// 设置图片
	private Drawable drawable;
	// 时间表达式
	private String pattern = "HH:mm";
	// 格式化日期
	private SimpleDateFormat format;
	// 刷新监听器
	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		public void onRefresh() {
		}
	};
	// 滚动监听器
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	};

	public PullClickRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullClickRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullClickRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		// 初始化刷新效果所需要的动画
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// 设置线性插值器
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		// 设置动画时间
		mFlipAnimation.setDuration(250);
		// 设置保留动画后的效果
		mFlipAnimation.setFillAfter(true);
		// 同上7
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);
		// 初始化布局解析器
		mInflater = (LayoutInflater) LayoutInflater.from(context);
		format = new SimpleDateFormat(pattern);
	}

	/**
	 * 通过Id资源解析布局---该方法一定要在设置适配器之前调用
	 * 
	 * @param layout布局文件
	 * @param imageView图片控件
	 * @param hintView提示语文本控件
	 * @param progressBar环形进度条
	 * @param textLastUpdate更新时间
	 */
	public void initHeader(int layout, int imageView, int hintView,
			int progressBar, int textLastUpdate) {
		// 解析布局视图
		mRefreshView = (LinearLayout) mInflater.inflate(layout, null);
		mRefreshViewImage = (ImageView) mRefreshView.findViewById(imageView);
		mRefreshViewText = (TextView) mRefreshView.findViewById(hintView);
		mRefreshViewProgress = (ProgressBar) mRefreshView
				.findViewById(progressBar);
		mRefreshViewLastUpdated = (TextView) mRefreshView
				.findViewById(textLastUpdate);
		mRefreshViewText.setText(hintRefreshLabel);
		mRefreshViewLastUpdated.setText(updateTime());

		drawable = mRefreshViewImage.getDrawable();
		mRefreshViewImage.setMinimumHeight(minHeight);
		mRefreshView.setOnClickListener(new OnClickRefreshListener());
		mRefreshOriginalTopPadding = mRefreshView.getPaddingTop();
		mRefreshState = TAP_TO_REFRESH;
		// 为刷新视图分配尺寸
		measureView(mRefreshView);
		// 将刷新视图计算出来高度设置为刷新视图的临界点
		mRefreshViewHeight = mRefreshView.getMeasuredHeight();
		// 将刷新视图添加为ListView的头部
		addHeaderView(mRefreshView);
		// 注册滚动监听器
		super.setOnScrollListener(this);
	}

	public void setPaddingRatio(float paddingRatio) {
		this.paddingRatio = paddingRatio;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public void setDrawable(int resDraw) {
		this.drawable = context.getResources().getDrawable(resDraw);
	}

	// @Override
	// // 该函数在onDraw方法之前调用
	// protected void onAttachedToWindow() {
	// // 使用滚动器代替setSelection来指定视图位置的原因是防止没有数据为空而显示刷新视图
	// //设置第一项被选
	// setSelection(selected);
	// }

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		setSelection(selected);
	}

	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	/**
	 * 调用该方法表示重新设置提示语
	 * 
	 * @param hintLabel
	 *            默认 松开可以刷新
	 * @param refreshingLabel
	 *            默认 正在刷新...
	 */
	public void initLabel(String hintLabel, String refreshingLabel) {
		this.hintRefreshLabel = hintLabel;
		this.refreshingLabel = refreshingLabel;
	}

	/**
	 * 调用该方法表示重新设置提示语
	 * 
	 * @param hintLabel
	 *            默认 松开可以刷新
	 * @param refreshingLabel
	 *            默认 正在刷新...
	 */
	public void initLabel(int hintLabel, int refreshingLabel) {
		this.hintRefreshLabel = context.getResources().getString(hintLabel);
		this.refreshingLabel = context.getResources()
				.getString(refreshingLabel);
	}

	/**
	 * 注册回调监听接口
	 * 
	 * @param callback
	 *            onRefresh
	 */
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}

	/**
	 * 设置最后更新提示的字符
	 * 
	 * @param lastUpdated
	 *            time at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated != null) {
			mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
			mRefreshViewLastUpdated.setText(lastUpdated);
		} else {
			mRefreshViewLastUpdated.setVisibility(View.GONE);
		}
	}

	/**
	 * 重写触摸事件
	 */
	public boolean onTouchEvent(MotionEvent event) {
		final int y = (int) event.getY();
		switch (event.getAction()) {
		// 松手事件
		case MotionEvent.ACTION_UP:
			// 如果垂直滚动条未被画
			if (!isVerticalScrollBarEnabled()) {
				// 设置滚动条被画---确保始终出现滚动条
				setVerticalScrollBarEnabled(true);
			}
			// 当第一个可见项为第一项是且不处于刷新状态
			if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
				// 当刷新视图的底边Y坐标大于满足刷新的高度或刷新视图的上边Y坐标大于等于0时、且处于释放状态时
				// System.out.println("up--" + mRefreshView.getBottom() + "--"
				// + mRefreshView.getTop());
				if ((mRefreshView.getBottom() > mRefreshViewHeight || mRefreshView
						.getTop() >= 0) && mRefreshState == RELEASE_TO_REFRESH) {
					// 设置为刷新状态
					mRefreshState = REFRESHING;
					Refreshing();
					onRefresh();
				} else if (mRefreshView.getBottom() < mRefreshViewHeight
						|| mRefreshView.getTop() < 0) {
					// 停止刷新、滚动、显示内容视图
					resetHeader();
					setSelection(selected);
				}
			}
			break;
		// 按下事件
		case MotionEvent.ACTION_DOWN:
			// 记录Y坐标点
			mLastMotionY = y;
			break;
		// 移动事件
		case MotionEvent.ACTION_MOVE:
			applyHeaderPadding(event);
			break;
		}
		return super.onTouchEvent(event);
	}

	// 请求头部填补
	private void applyHeaderPadding(MotionEvent event) {
		// 获取事件历史中发生的点数
		final int historySize = event.getHistorySize();
		// System.out.println("historySize---"+historySize);
		int pointerCount = 1;
		try {
			// 获取事件指针数量
			pointerCount = event.getPointerCount();
			// System.out.println("pointerCount-----------"+pointerCount);
		} catch (Exception e) {
			System.out.println("你使用的android sdk版本低于1.5");
			e.printStackTrace();
		}
		for (int h = 0; h < historySize; h++) {
			for (int p = 0; p < pointerCount; p++) {
				if (mRefreshState == RELEASE_TO_REFRESH) {
					if (isVerticalFadingEdgeEnabled()) {
						setVerticalScrollBarEnabled(true);
					}
					int historicalY = 0;
					try {
						// android sdk2.0后才支持的方法
						historicalY = (int) event.getHistoricalY(p, h);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							historicalY = (int) event.getHistoricalY(h);
							System.out.println("historicalY---" + historicalY);
						} catch (Exception e1) {
							e1.printStackTrace();
							System.out.println("您的android sdk版本低于2.0");
						}
					}
					// 计算顶部需要填充的距离,下拉期间我们除以某个数值来模拟一个更耐磨的(resistant)效果
					int topPadding = (int) (((historicalY - mLastMotionY) - mRefreshViewHeight) / paddingRatio);
					// 设置计算好的顶部填充
					mRefreshView.setPadding(mRefreshView.getPaddingLeft(),
							topPadding, mRefreshView.getPaddingRight(),
							mRefreshView.getPaddingBottom());
				}
			}
		}
	}

	/**
	 * 设置头部填充回到原始尺寸
	 */
	private void resetHeaderPadding() {
		mRefreshView.setPadding(mRefreshView.getPaddingLeft(),
				mRefreshOriginalTopPadding, mRefreshView.getPaddingRight(),
				mRefreshView.getPaddingBottom());
	}

	/**
	 * 重置头部、恢复到原始状态
	 */
	private void resetHeader() {
		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH;
			resetHeaderPadding();
			// 设置刷新标签
			mRefreshViewText.setText(this.hintRefreshLabel);
			// 替换刷新图片
			mRefreshViewImage.setImageDrawable(drawable);
			// 清除所有原始动画
			mRefreshViewImage.clearAnimation();
			// 隐藏刷新视图中的图片
			mRefreshViewImage.setVisibility(View.VISIBLE);
			// 隐藏进度条
			mRefreshViewProgress.setVisibility(View.GONE);
		}
	}

	// 测量视图、为视图分配尺寸
	private void measureView(View child) {
		// 得到子视图的布局参数
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			// 如果大于0、设置为精确计算模式
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			// 如果坐标小于0、不可见状态、设置为未指定模式
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		// 为子视图计算宽高
		child.measure(childWidthSpec, childHeightSpec);
	}

	// 实现滚动监听器当执行滚动时回调该方法
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 当刷新视图可见时改变提示语为松手时hintRefreshLabel、松手时为refreshingLabel同时替换箭头图片
		if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
				&& mRefreshState != REFRESHING) {
			if (firstVisibleItem == 0) {
				mRefreshViewImage.setVisibility(View.VISIBLE);
				if ((mRefreshView.getBottom() >= mRefreshViewHeight || mRefreshView
						.getTop() >= 0) && mRefreshState != RELEASE_TO_REFRESH) {
					mRefreshViewText.setText(hintRefreshLabel);
					mRefreshViewImage.clearAnimation();
					mRefreshViewImage.startAnimation(mFlipAnimation);
					mRefreshState = RELEASE_TO_REFRESH;
				} else if (mRefreshView.getBottom() < mRefreshViewHeight
						&& mRefreshState != PULL_TO_REFRESH) {
					if (mRefreshState != TAP_TO_REFRESH) {
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mReverseFlipAnimation);
					}
					mRefreshState = PULL_TO_REFRESH;
				}
			} else {
				setSelection(selected);
				resetHeader();
			}
		} else if (mCurrentScrollState == SCROLL_STATE_FLING
				&& firstVisibleItem == 0 && mRefreshState != REFRESHING) {
			setSelection(selected);
		}

		mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
				totalItemCount);
	}

	// 实现滚动监听器滚动状态改变时执行
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mCurrentScrollState = scrollState;
		mOnScrollListener.onScrollStateChanged(view, scrollState);
	}

	/**
	 * 处于刷新状态刷新
	 */
	public void Refreshing() {
		mRefreshView.setPadding(mRefreshView.getPaddingLeft(),
				mRefreshOriginalTopPadding, mRefreshView.getPaddingRight(),
				mRefreshView.getPaddingBottom());
		mRefreshViewImage.setVisibility(View.GONE);
		// 把图片设置为null,否则将保持之前的动画
		mRefreshViewImage.setImageDrawable(null);
		// 设置进度条可见
		mRefreshViewProgress.setVisibility(View.VISIBLE);
		// 设置正在刷新
		mRefreshViewText.setText(refreshingLabel);
		mRefreshState = REFRESHING;
	}

	/**
	 * 执行刷新
	 */
	public void onRefresh() {
		mOnRefreshListener.onRefresh();
	}

	/**
	 * 刷新完成--使ListView回到正常状态
	 * 
	 * @param lastUpdated
	 */
	public void onRefreshComplete(CharSequence lastUpdated) {
		// 使所有视图重建
		invalidateViews();
		resetHeader();
		setLastUpdated(lastUpdated);
		setSelection(selected);
	}

	/**
	 * 刷新完成后重置头部
	 */
	public void onRefreshComplete() {
		// 使所有视图重建
		invalidateViews();
		resetHeader();
		setLastUpdated(updateTime());
		setSelection(selected);
	}

	private String updateTime() {
		return "最近更新：" + format.format(new Date());
	}

	/**
	 * 执行单击刷新的监听器、用于事件回调
	 * 
	 * @author chengkai
	 */
	private class OnClickRefreshListener implements OnClickListener {
		public void onClick(View v) {
			if (mRefreshState != REFRESHING) {
				Refreshing();
				onRefresh();
			}
		}
	}

}
