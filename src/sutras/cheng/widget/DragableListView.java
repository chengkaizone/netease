package sutras.cheng.widget;

import java.util.ArrayList;
import java.util.List;

import sutras.cheng.listener.OnDragListViewListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 可拖拉改变的ListView
 * 
 * @author chengkai
 * 
 */
public class DragableListView extends ListView {
	// android 窗口管理器
	private WindowManager mWindowManager;
	// 窗口布局参数
	private WindowManager.LayoutParams mWindowParams;
	// 手机屏幕的宽度
	private int screenWidth;
	// 显示拖曳时背景的控件---该控件上的内容在处于拖曳状态时会截取到一个矩形区域中
	private ImageView mDragView;
	// 显示拖曳的临时矩形；拖曳时将ImageView控件上的内容截取到该矩形上
	private Rect mTempRect = new Rect();
	// 显示拖曳时的位图
	private Bitmap mDragBitmap;
	// 垃圾图片
	private Drawable mTrashcan;
	// 拖曳时的背景图片
	private Drawable drawable;
	// 拖曳的目标位置
	private int mDesignDragPos;
	// 拖曳之前的位置
	private int mOriginalDragPos;
	// 拖曳的x像素坐标
	private int mDragPointX;
	// 拖曳的y像素坐标
	private int mDragPointY;
	// x坐标偏移量
	private int mXOffset;
	// y坐标偏移量
	private int mYOffset;
	// ListView的上边
	private int mUpperBound;
	// ListView的下边
	private int mLowerBound;
	// ListView的自身高度
	private int mHeight;
	// 设置拖曳时的背景色
	private int backgroundColor = Color.argb(55, 0xcc, 0xcc, 0xcc);
	// 拖放时的透明度
	private float alphaRate = 0.5f;
	// android手势检测器
	private GestureDetector mGestureDetector;
	// 手势触摸滑动模式
	private static final int FLING = 0;
	// 自由滑动模式
	private static final int SLIDE = 1;
	// 丢弃模式
	private static final int TRASH = 2;
	// 当前的状态模式
	private int mStateMode = -1;
	// 我们认为满足触摸滚动条件的像素单位---由设备配置决定
	private final int mTouchSlop;
	// 可拖曳的空间块像素尺寸大小
	private int dragablePixel;
	// item正常高度
	private int mItemHeightNormal = 0;
	// item 扩充高度
	private int mItemHeightExpanded = mItemHeightNormal * 2;
	// item项一般的高度
	private int mItemHeightHalf = mItemHeightNormal / 2;
	// 是否为膨胀模式---默认不膨胀
	private boolean isExpansion = false;
	// 设置分组数据
	private List<String> group = new ArrayList<String>();
	// 拖曳监听器
	private OnDragListViewListener mDragListener = new OnDragListViewListener() {
		public void remove(int which) {
		}

		public void drop(int from, int to) {
		}

		public void drag(int from, int to) {
		}
	};

