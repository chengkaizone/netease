package netease.cheng.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Scroller;
//垂直滚动显示类---暂未使用--作为容器使用
public class VerticalScroll extends ViewGroup{
	//处于静止状态
	private static final int TOUCH_STATE_REST=0;
	//处于滚动状态
	private static final int TOUCH_STATE_SCROLLING=1;
	public int maxHeight=60;
	//当前触摸状态
	private int touchState;
	//滚动器
	private Scroller mScroller;
	//显示默认子控件
	private int defaultChild=1;
	//指定显示在屏幕顶部的控件
	private int topChild;
	//容器下的第一个控件
	private View firstChild;
	//动作最后的X坐标
	private float lastMotionX;
	//动作最后的Y坐标
	private float lastMotionY;
	//响应触摸的最小像素值
	private int touchSlop;
	public VerticalScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VerticalScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VerticalScroll(Context context) {
		super(context);
		init(context);
	}
	private void init(Context context){
		mScroller=new Scroller(context);
		//默认显示第二个子控件
		topChild=defaultChild;
		//获取两点间的触摸距离--滚动的像素--slop溢出
		touchSlop=ViewConfiguration.get(getContext()).getScaledTouchSlop();
		touchState=TOUCH_STATE_REST;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed){
			int childTop=0;
			final int num=getChildCount();
			for(int i=0;i<num;i++){
				final View child=this.getChildAt(i);
				if(child.getVisibility()!=View.GONE){
					final int childWidth=child.getMeasuredWidth();
					final int childHeight=child.getMeasuredHeight();
					//为每一个子控件分配位置有于是垂直滚动所以只处理纵坐标
					child.layout(0, childTop,childWidth ,childTop+=childHeight);
				}
			}
		}
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();//父类未实现的方法
		if(mScroller.computeScrollOffset()){
			//设置视图改变的位置
			this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			//重新通知绘图
			this.postInvalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//计算尺寸
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		System.out.println(measuredHeight+"---onMeasure---"+measuredWidth);
		this.setMeasuredDimension(measuredHeight, measuredWidth);
	    //给定相同的宽高   ---获取子视图总数
	    final int count = getChildCount();   
	    //为每一个视图分配宽高
	    for (int i = 0; i < count; i++) {   
	        getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
	    }   
	    //定向到第一个子视图下面
	    scrollTo(0,getChildAt(0).getHeight());
	}
	private int measureHeight(int measureHeight){
		//原则上两种情况不一样--大多数情况一样--暂不处理
		int specMode=MeasureSpec.getMode(measureHeight);
		int specSize=MeasureSpec.getSize(measureHeight);
		int result=480;
		if(specMode==MeasureSpec.AT_MOST){
			result=specSize;
		}else if(specMode==MeasureSpec.EXACTLY){
			result=specSize;
		}
		return result;
	}
	private int measureWidth(int measureWidth){
		//原则上两种情况不一样--大多数情况一样--暂不处理
		int specMode=MeasureSpec.getMode(measureWidth);
		int specSize=MeasureSpec.getSize(measureWidth);
		int result=320;
		if(specMode==MeasureSpec.AT_MOST){
			result=specSize;
		}else if(specMode==MeasureSpec.EXACTLY){
			result=specSize;
		}
		return result;
	}
	//如果注重重用性该方法需要在主线程中重写--在此处写只能是静态显示？？？
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action=event.getAction();
		final float x=event.getX();
		final float y=event.getY();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(mScroller.isFinished()){
				//强制结束滚动动画
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int distanceY=(int)(lastMotionY-y);
			lastMotionY=y;
			//此方法将回调想关方法getScrollY()获取Y方向改变的距离
			scrollBy(0,distanceY);
			break;
		case MotionEvent.ACTION_UP:
			//获取当前视图组件顶部边缘位置
			final int scrollY=getScrollY();
			//松手后开始滚动到实际的目标Y坐标
			final int deltaY=getHeight()-scrollY;
			//计算滚动的时间
			final int time=Math.abs(deltaY)*2;
			if(deltaY>maxHeight){
				//控制最终停止的位置
				mScroller.startScroll(0,scrollY, 0,deltaY-maxHeight,time);
			}else{
				mScroller.startScroll(0,scrollY, 0,deltaY,time);
			}
			invalidate();//通知重绘组件
			//执行完后赋值为静止状态
			touchState=TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			touchState=TOUCH_STATE_REST;
			break;
		}
		return true;//必须返回true；否则主线程不响应
	}
	public void setHeadHeight(int height){
		maxHeight=height;
	}
	//拦截触摸事件以显示滚动过程
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action=event.getAction();
		if((action==MotionEvent.ACTION_MOVE)&&touchState!=TOUCH_STATE_REST){
			return true;
		}
		final float x=event.getX();
		final float y=event.getY();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			lastMotionX=x;
			lastMotionY=y;
			touchState=mScroller.isFinished()?TOUCH_STATE_REST:TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_MOVE:
			final int yDistance=(int)Math.abs(lastMotionY-y);
			if(yDistance>touchSlop){
				touchState=TOUCH_STATE_SCROLLING;
			}
			break;
			//同时处理松手事件和取消事件
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			touchState=TOUCH_STATE_REST;
			break;
		}
		return true;//不向外传递拦截事件
	}
	
}
