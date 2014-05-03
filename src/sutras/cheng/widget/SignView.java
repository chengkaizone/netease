package sutras.cheng.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 签名控件--注意一不能调用画布获取宽高的方法---否则程序会崩溃
 * 
 * @author chengkai
 * 
 */
public class SignView extends View {
	// 默认透明纸板
	private int paperColor = Color.argb(0, 0, 0, 0);
	private float preX;
	private float preY;
	private Path path;
	private Paint cachePaint;
	public Bitmap cacheBitmap;
	public Canvas cacheCanvas;
	private boolean isInit = true;

	public SignView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SignView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SignView(Context context) {
		super(context);
	}

	/**
	 * 清理签名板
	 * 
	 * @param context
	 */
	public void initParam() {
		// 初始化画布
		cacheCanvas = new Canvas();
		// 256色位图---高质量位图
		cacheBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
				Config.ARGB_8888);
		path = new Path();
		// 设置画布绘制到内存的cacheBitmap上
		cacheCanvas.setBitmap(cacheBitmap);
		// 设置画布颜色
		cacheCanvas.drawColor(paperColor);
		//
		cachePaint = new Paint(Paint.DITHER_FLAG);
		// 画笔颜色
		cachePaint.setColor(Color.RED);
		// 设置画笔宽度
		cachePaint.setStrokeWidth(1.0f);
		// 画笔风格--抖动
		cachePaint.setStyle(Paint.Style.STROKE);
		// 设置抗锯齿
		cachePaint.setAntiAlias(true);
		cachePaint.setDither(true);
		this.invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float pointX = event.getX();
		float pointY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(pointX, pointY);
			preX = pointX;
			preY = pointY;
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(preX, preY, pointX, pointY);
			preX = pointX;
			preY = pointY;
			break;
		case MotionEvent.ACTION_UP:
			// 缓存画布画路径
			cacheCanvas.drawPath(path, cachePaint);
			// 重设路径
			path.reset();
			break;
		}
		this.invalidate();// 通知组件绘图
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isInit) {
			// 创建矩形类
			Rect rect = new Rect();
			Rect rect2 = new Rect();
			// 将自己的宽高赋值给矩形
			this.getDrawingRect(rect2);
			// 将屏幕宽高分布赋值给矩形类
			this.getWindowVisibleDisplayFrame(rect);
			// int tmpH = rect.height();// 应用程序层高度
			// int tmpT = rect.top;// 状态栏高度
			// 只有当onDraw方法被回调后高度和宽度才被计算
			// int tmpH2 = rect2.height();// 自己被计算出的高度
			System.out.println(this.getWidth() + "<<<>>>" + this.getHeight());
			initParam();
			isInit = false;
		}
		// 设置临时画布的颜色
		// canvas.drawColor(paperColor);
		// canvas.drawBitmap(defaultBitmap, 0, 0,null);
		Paint tmpPaint = new Paint();// 创建默认画笔
		// 将缓存位图绘制到View控件上
		canvas.drawBitmap(cacheBitmap, 0, 0, tmpPaint);
		// 沿着路径画
		canvas.drawPath(path, cachePaint);

	}

	/**
	 * 使用颜色为签名背景
	 * 
	 * @param color
	 */
	public void setPaperColor(int color) {
		this.paperColor = color;
		Canvas canvas = new Canvas();
		// 必须在先画位图后设置画布颜色；初始化画布是没有大小的；画好位图后才有大小
		cacheCanvas = canvas;// 将缓存画布重新赋值；清除以前的图像
		cacheCanvas.setBitmap(cacheBitmap);// 执行这一步是为了为画布指定区域
		cacheCanvas.drawColor(paperColor);// 为缓存画布设置颜色
		cacheCanvas.setBitmap(cacheBitmap);// 再保存
		cacheCanvas.save(Canvas.ALL_SAVE_FLAG);// 保存所有图层
	}

	/**
	 * 使用位图为签名背景
	 * 
	 * @param bitmap
	 */
	public void setPaperBg(Bitmap bitmap) {
		Canvas canvas = new Canvas();
		cacheCanvas = canvas;
		canvas.drawBitmap(bitmap, 0, 0, null);
		cacheCanvas.setBitmap(cacheBitmap);
		cacheCanvas.save(Canvas.ALL_SAVE_FLAG);
	}

	/**
	 * 返回缓存的位图对象
	 * 
	 * @return
	 */
	public Bitmap getBitmap() {
		return cacheBitmap;
	}

	/**
	 * 设置缓存画笔
	 * 
	 * @return
	 */
	public Paint getCachePaint() {
		return cachePaint;
	}

	/**
	 * 获取缓存画笔
	 * 
	 * @param cachePaint
	 */
	public void setCachePaint(Paint cachePaint) {
		this.cachePaint = cachePaint;
	}

}