	public DragableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		// 使用屏幕宽度的1/5作为可执行拖动的宽度
		dragablePixel = screenWidth / 5;
	}

	/**
	 * 拦截触摸事件能看到拖曳效果---先处理拦截事件在处理触摸事件
	 */
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 如果拖曳的监听器不为null切手势侦测器为null
		if (mGestureDetector == null) {
			// 如果为滑行状态
			if (mStateMode == FLING) {
				// 创建手势侦测器
				mGestureDetector = new GestureDetector(getContext(),
				// 该手势监听器只需要实现滑动这一个方法
						new SimpleOnGestureListener() {
							// 手势触摸滑动模式
							public boolean onFling(MotionEvent e1,
									MotionEvent e2, float velocityX,
									float velocityY) {
								// 当ImageView对象存在时才进行拦截
								if (mDragView != null) {
									// 如果沿着X轴上的速率大于1000像素每秒--可以认为是一种极限情况
									if (velocityX > 1000) {
										// 创建矩形区域
										Rect r = mTempRect;
										// 这是一个截屏效果将ImageView控件上的内容截取到一个矩形区域中
										mDragView.getDrawingRect(r);
										// 如果移动到的像素点的X坐标大于矩形右边缘X坐标的2/3
										if (e2.getX() > r.right * 2 / 3) {
											// 在屏幕右边缘快速滑动并释放
											stopDragging();
											// 处于滑动状态时---移除原始位置项
											mDragListener
													.remove(mOriginalDragPos);
											// 恢复所有item项
											resumeViews(true);
										}
									}
									// 如果正在拖曳表示没有偏移量
									return true;
								}
								return false;
							}
						});
			}
		}
		switch (ev.getAction()) {
		// 当处于按下事件是主要是记录各个属性值
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			// 根据x/y坐标计算出当前像素点在ListView中的哪一个item中
			int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION) {
				break;
			}
			if (group != null && group.contains(getAdapter().getItem(itemnum))) {
				break;
			}
			// 组视图在ListView中就是一个item项
			ViewGroup item = (ViewGroup) getChildAt(itemnum
					- getFirstVisiblePosition());
			// 获取坐标点及偏移量
			mDragPointX = x - item.getLeft();
			mDragPointY = y - item.getTop();
			mXOffset = ((int) ev.getRawX()) - x;
			mYOffset = ((int) ev.getRawY()) - y;
			// 指定x像素范围就是能拖曳的区间
			if (x > screenWidth - dragablePixel) {
				// 设置可缓存---可以被截取
				item.setDrawingCacheEnabled(true);
				// 当程序框架框架试图清理内存时、创建一份拷贝绘图缓存,防止回收被回收;作为悬停的图片
				Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
				// 设置位图显示的位置
				startDragging(bitmap, x, y);
				// 记录目标位置
				mDesignDragPos = itemnum;
				mOriginalDragPos = mDesignDragPos;
				mHeight = getHeight();
				int touchSlop = mTouchSlop;
				mUpperBound = Math.min(y - touchSlop, mHeight / 3);
				mLowerBound = Math.max(y + touchSlop, mHeight * 2 / 3);
				return false;
			}
			// 停止拖曳
			stopDragging();
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			mGestureDetector.onTouchEvent(event);
		}
		if (mDragView != null) {
			int action = event.getAction();
			switch (action) {
			// 松手事件
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				Rect rect = mTempRect;
				// 将截取缓存图映射到Rect对象中
				mDragView.getDrawingRect(rect);
				stopDragging();
				// 如果处于自由滑动模式
				if (mStateMode == SLIDE && event.getX() > rect.right * 3 / 4) {
					mDragListener.remove(mOriginalDragPos);
					resumeViews(true);
				} else {
					if (mDesignDragPos >= 0 && mDesignDragPos < getCount()) {
						mDragListener.drop(mOriginalDragPos, mDesignDragPos);
					}
					resumeViews(false);
				}
				break;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				int x = (int) event.getX();
				int y = (int) event.getY();
				// 获取ListView顶部的Y像素坐标
				dragView(x, y);
				if (!isExpansion) {
					// 获取ListView有效的顶部区域Y
					int tempY = this.getTop();
					if (tempY > y) {
						y = tempY;
					}
				}
				int itemnum = pointToPosition(0, y);
				if (itemnum == INVALID_POSITION) {
					break;
				}
				if (itemnum >= 0) {
					if (action == MotionEvent.ACTION_DOWN
							|| itemnum != mDesignDragPos) {
						mDragListener.drag(mDesignDragPos, itemnum);
						mDesignDragPos = itemnum;
						// 如果为膨胀模式
						if (isExpansion) {
							doExpansion();
						}
					}
					int distance = 0;
					adjustScrollBounds(y);
					if (y > mLowerBound) {
						// 滚动一点列表
						if (getLastVisiblePosition() < getCount() - 1) {
							distance = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
						} else {
							distance = 1;
						}
					} else if (y < mUpperBound) {
						// 列表向下滚动一点
						distance = y < mUpperBound / 2 ? -16 : -4;
						if (getFirstVisiblePosition() == 0
								&& getChildAt(0).getTop() >= getPaddingTop()) {
							// 如果已经在顶部设置速度为0；不滚动
							distance = 0;
						}
					}
					if (distance != 0) {
						// 使用30毫秒滚动指定的距离
						smoothScrollBy(distance, 30);
					}
				}
				break;
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 调整滚动边界
	 * 
	 * @param y
	 */
	private void adjustScrollBounds(int y) {
		if (y >= mHeight / 3) {
			mUpperBound = mHeight / 3;
		}
		if (y <= mHeight * 2 / 3) {
			mLowerBound = mHeight * 2 / 3;
		}
	}

	/**
	 * 恢复所有ListView item项、使其不处于扩充状态
	 */
	private void resumeViews(boolean deletion) {
		for (int i = 0;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// 强制更新item 数量
					int position = getFirstVisiblePosition();
					int y = getChildAt(0).getTop();
					setAdapter(getAdapter());
					setSelectionFromTop(position, y);
				}
				try {
					// 当需要时item项被强制创建
					this.layoutChildren();
					v = getChildAt(i);
				} catch (IllegalStateException ex) {
					// 多数因为layoutChildren方法抛出(在进程被撤掉；但是仍然处于触摸事件时)
					ex.printStackTrace();
				}
				if (v == null) {
					return;
				}
			}
			ViewGroup.LayoutParams params = v.getLayoutParams();
			params.height = mItemHeightNormal;
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 调整可见性和大小(使item项膨胀)
	 */
	private void doExpansion() {
		int childnum = mDesignDragPos - getFirstVisiblePosition();
		if (mDesignDragPos > mOriginalDragPos) {
			childnum++;
		}
		int numheaders = getHeaderViewsCount();
		View first = getChildAt(mOriginalDragPos - getFirstVisiblePosition());
		for (int i = 0;; i++) {
			View vv = getChildAt(i);
			if (vv == null) {
				break;
			}
			int height = mItemHeightNormal;
			int visibility = View.VISIBLE;
			if (mDesignDragPos < numheaders && i == numheaders) {
				// 当被拖曳的item被拖到顶端时放到ListView第一项
				if (vv.equals(first)) {
					visibility = View.INVISIBLE;
				} else {
					height = mItemHeightExpanded;
				}
			} else if (vv.equals(first)) {
				// 处理正在被拖曳的item项
				if (mDesignDragPos == mOriginalDragPos
						|| getPositionForView(vv) == getCount() - 1) {
					// 在原始的位置上面悬停
					visibility = View.INVISIBLE;
				} else {
					// 不在上空悬停；理想情况下,已经达到了预期效果，item将完全消失；但是不设置它的大小为0；也不设置可见性为gone;
					height = 1;
				}
			} else if (i == childnum) {
				if (mDesignDragPos >= numheaders
						&& mDesignDragPos < getCount() - 1) {
					height = mItemHeightExpanded;
				}
			}
			ViewGroup.LayoutParams params = vv.getLayoutParams();
			params.height = height;
			vv.setLayoutParams(params);
			vv.setVisibility(visibility);
		}
	}

	/**
	 * 开始拖曳
	 * 
	 * @param bitmap
	 * @param x
	 * @param y
	 */
	private void startDragging(Bitmap bitmap, int x, int y) {
		stopDragging();
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowParams.x = x - mDragPointX + mXOffset;
		mWindowParams.y = y - mDragPointY + mYOffset;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;
		Context context = getContext();
		ImageView image = new ImageView(context);
		image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				mItemHeightNormal));
		image.setImageBitmap(bitmap);
		image.setAlpha((int) (255 * alphaRate));
		if (drawable != null) {
			image.setBackgroundDrawable(drawable);
		} else {
			image.setBackgroundColor(backgroundColor);
		}
		// 引用它当不需要时回收它
		mDragBitmap = bitmap;
		mWindowManager.addView(image, mWindowParams);
		mDragView = image;
	}

	/**
	 * 拖曳视图
	 */
	private void dragView(int x, int y) {
		if (mStateMode == SLIDE) {
			float alpha = 1.0f;
			int width = mDragView.getWidth();
			if (x > width / 2) {
				alpha = ((float) (width - x)) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		}
		if (mStateMode == FLING || mStateMode == TRASH) {
			mWindowParams.x = x - mDragPointX + mXOffset;
		} else {
			mWindowParams.x = 0;
		}
		mWindowParams.y = y - mDragPointY + mYOffset;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
		if (mTrashcan != null) {
			int width = mDragView.getWidth();
			if (y > getHeight() * 3 / 4) {
				mTrashcan.setLevel(2);
			} else if (width > 0 && x > width / 4) {
				mTrashcan.setLevel(1);
			} else {
				mTrashcan.setLevel(0);
			}
		}
	}

	/**
	 * 结束拖曳
	 */
	private void stopDragging() {
		if (mDragView != null) {
			mDragView.setVisibility(GONE);
			WindowManager wm = (WindowManager) getContext().getSystemService(
					Context.WINDOW_SERVICE);
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null) {
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
		if (mTrashcan != null) {
			mTrashcan.setLevel(0);
		}
	}

	/**
	 * 设置垃圾图片
	 * 
	 * @param trash
	 */
	public void setTrashcan(Drawable trash) {
		mTrashcan = trash;
		mStateMode = TRASH;
	}

	/**
	 * 手动设置可拖曳的空间块尺寸x像素点
	 * 
	 * @param dragablePixel
	 */
	public void setDragablePixel(int dragablePixel) {
		this.dragablePixel = dragablePixel;
	}

	/**
	 * 使用资源设置拖曳时的背景图片
	 * 
	 * @param resDrawable
	 */
	public void setResDrawable(int resDrawable) {
		this.drawable = this.getContext().getResources()
				.getDrawable(resDrawable);
	}

	/**
	 * 使用drawable对象设置拖曳时的背景
	 * 
	 * @param drawable
	 */
	public void setResDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	/**
	 * 设置item的正常高度---在封装此自定义类后由于高度不确定；调用这个方法主要是为了扩充子视图 但必须设置为允许扩充且不能分组
	 * 
	 * @param mItemHeightNormal
	 */
	public void setmItemHeightNormal(int mItemHeightNormal) {
		this.mItemHeightNormal = mItemHeightNormal;
	}

	/**
	 * 设置拖放时的背景色
	 */
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * 设置拖曳时的监听器在主界面执行业务逻辑(实现相关方法)
	 * 
	 * @param listener
	 */
	public void setDragListViewListener(OnDragListViewListener listener) {
		mDragListener = listener;
	}

	/**
	 * 设置是否为膨胀模式
	 * 
	 * @param isExpansion
	 */
	public void setExpansion(boolean isExpansion) {
		this.isExpansion = isExpansion;
	}

	/**
	 * 设置分组数据---以保证显示分组的item不可点击
	 * 
	 * @param group
	 */
	public void setGroup(List<String> group) {
		this.group = group;
		this.isExpansion = false;
	}

}
