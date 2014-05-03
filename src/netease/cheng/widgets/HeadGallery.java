package netease.cheng.widgets;

import netease.cheng.adapters.HeadGalleryAdapter;
import netease.cheng.beans.NewsInfo;
import netease.cheng.main.DetailPage;
import netease.cheng.main.R;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.TextView;
/**
 * 未添加图片指示器--自定义帧包含器
 * @author Administrator
 *
 */
public class HeadGallery extends FrameLayout implements 
				OnItemSelectedListener,OnItemClickListener{
	private MyGallery mGallery;
	private TextView tag;
	private TextView title;
	private HeadGalleryAdapter mAdapter;
	public HeadGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public HeadGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public HeadGallery(Context context) {
		super(context);
	}
	//重写单击监听
	public void setOnItemClickListener(
			AdapterView.OnItemClickListener listener) {
		this.mGallery.setOnItemClickListener(listener);
	}
	//设置是否自动滚动
	public void startAutoScroll(boolean bool) {
		if (this.mGallery !=null){
			this.mGallery.startAutoScroll(bool);
		}
	}
	@Override//结束解析xml文件时调用
	protected void onFinishInflate() {
		//此方法需要一个资源文件
		mGallery=(MyGallery)findViewById(R.id.head_mygallery);//需要一个资源文件
		mGallery.setAutoTime(0);
		mGallery.setOnItemSelectedListener(this);
		mGallery.setOnItemClickListener(this);
		tag=(TextView)findViewById(R.id.head_text_tag);
		title=(TextView)findViewById(R.id.head_text_title);
		super.onFinishInflate();
	}
	//共有方法
	public MyGallery getmGallery() {
		return mGallery;
	}
	public void setmGallery(MyGallery mGallery) {
		this.mGallery = mGallery;
	}
	//设置适配器传递给自定义画廊
	public void setAdapter(HeadGalleryAdapter adapter) {
		if (adapter != null){
			this.mGallery.setAdapter(adapter);
			this.mGallery.setSelection(adapter.getInitLoc(0));
			this.mAdapter = adapter;
		}
	}
	@Override//回调父类窗口可见性改变时的处理
	protected void onWindowVisibilityChanged(int loc) {
		super.onWindowVisibilityChanged(loc);
		if (((loc == 0) && (((isShown()) || (this.mGallery == null) 
				|| (this.mAdapter == null))))|| (this.mAdapter.getCount() <= 0)){
			return;
		}
		this.mGallery.setSelection(this.mAdapter.getInitLoc(this.mGallery
				.getSelectedItemPosition()));
	}
	
	@Override//回调分离窗口
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if ((this.mGallery == null) || (this.mAdapter == null)
				|| (this.mAdapter.getCount() <= 0)){
			return;
		}
		this.mGallery.setSelection(this.mAdapter.getInitLoc(this.mGallery
				.getSelectedItemPosition()));
	}
	@Override//重写被选的方法
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if(mAdapter!=null){
			NewsInfo info=mAdapter.getItem(position);
			if(!info.getTag().equals("")){
				this.tag.setText(info.getTag());
				this.tag.setBackgroundResource(R.drawable.topcomment_column);
			}
			this.title.setText(info.getTitle());
			System.out.println(info.getTag()+"---"+info.getTitle());
		}
		System.out.println("等待实现====？---->"+position);
	}
	//不处理
	public void onNothingSelected(AdapterView<?> parent) {}
	//设置指示器
	public void setIndicator(){
		System.out.println("等待实现指示器");
//		this.mIndicator.setIndicatorBg(paramContext, paramThemeSettingsHelper);
//		paramThemeSettingsHelper.setViewBackgroud(paramContext, this.mTag,
//				2130837732);
//		paramThemeSettingsHelper.setTextViewColor(paramContext, this.mTag,
//				2131230765);
	}
	@Override//重写选项被单击时回调此方法
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println("ItemClickListener--->"+position);
		this.getContext().startActivity(new Intent(getContext(),DetailPage.class));
	}
}
