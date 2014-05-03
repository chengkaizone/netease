package sutras.cheng.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class SignableListView extends ListView {
	//
	private Context mContext;
	private int originalColor = Color.TRANSPARENT;
	private int checkColor = Color.WHITE;
	private Drawable preBackground;
	private Drawable checkBackground;
	private View checkView;
	private int checkPosition;
	private boolean flag = false;
	private ImageView slider;
	private TranslateAnimation anim;
	private OnItemClickListener mItemListener;

	public SignableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		slider = new ImageView(mContext);
		slider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 40));
		anim = new TranslateAnimation(0f, 0, this.getWidth(), 50);
		anim.setDuration(200);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mItemListener = onItemClickListener;
		super.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mItemListener.onItemClick(parent, view, position, id);
				checkPosition = position;
				check();
			}
		});
	}

	public void setCheckBackground(Drawable checkDrawable) {
		// this.preBackground=oriDrawable;
		this.checkBackground = checkDrawable;
		flag = false;
	}

	public void setCheckBackground(int resId) {
		// this.preBackground=mContext.getResources().getDrawable(oresId);
		this.checkBackground = mContext.getResources().getDrawable(resId);
		slider.setBackgroundResource(resId);
		flag = false;
	}

	public void setCheckBackgroundColor(int color) {
		// this.originalColor=oriColor;
		this.checkColor = color;
		slider.setBackgroundColor(color);
		flag = true;
	}

	public int getCheckPosition() {
		return checkPosition;
	}

	private void check() {
		slider.startAnimation(anim);
		// int first=this.getFirstVisiblePosition();
		// int last=this.getLastVisiblePosition();
		// if(checkPosition>first&&checkPosition<last){
		// //得到总可见的item项
		// int count=this.getChildCount();
		// System.out.println(first+"====="+last+"=====当前点--->"
		// +((count-1)-checkPosition+first));
		// for(int i=0;i<count;i++){
		// checkView=this.getChildAt(i);
		// if(i==((count-1)-checkPosition+first)){
		// if(flag){
		// checkView.setBackgroundColor(checkColor);
		// }else{
		// checkView.setBackgroundDrawable(checkBackground);
		// }
		// }else{
		// if(flag){
		// checkView.setBackgroundColor(originalColor);
		// }
		// checkView.setBackgroundDrawable(preBackground);
		// }
		// }
		// }
	}
}
