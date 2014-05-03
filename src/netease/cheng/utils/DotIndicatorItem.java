package netease.cheng.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 参考类未使用
 * @author Administrator
 *
 */
public class DotIndicatorItem extends ImageView {
	private int mNum = -1;

	public DotIndicatorItem(Context paramContext) {
		super(paramContext);
	}

	public DotIndicatorItem(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public DotIndicatorItem(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}
	//在画布上写字
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.mNum != -1){
			TextPaint paint = new TextPaint(1);
			int width= getMeasuredWidth();
			int height= getMeasuredHeight();
			String str = String.valueOf(1 + this.mNum);
			int k = (width - 3) * str.length();
			paint.setTextSize(k);
			paint.setColor(-16777216);//设置画笔颜色
			canvas.drawText(str, (width - k / 2) / 2,(height + k) / 2, paint);
		}
	}

	public void resetNum() {
		this.mNum = -1;
	}

	public void setNum(int paramInt) {
		this.mNum = paramInt;
	}
}