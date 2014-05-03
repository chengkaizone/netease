package netease.cheng.utils;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
/**
 * 参考类未使用
 * @author Administrator
 *
 */
public class DotIndicator extends Indicator {
	private static final String TAG = "SlideDotIndicator";
	//是否显示数量
	private boolean mIsShowNum = false;

	public DotIndicator(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public DotIndicator(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	private void init(Context paramContext) {
	}
	//设置当前选项
	public void onSetCurrentItem() {
		//返回子节点的数量
		int i = getChildCount();
		for (int k = 0; k < i; k++) {
			DotIndicatorItem item = (DotIndicatorItem) getChildAt(k);
			//如果不显示数量那么重置数量
			if (this.mIsShowNum){
				item.resetNum();
			}
			//过渡--->获取控件上的图片//重置表示只显示第一层图片
			((TransitionDrawable) item.getDrawable()).resetTransition();
		}
		//获取当前选项
		int j = getCurrentItem();
		if ((j < 0) || (j >= i)){
			return;
		}
		DotIndicatorItem item1 = (DotIndicatorItem) getChildAt(j);
		if (this.mIsShowNum)
			item1.setNum(j);
		((TransitionDrawable) item1.getDrawable()).startTransition(50);
	}

	public void onSetTotalItems() {
		//从父类中分离所有的视图
		detachAllViewsFromParent();
		int i = getTotalItems();
		LayoutInflater localLayoutInflater=null;
		if (i > 0){
			localLayoutInflater = LayoutInflater.from(getContext());
		}
		for (int j = 0;j<i; ++j) {
			DotIndicatorItem item = (DotIndicatorItem)
			localLayoutInflater.inflate(2130903072, null);
			((TransitionDrawable) item.getDrawable())
			.setCrossFadeEnabled(true);
			addView(item,new LinearLayout.LayoutParams(-2, -2));
		}
	}
}