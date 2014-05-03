package sutras.cheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class HorizontalScrollBar extends HorizontalScrollView {
	private View mLeftView;
	private View mRightView;

	public HorizontalScrollBar(Context context) {
		super(context);
	}

	public HorizontalScrollBar(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public HorizontalScrollBar(Context context, AttributeSet attr, int style) {
		super(context, attr, style);
	}

	private void checkScrollX() {
		View view = getChildAt(0);
		if (view != null) {
			int i = getScrollX();
			if (i > 0)
				setViewVisibility(this.mLeftView, 0);
			else
				setViewVisibility(this.mLeftView, 4);
			if (i < view.getRight() + getPaddingRight() - getWidth())
				setViewVisibility(this.mRightView, 0);
			else
				setViewVisibility(this.mRightView, 4);
		} else {
			setViewVisibility(this.mLeftView, 4);
			setViewVisibility(this.mRightView, 4);
		}
	}

	private void setViewVisibility(View view, int visible) {
		if (view != null) {
			view.setVisibility(visible);
		}
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		checkScrollX();
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		checkScrollX();
	}

	public void scrollToLeft() {
		scrollTo(0, 0);
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
						Math.min(tGroup.getChildAt(position).getLeft(),
								tGroup.getRight() - getWidth()), 0);
			} else {
				scrollToRight();
			}
		} else {
			scrollToLeft();
		}
	}

	public void scrollToRight() {
		View view = getChildAt(0);
		if (view != null) {
			scrollTo(view.getRight() + getPaddingRight() - getWidth(), 0);
		}
	}

	public void setLeftView(View leftView) {
		this.mLeftView = leftView;
	}

	public void setRightView(View rightView) {
		this.mRightView = rightView;
	}
}