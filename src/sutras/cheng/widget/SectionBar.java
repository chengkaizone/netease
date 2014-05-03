package sutras.cheng.widget;

import sutras.cheng.listener.SectionListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 触摸选项条；用于快速定位
 * 
 * @author chengkai
 * 
 */
public class SectionBar extends View {
	// 字符数组资源--用于快速定位
	private char[] crr;
	// 组件所在的上下文
	private Context context;
	// 每一个字母所在的像素值
	private int barHeight;
	// 提示点击的字母容器
	private View container;
	// 用于提示点击的字母
	private TextView hint;
	// 是否提示点击项
	private boolean isHint = false;
	// 控件的背景颜色
	private int backgroundColor = 0x00000000;
	// 画笔颜色--决定字母显示颜色
	private int paintColor = 0xff000000;
	// 设置画出的字符大小
	private int textSize = 12;
	// 底部缝隙
	private int space = 1;
	// 需要在主线程中实现的监听器
	private SectionListener mListener = new SectionListener() {
		public void doPosition(String str) {
		}
	};

	public SectionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SectionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SectionBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		crr = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z' };
		setBackgroundColor(backgroundColor);
	}

	public void setHint(boolean isHint) {
		this.isHint = isHint;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
	}

	// 初始化提示组件有提示效果
	public void initHintContainer(View container, TextView hint) {
		this.container = container;
		this.hint = hint;
		isHint = true;
	}

	// 初始化提示组件有提示效果
	public void initHintContainer(int resLay, int resText) {
		this.container = ((Activity) context).findViewById(resLay);
		this.hint = (TextView) ((Activity) context).findViewById(resText);
		isHint = true;
	}

	public void setSectionListener(SectionListener listener) {
		this.mListener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		barHeight = (getHeight() - space) / crr.length;
		super.onLayout(changed, left, top, right, bottom);
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = i / barHeight;
		if (idx >= crr.length) {
			idx = crr.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (isHint) {
				container.setVisibility(View.VISIBLE);
				hint.setText(crr[idx] + "");
			}
			// 执行回调方法
			mListener.doPosition(crr[idx] + "");
			break;
		case MotionEvent.ACTION_UP:
			if (isHint) {
				container.setVisibility(View.GONE);
			}
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(paintColor);
		paint.setTextSize(textSize);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < crr.length; i++) {
			canvas.drawText(String.valueOf(crr[i]), widthCenter, barHeight
					+ (i * barHeight), paint);
		}
		super.onDraw(canvas);
	}

}
