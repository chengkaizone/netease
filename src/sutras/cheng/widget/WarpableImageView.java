package sutras.cheng.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 可扭曲的组件
 * 
 * @author chengkai
 */
public class WarpableImageView extends ImageView {
	private Bitmap bitmap;
	private int bWidth, bHeight;
	private int meshX = 20, meshY = 20;
	private int count = (meshX + 1) * (meshY + 1);
	private float[] orign = new float[count * 2];
	private float[] verts = new float[count * 2];

	public WarpableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParam(context);
	}

	public WarpableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParam(context);
	}

	public WarpableImageView(Context context) {
		super(context);
		initParam(context);
	}

	// 初始化方法
	private void initParam(Context context) {
		// 设置可以获得焦点
		this.setFocusable(true);
		// 初始化坐标值---x/y的值可以分开初始化 单数保存y坐标数据--双数保存x坐标数据
		int index = 0;
		for (int i = 0; i <= meshY; i++) {
			// 循环初始化y坐标值
			float y = i * bHeight / meshY;
			for (int j = 0; j <= meshX; j++) {
				// 循环初始化x坐标值
				float x = j * bWidth / meshX;
				// 设置数组中单数索引为y方向坐标值--双数为x坐标值
				orign[index * 2 + 0] = verts[index * 2 + 0] = x;
				orign[index * 2 + 1] = verts[index * 2 + 1] = y;
				index++;
			}
		}
		this.setBackgroundColor(Color.WHITE);
		bitmap = getBitmap();
		bWidth = bitmap.getWidth();
		bHeight = bitmap.getHeight();
	}

	// 工具方法--根据触摸点的坐标重新计算数组
	public void handlePoint(float cx, float cy) {
		for (int i = 0; i < count * 2; i += 2) {
			float calX = cx - orign[i + 0];
			float calY = cy - orign[i + 1];
			// 计算每个坐标与当前点的距离--坐标法计算
			float area = calX * calX + calY * calY;
			float distance = (float) Math.sqrt(area);
			// 计算扭曲度--距离越远；扭曲越小--此算法自定；参数自己定
			float pull = 80000 / ((float) (area * distance));
			// 对数组坐标重新赋值
			if (pull >= 1) {
				verts[i + 0] = cx;
				verts[i + 1] = cy;
			} else {
				// 控制各顶点向触摸点偏移
				verts[i + 0] = orign[i + 0] + calX * pull;
				verts[i + 1] = orign[i + 1] + calY * pull;
			}
		}
		this.invalidate();
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		bWidth = bitmap.getWidth();
		bHeight = bitmap.getHeight();
	}

	private Bitmap getBitmap() {
		return ((BitmapDrawable) this.getDrawable()).getBitmap();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmapMesh(bitmap, meshX, meshY, verts, 0, null, 0, null);
	}

	// 如果主线程在添加触摸监听；此方法不会响应
	public boolean onTouchEvent(MotionEvent event) {
		handlePoint(event.getX(), event.getY());
		return true;// 父类方法一般返回false;此处一定要明确返回true表示已处理
	}
}
