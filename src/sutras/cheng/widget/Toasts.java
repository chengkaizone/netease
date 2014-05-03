package sutras.cheng.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义Toast类主要使用指定的背景---默认透明背景
 * 
 * @author chengkai
 * 
 */
public class Toasts {
	private Context mContext;
	private static Toast mToast;
	// 默认使用短提示
	public static int SHORT = Toast.LENGTH_SHORT;
	public static int LONG = Toast.LENGTH_LONG;
	// 出事背景色为透明
	private int colorBackground = Color.BLUE;
	private Drawable backDrawable;
	private int textColor = Color.RED;
	private float layer = 0.0f;
	private int padding = 5;
	private Method show, hide;
	private Field field;
	private Object obj;

	public void setColorBackground(int colorBackground) {
		this.colorBackground = colorBackground;
	}

	public void setBackDrawable(Drawable drawable) {
		this.backDrawable = drawable;
	}

	public void setBackDrawable(int resId) {
		this.backDrawable = mContext.getResources().getDrawable(resId);
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public Toasts(Context context) {
		mContext = context;
		mToast = new Toast(context);
		reflection();
	}

	public Toasts(Context context, String hint, boolean style) {
		mContext = context;
		if (style) {
			mToast = new Toast(context);
			mToast.setView(createLayout(context, hint));
		} else {
			mToast = Toast.makeText(context, hint, LONG);
		}
		reflection();
	}

	public Toasts(Context context, String hint, int resDrawable, boolean style) {
		if (style) {
			mToast = new Toast(context);
			mToast.setView(createLayout(context, hint, resDrawable));
		} else {
			mToast = Toast.makeText(context, hint, LONG);
		}
		reflection();
	}

	private View createLayout(Context context, String hint) {
		LinearLayout lay = new LinearLayout(context);
		lay.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		lay.setFadingEdgeLength(10);
		lay.setBackgroundColor(colorBackground);
		if (backDrawable != null) {
			lay.setBackgroundDrawable(backDrawable);
		}
		TextView text = new TextView(context);
		int showColor = Color.BLACK;
		text.setTextColor(textColor);
		text.setTextAppearance(context, android.R.style.TextAppearance_Small);
		text.setPadding(padding, padding, padding, padding);
		text.setShadowLayer(layer, layer, layer, showColor);
		text.setTypeface(Typeface.DEFAULT_BOLD);
		text.setText(hint);
		lay.addView(text);
		return lay;
	}

	private View createLayout(Context context, String hint, int resDrawable) {
		LinearLayout lay = new LinearLayout(context);
		lay.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		lay.setFadingEdgeLength(10);
		lay.setBackgroundResource(resDrawable);
		TextView text = new TextView(context);
		int showColor = Color.BLACK;
		text.setTextColor(textColor);
		text.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		text.setPadding(padding, padding, padding, padding);
		text.setShadowLayer(layer, layer, layer, showColor);
		text.setText(hint);
		lay.addView(text);
		return lay;
	}

	public static void showHint(Context context, String hint, int duration) {
		Toasts t = new Toasts(context);
		mToast.setDuration(duration);
		mToast.setView(t.createLayout(context, hint));
		mToast.show();
	}

	public static void showHint(Context context, String hint) {
		Toasts t = new Toasts(context);
		mToast.setDuration(SHORT);
		mToast.setView(t.createLayout(context, hint));
		mToast.show();
	}

	public static void showHintColor(Context context, String hint, int bgColor,
			int textColor) {
		Toasts t = new Toasts(context);
		t.setColorBackground(bgColor);
		t.setTextColor(textColor);
		mToast.setDuration(SHORT);
		mToast.setView(t.createLayout(context, hint));
		mToast.show();
	}

	public static void showHint(Context context, String hint, int drawable,
			int textColor) {
		Toasts t = new Toasts(context);
		t.setBackDrawable(drawable);
		t.setTextColor(textColor);
		mToast.setDuration(SHORT);
		mToast.setView(t.createLayout(context, hint, drawable));
		mToast.show();
	}

	/**
	 * 加入消息队列
	 * 
	 * @return
	 */
	public Toasts show() {
		try {
			show.invoke(obj, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 退出消息队列
	 * 
	 * @return
	 */
	public Toasts hide() {
		try {
			hide.invoke(obj, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 通过反射获取不可访问的对象和方法
	 */
	private void reflection() {
		try {
			field = mToast.getClass().getDeclaredField("mTN");
			field.setAccessible(true);
			obj = field.get(mToast);
			show = obj.getClass().getDeclaredMethod("show", null);
			hide = obj.getClass().getDeclaredMethod("hide", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
