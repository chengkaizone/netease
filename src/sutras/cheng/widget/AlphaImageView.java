package sutras.cheng.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 注意声明的自定义属性的命名空间必须是安装应用的包名；否则无法找到该属性;透明度最大为255
 * 
 * @author chengkai
 * 
 */
public class AlphaImageView extends ImageView {
	// 需要判断是否加入线程；
	private final int YES = 1;
	// 不需要判断
	private final int NO = -1;
	private int curState = YES;
	// 图片每次要改变的透明度等级
	private int alphaLevel = 10;
	// 当前透明度
	private int curAlpha = 100;
	// 最小透明度
	private int minAlpha = 100;
	// 最大透明度
	private int maxAlpha = 240;
	// 定时的速度0.5秒改变一次透明度
	private int speed = 500;
	// 来回更改透明度的标志
	private boolean flag = true;
	// 定时改变透明度是否停止
	private boolean isStop = false;
	// 时间为5秒
	private int duration = 5000;
	Handler handler = new Handler();

	public AlphaImageView(Context context) {
		this(context, null);
	}

	public AlphaImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 加载属性数组
		// TypedArray
		// typedArray=context.obtainStyledAttributes(attrs,R.styleable.AlphaImageView);
		// //获取duration 参数--默认为1
		// int
		// duration=typedArray.getInt(R.styleable.AlphaImageView_duration,1);
		// 计算初始透明度--每次改变的大小
		alphaLevel = maxAlpha * speed / duration;
	}

	public AlphaImageView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	private Runnable run = new Runnable() {
		public void run() {
			if (flag) {
				curAlpha += alphaLevel;
			} else {
				curAlpha -= alphaLevel;
			}
			if (curAlpha >= maxAlpha) {
				flag = false;
			} else if (curAlpha <= minAlpha) {
				flag = true;
			}
			AlphaImageView.this.invalidate();// 这一步一直在通知改变
			handler.postDelayed(this, speed);
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		if (curState == YES) {
			if (!isStop) {
				// 如果未停止执行线程
				handler.post(run);
				curState = NO;
			} else {
				handler.removeCallbacks(run);
			}
		}
		this.setAlpha(curAlpha);
		super.onDraw(canvas);
	}

	public int getMinAlpha() {
		return minAlpha;
	}

	public void setMinAlpha(int minAlpha) {
		this.minAlpha = minAlpha;
	}

	public int getMaxAlpha() {
		return maxAlpha;
	}

	public void setMaxAlpha(int maxAlpha) {
		this.maxAlpha = maxAlpha;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getAlphaLevel() {
		return alphaLevel;
	}

	public void setAlphaLevel(int alphaLevel) {
		this.alphaLevel = alphaLevel;
		this.invalidate();
	}

	public int getCurAlpha() {
		return curAlpha;
	}

	public void setCurAlpha(int curAlpha) {
		this.curAlpha = curAlpha;
		this.invalidate();
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		this.invalidate();
	}

	public boolean isStop() {
		return isStop;
	}

	public void stop() {
		curState = YES;
		// 设置不透明
		this.curAlpha = maxAlpha;
		this.isStop = true;
		this.invalidate();
	}

	public void start() {
		curState = YES;
		isStop = false;
		this.invalidate();
	}

}
