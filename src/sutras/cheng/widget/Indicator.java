package sutras.cheng.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 封装的指示器---只是一个布局---主要靠动态添加ImageView实现
 * 
 * @author chengkai
 * 
 */
public class Indicator extends LinearLayout {
	// 指示器方向---水平
	public static final int HORIZONTAL = 0;
	// 指示器方向---垂直
	public static final int VERTICAL = 1;
	// 默认缩放比率
	public static final float DEFAULT_RATE = 1.2f;
	// 一般模型
	public static final int PLAIN_MODE = 0;
	// 缩放模型
	public static final int SCALE_MODE = 1;
	// 滤镜模型
	public static final int ALPHA_MODE = 2;
	// 默认水平方向
	private int orientation = HORIZONTAL;
	private boolean isFix = false;
	private Context context;
	private int mode = PLAIN_MODE;
	private int points;
	private int curPoint;
	private Bitmap src, des;
	private float scaleRate = DEFAULT_RATE;
	private ImageView[] images;
	private int space = 0;
	private int imageWidth, imageHeight;

	public Indicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setGravity(Gravity.CENTER);
		this.desPoint(0);
	}

	public Indicator(Context context) {
		super(context);
		this.context = context;
		this.setGravity(Gravity.CENTER);
		this.desPoint(0);
	}

	public void init(Bitmap src) {
		this.mode = SCALE_MODE;
		if (isFix) {
			this.src = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
			this.des = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
		} else {
			this.src = Bitmap.createScaledBitmap(src,
					(int) (src.getWidth() * scaleRate),
					(int) (src.getHeight() * scaleRate), true);
			this.des = Bitmap.createScaledBitmap(src,
					(int) (src.getWidth() * scaleRate),
					(int) (src.getHeight() * scaleRate), true);
		}
	}

	public void init(int srcid) {
		this.mode = SCALE_MODE;
		this.src = BitmapFactory.decodeResource(getResources(), srcid);
		if (isFix) {
			this.src = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
			this.des = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
		} else {
			this.src = Bitmap.createScaledBitmap(src,
					(int) (src.getWidth() * scaleRate),
					(int) (src.getHeight() * scaleRate), true);
			this.des = Bitmap.createScaledBitmap(src,
					(int) (src.getWidth() * scaleRate),
					(int) (src.getHeight() * scaleRate), true);
		}
	}

	public void init(Bitmap src, Bitmap des) {
		this.mode = PLAIN_MODE;
		this.src = src;
		this.des = des;
		if (isFix) {
			this.src = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
			this.des = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
		}
	}

	public void init(int srcid, int desid) {
		this.mode = PLAIN_MODE;
		this.src = BitmapFactory.decodeResource(getResources(), srcid);
		this.des = BitmapFactory.decodeResource(getResources(), desid);
		if (isFix) {
			this.src = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
			this.des = Bitmap.createScaledBitmap(src, imageWidth, imageHeight,
					true);
		}
	}

	public void initWH(int width, int height) {
		this.imageWidth = width;
		this.imageHeight = height;
		this.isFix = true;
	}

	public int getPoints() {
		return points;
	}

	public void desPoint(int loc) {
		if (loc > points) {
			loc = points;
		} else if (loc < 0) {
			loc = 0;
		}
		for (int i = 0; i < points; i++) {
			if (i == loc) {
				images[i].setImageBitmap(des);
			} else {
				images[i].setImageBitmap(src);
			}
		}
	}

	// 这一步初始化控件
	public void setPoints(int points) {
		this.points = points;
		images = new ImageView[points];
		for (int i = 0; i < points; i++) {
			ImageView child = new ImageView(context);
			child.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			if (this.orientation == 0) {
				child.setPadding(space, 0, space, 0);
			} else {
				child.setPadding(0, space, 0, space);
			}
			child.setImageBitmap(src);
			images[i] = child;
			this.addView(child);
		}
	}

	public void setPoints(int points, int space) {
		this.setSpace(space);
		this.setPoints(points);
	}

	public int getCurPoint() {
		return curPoint;
	}

	public void setCurPoint(int curPoint) {
		this.curPoint = curPoint;
	}

	public Bitmap getDes() {
		return des;
	}

	public void setDes(Bitmap des) {
		this.des = des;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		super.setOrientation(orientation);
		this.orientation = orientation;
	}

	public int getSpace() {
		return space;
	}

	public float getScaleRate() {
		return scaleRate;
	}

	public void setScaleRate(float scaleRate) {
		this.scaleRate = scaleRate;
		des = Bitmap.createScaledBitmap(src,
				(int) (src.getWidth() * scaleRate),
				(int) (src.getHeight() * scaleRate), true);
	}

	public void setSpace(int space) {
		if (space < 0) {
			this.space = 0;
		} else {
			this.space = space;
		}
	}
}
