package sutras.cheng.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 多点触控的ImageView
 * 
 * @author chengkai
 * 
 */
public class MultiImageView extends ImageView {
	private Matrix matrix;
	private Matrix savePreMatrix;
	private PointF start;// 第一个触摸点
	private PointF mid;
	private float oldDist = 1.0f;
	// 图片的三种状态
	final int NONE = 0;// 无状态
	final int DRAG = 1;// 拖曳状态
	final int ZOOM = 2;// 缩放状态
	int MODE = NONE;

	public MultiImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MultiImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		matrix = new Matrix();
		savePreMatrix = new Matrix();
		start = new PointF();
		mid = new PointF();
	}

	// 只处理的多点
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		// 单点按下时触发此事件
		case MotionEvent.ACTION_DOWN:
			savePreMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			MODE = DRAG;// 转换为拖曳状态
			break;
		case MotionEvent.ACTION_MOVE:
			if (MODE == DRAG) {
				matrix.set(savePreMatrix);
				// 水平移动
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (MODE == ZOOM) {
				float newDist = calDis(event);
				float scale = newDist / oldDist;
				matrix.set(savePreMatrix);
				matrix.postScale(scale, scale, mid.x, mid.y);
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = calDis(event);// 计算两点之间的距离
			savePreMatrix.set(matrix);
			mid = this.getMidPoint(mid, event);
			MODE = ZOOM;// 转换为缩放状态
			break;
		// 同时处理这两种状态
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			MODE = NONE;
			break;
		}
		this.setImageMatrix(matrix);
		return true;
	}

	// 计算两点之间的距离
	private float calDis(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算两点的中心点
	private PointF getMidPoint(PointF p, MotionEvent event) {
		float midX = (event.getX(0) + event.getX(1)) / 2;
		float midY = (event.getY(0) + event.getY(1)) / 2;
		p.set(midX, midY);
		return p;
	}
}
