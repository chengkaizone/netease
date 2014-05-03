package sutras.cheng.widget;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

public class MenuPopup extends PopupWindow {
	private ContentFrame mContent;
	private View contentView;
	private OnTouchListener mTouchInterceptor;
	private Activity mAct;
	private MenuCallback mMenuCallback;
	private OnClickListener mOnClickListener;
	// 菜单高度
	private int height = 80;

	public MenuPopup(Activity act, int width, int height, int animationStyle) {
		super(act);
		this.mAct = act;
		setAnimationStyle(animationStyle);
		this.mContent = new ContentFrame(act);
		setContentView(this.mContent);
		setWidth(width);
		setHeight(height);
		setFocusable(true);
		setBackgroundDrawable(null);
		update();
	}

	private void callMenuCallbackClose() {
		if (this.mMenuCallback != null) {
			this.mMenuCallback.onMenuClose();
		}
	}

	private boolean callMenuCallbackOpen() {
		if (this.mMenuCallback != null) {
			return this.mMenuCallback.onMenuOpen();
		}
		return true;
	}

	protected boolean checkDismissKey(int key) {
		if ((key == 4) || (key == 82)) {
			return true;
		}
		return false;
	}

	protected boolean checkShowKey(int key) {
		if (key != 82) {
			return false;
		}
		return true;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		int i = 1;
		int k = event.getKeyCode();
		int j = event.getAction();
		if (!isShowing()) {
			if (!checkShowKey(k)) {
				i = 0;
			} else if ((j == i) && (callMenuCallbackOpen()))
				contentView.post(new Runnable() {
					public void run() {
						MenuPopup.this
								.showAtLocation(contentView, height, 0, 0);
					}
				});
		} else if (!checkDismissKey(k)) {
			i = 0;
		} else if (j == i) {
			callMenuCallbackClose();
			dismiss();
		}
		if (i == 0) {
			return false;
		}
		return true;
	}

	public void setMenuCallback(MenuCallback callback) {
		this.mMenuCallback = callback;
	}

	/**
	 * 为每一个控件设置监听
	 * 
	 * @param listener
	 * @param resIds
	 */
	public void setOnClickListener(OnClickListener listener, int[] resIds) {
		this.mOnClickListener = listener;
		contentView = getContentView();
		if ((contentView == null) || (resIds == null) || (resIds.length <= 0)) {
			return;
		}
		int j = resIds.length;
		for (int i = 0; i < j; ++i) {
			contentView.findViewById(resIds[i]).setOnClickListener(
					this.mOnClickListener);
		}
	}

	protected void onOutSideTouched(MotionEvent event) {
		dismiss();
	}

	public View getContentView() {
		return this.mContent.getChildAt(0);
	}

	private void setContentViewInternal(View internalView) {
		if (this.mContent != internalView) {
			this.mContent.removeAllViews();
			if (internalView != null) {
				this.mContent.addView(internalView);
			}
			update();
		} else {
			super.setContentView(internalView);
		}
	}

	public void setContentView(int layout) {
		if (layout != 0) {
			contentView = LayoutInflater.from(this.mAct).inflate(layout, null);
			setContentViewInternal(contentView);
		}
	}

	public void setContentView(View contentView) {
		setContentViewInternal(contentView);
	}

	public void setTouchInterceptor(View.OnTouchListener listener) {
		this.mTouchInterceptor = listener;
	}

	private class ContentFrame extends FrameLayout {
		public ContentFrame(Context context) {
			super(context);
		}

		public boolean dispatchKeyEvent(KeyEvent event) {
			if (!MenuPopup.this.dispatchKeyEvent(event)) {
				return super.dispatchKeyEvent(event);
			}
			return true;
		}

		public boolean dispatchTouchEvent(MotionEvent event) {
			if ((MenuPopup.this.mTouchInterceptor == null)
					|| (!MenuPopup.this.mTouchInterceptor.onTouch(this, event))) {
				return super.dispatchTouchEvent(event);
			}
			return true;
		}

		public boolean onTouchEvent(MotionEvent event) {
			boolean bool = true;
			int j = (int) event.getX();
			int i = (int) event.getY();
			if ((event.getAction() != 0)
					|| ((j >= 0) && (j < getWidth()) && (i >= 0) && (i < getHeight()))) {
				if (event.getAction() != 4) {
					bool = super.onTouchEvent(event);
				} else {
					MenuPopup.this.onOutSideTouched(event);
				}
			} else {
				MenuPopup.this.onOutSideTouched(event);
			}
			return bool;
		}
	}

	public interface MenuCallback {
		public void onMenuClose();

		public boolean onMenuOpen();
	}
}