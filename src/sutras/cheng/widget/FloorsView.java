package sutras.cheng.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class FloorsView extends LinearLayout {
	private Rect mClipRect = new Rect();
	private boolean mDataChanged;
	private FloorBinder mFloorBinder;
	private Object mFloorsData;
	private int mPadding;
	private Paint mRectColorPaint = new Paint();
	private RectF mRectF = new RectF();
	private Paint mRectStrokePaint = new Paint();
	private float mStrokeWidth;
	private FloorViewHolder mViewHolder;

	public FloorsView(Context context) {
		super(context);
		init(context);
	}

	public FloorsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private FloorView getFloorView() {
		FloorView floorView = this.mViewHolder.getFloorView(this);
		if (floorView == null) {
			floorView = new FloorView(getContext(), this);
			View localView = this.mFloorBinder.createFloorContentView(
					this.mFloorsData, floorView);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) localView
					.getLayoutParams();
			if (params == null)
				params = new FrameLayout.LayoutParams(-1, -2);
			floorView.addView(localView, params);
		}
		return floorView;
	}

	private void init(Context context) {
		setOrientation(1);
		float f = context.getResources().getDisplayMetrics().density;
		this.mPadding = (int) (2.0F * f);
		this.mStrokeWidth = (0.5F * f);
		this.mRectStrokePaint.setStrokeWidth(this.mStrokeWidth);
		this.mRectStrokePaint.setStyle(Paint.Style.STROKE);
	}

	protected void dispatchDraw(Canvas canvas) {
		int i = getChildCount();
		if (i > 0) {
			if (!canvas.getClipBounds(this.mClipRect))
				this.mClipRect.setEmpty();
			View localView2 = getChildAt(i - 1);
			this.mRectF.set(localView2.getLeft(), localView2.getLeft(),
					localView2.getRight(), localView2.getBottom());
			canvas.drawRect(this.mRectF, this.mRectColorPaint);
		}
		super.dispatchDraw(canvas);
		if (i <= 0)
			return;
		for (int j = i - 1; j >= 0; --j) {
			View localView1 = getChildAt(j);
			this.mRectF.set(localView1.getLeft() + this.mStrokeWidth,
					localView1.getLeft() + this.mStrokeWidth,
					localView1.getRight() - this.mStrokeWidth,
					localView1.getBottom() - this.mStrokeWidth);
			canvas.drawRect(this.mRectF, this.mRectStrokePaint);
		}
	}

	protected boolean drawChild(Canvas canvas, View paramView, long paramLong) {
		boolean bool;
		if ((this.mClipRect.top < paramView.getBottom())
				&& (this.mClipRect.bottom > paramView.getTop()))
			bool = super.drawChild(canvas, paramView, paramLong);
		else
			bool = false;
		return bool;
	}

	public int getCount() {
		int i;
		if (this.mFloorBinder != null)
			i = this.mFloorBinder.getFloorCount(this.mFloorsData);
		else
			i = 0;
		return i;
	}

	public FloorViewHolder getFloorViewHolder() {
		return this.mViewHolder;
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (this.mDataChanged) {
			this.mDataChanged = false;
		}
		super.onLayout(changed, l, t, r, b);
	}

	public void setFloorBinder(FloorBinder floorBinder) {
		this.mFloorBinder = floorBinder;
	}

	public void setFloorColor(int color) {
		this.mRectColorPaint.setColor(color);
		invalidate();
	}

	public void setFloorStrokeColor(int color) {
		this.mRectStrokePaint.setColor(color);
		invalidate();
	}

	public void setFloorViewHolder(FloorViewHolder holder) {
		this.mViewHolder = holder;
	}

	public void setFloorsValue(Object obj) {
		this.mFloorsData = obj;
		this.mDataChanged = true;
		if (this.mViewHolder == null)
			this.mViewHolder = new FloorViewHolder();
		this.mViewHolder.recycleFloorView(this);
		int k = getCount();
		if ((this.mFloorBinder == null) || (k <= 0))
			return;
		for (int j = 0; j < k; ++j) {
			FloorView floorView = getFloorView();
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) floorView
					.getLayoutParams();
			if (params != null) {
				attachViewToParent(floorView, j, params);
			} else {
				params = new LinearLayout.LayoutParams(-1, -2);
				params.gravity = 49;
				addViewInLayout(floorView, j, params);
			}
			int i = Math.min(-1 + (k - j), 4) * this.mPadding;
			params.leftMargin = i;
			params.rightMargin = i;
			if (j != 0)
				params.topMargin = 0;
			else
				params.topMargin = (Math.min(k, 4) * this.mPadding);
			this.mFloorBinder.bindFloor(floorView, j, this.mFloorsData, k);
		}
	}

	public static abstract interface FloorBinder {
		public abstract void bindFloor(ViewGroup viewGroup, int paramInt1,
				Object obj, int paramInt2);

		public abstract View createFloorContentView(Object obj,
				ViewGroup viewGroup);

		public abstract int getFloorCount(Object obj);
	}

	private static class FloorView extends FrameLayout {
		public FloorView(Context context, FloorsView floorsView) {
			super(context);
		}
	}

	public static class FloorViewHolder {
		private ArrayList<FloorsView.FloorView> mFloorCache = new ArrayList<FloorsView.FloorView>();

		public FloorsView.FloorView getFloorView(FloorsView floorsView) {
			FloorsView.FloorView floorView;
			if (this.mFloorCache.size() <= 0)
				floorView = null;
			else
				floorView = (FloorsView.FloorView) this.mFloorCache.remove(0);
			return floorView;
		}

		public void recycleFloorView(FloorsView floorsView) {
			for (int i = -1 + floorsView.getChildCount(); i >= 0; --i) {
				FloorsView.FloorView floorView = (FloorsView.FloorView) floorsView
						.getChildAt(i);
				this.mFloorCache.add(floorView);
			}
			floorsView.detachAllViewsFromParent();
		}
	}
}