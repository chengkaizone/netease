package sutras.cheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class VerticalScrollBar extends ScrollView {
	// 顶部视图
	private View mBottomView;
	// 底部视图
	private View mTopView;

	public VerticalScrollBar(Context context) {
		super(context);
	}

	public VerticalScrollBar(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public VerticalScrollBar(Context context, AttributeSet attr, int style) {
		super(context, attr, style);
	}

	private void checkScrollY() {
		View view = getChildAt(0);
		if (view != null) {
			int i = getScrollY();
			if (i > 0) {
				setViewVisibility(this.mTopView, 0);
			} else {
				setViewVisibility(this.mTopView, 4);
			}
			if (i < view.getBottom() + getPaddingBottom() - getHeight()) {
				setViewVisibility(this.mBottomView, 0);
			} else {
				setViewVisibility(this.mBottomView, 4);
			}
		} else {
			setViewVisibility(this.mTopView, 4);
			setViewVisibility(this.mBottomView, 4);
		}
	}

	private void setViewVisibility(View view, int visible) {
		if (view == null) {
			return;
		}
		view.setVisibility(visible);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		checkScrollY();
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		checkScrollY();
	}

	public void scrollToBottom() {
		View view = getChildAt(0);
		if (view != null) {
			scrollTo(0, view.getBottom() + getPaddingBottom() - getHeight());
		}
	}

	public void scrollToPosition(int position) {
		ViewGroup tGroup = (ViewGroup) getChildAt(0);
		if ((tGroup == null) || (position < 0)
				|| (position >= tGroup.getChildCount())) {
			return;
		}
		if (position != 0) {
			if (position != -1 + tGroup.getChildCount()) {
				scrollTo(
						0,
						Math.min(tGroup.getChildAt(position).getTop(),
								tGroup.getBottom() - getHeight()));
			} else {
				scrollToBottom();
			}
		} else {
			scrollToTop();
		}
	}

	public void scrollToTop() {
		scrollTo(0, 0);
	}

	public void setBottomView(View footView) {
		this.mBottomView = footView;
	}

	public void setTopView(View topView) {
		this.mTopView = topView;
	}
}