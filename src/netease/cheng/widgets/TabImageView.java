package netease.cheng.widgets;

import netease.cheng.main.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 选项卡图片显示--不需要再主程序中使用逻辑来控制效果
 * @author Administrator
 *
 */
public class TabImageView extends ImageView{
	StateListDrawable state;
	Context context;
	Drawable selected,font;
	Bitmap src;
	
	public TabImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		
		state.addState(SELECTED_STATE_SET, selected);
	}
	public TabImageView(Context context,int back_bg,int cur_bg){
		super(context);
		this.context=context;
		Bitmap src=BitmapFactory.decodeResource(context.getResources(),back_bg);
		font=context.getResources().getDrawable(R.drawable.tab_front_bg);
		selected=context.getResources().getDrawable(cur_bg);
		state=new StateListDrawable();
		//添加状态选择器
		state.addState(SELECTED_STATE_SET, selected);
		
//		this.setBackgroundDrawable(state);
//		this.setImageBitmap(src);
		this.setImageDrawable(state);
		this.setBackgroundResource(back_bg);
	}
}
