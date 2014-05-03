package sutras.cheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PageClickView extends ImageView implements
		GestureDetector.OnGestureListener {
	private GestureDetector mDetector;
	private PerformClick mPerformClick;

	public PageClickView(Context context) {
		super(context);
		init(context);
	}

	public PageClickView(Context context, AttributeSet attr) {
		super(context, attr);
		init(context);
	}

	public PageClickView(Context context, AttributeSet attr, int style) {
		super(context, attr, style);
		init(context);
	}

	private void init(Context context) {
		this.mDetector = new GestureDetector(this);
	}

	private void removePerformClick() {
		if (this.mPerformClick != null) {
			this.mPerformClick = null;
		}
	}

	public boolean onSingleTapUp(MotionEvent event) {
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case 0:
			this.mPerformClick = new PerformClick();
			setPressed(true);
			break;
		case 1:
		case 3:
			if (this.mPerformClick != null) {
				post(this.mPerformClick);
				this.mPerformClick = null;
			}
			setPressed(false);
		case 2:
		}
		return true;
	}

	private class PerformClick implements Runnable {
		private PerformClick() {
		}

		public void run() {
			PageClickView.this.performClick();
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		removePerformClick();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		removePerformClick();
		return true;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent event) {
	}

	@Override
	public void onShowPress(MotionEvent event) {
	}
}