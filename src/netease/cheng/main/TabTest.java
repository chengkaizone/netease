package netease.cheng.main;

import netease.cheng.widgets.TabImageView;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
/**
 * 主页面--控制选项卡
 * @author Administrator
 *
 */
public class TabTest extends TabActivity{
	TabWidget widget;
	TabHost tabHost;
	Bitmap src;
	TabImageView pre,cur;
	ImageView image;
	ViewStub stub;
	View guide;
	int imgloc=0;
	PopupWindow menu;
	View root,menuView;
	LayoutInflater inflater;
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.tab_host);
		image=(ImageView)findViewById(R.id.tab_host_image);
		stub=(ViewStub)findViewById(R.id.tab_host_stub);
		src=BitmapFactory.decodeResource(getResources(), R.drawable.tab_front_bg);
		tabHost=this.getTabHost();
		
		TabImageView tabView1=new TabImageView(this,R.drawable.back_news_tab,
				R.drawable.current_news_tab);
		TabSpec news=tabHost.newTabSpec("tab0");
		news.setContent(new Intent(this,MainClass.class));
		news.setIndicator(tabView1);
		
		TabSpec tab2=tabHost.newTabSpec("tab1");
		TabImageView tabView2=new TabImageView(this,R.drawable.back_topic_tab,
				R.drawable.current_topic_tab);
		
		tab2.setIndicator(tabView2);
		tab2.setContent(new Intent(this,Topic.class));
		TabSpec tab3=tabHost.newTabSpec("tab2");
		tab3.setIndicator(new TabImageView(this,R.drawable.back_picture_tab,
				R.drawable.current_picture_tab));
		tab3.setContent(new Intent(this,ImagePage.class));
		TabSpec tab4=tabHost.newTabSpec("tab3");
		tab4.setIndicator(new TabImageView(this,R.drawable.back_comment_tab,
				R.drawable.current_comment_tab));
		tab4.setContent(new Intent(this,CommentPage.class));
		TabSpec tab5=tabHost.newTabSpec("tab4");
		tab5.setIndicator(new TabImageView(this,R.drawable.back_vote_tab,
				R.drawable.current_vote_tab));
		tab5.setContent(new Intent(this,VotePage.class));
		tabHost.addTab(news);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		tabHost.addTab(tab4);
		tabHost.addTab(tab5);
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				int leftPix=tabHost.getCurrentTabView().getLeft();
				int rightPix=tabHost.getCurrentTabView().getRight();
				System.out.println("左边的像素坐标"+leftPix+"----右边--->"+rightPix);
				TranslateAnimation tran=new TranslateAnimation(imgloc,
						leftPix,image.getTop(),image.getTop());
				imgloc=leftPix;
				tran.setDuration(200);
				//设置保持最终状态
				tran.setFillAfter(true);
				image.startAnimation(tran);
			}
		});
	}
	
	//当界面可以获得焦点时回调此方法
	@Override
	public void onResume(){
		super.onResume();
		SharedPreferences share=this.getSharedPreferences("bools",Context.MODE_PRIVATE);
		boolean b=share.getBoolean("isFirst",false);
		if(!b){
			guide=stub.inflate();
			guide.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					v.setVisibility(View.GONE);
				}
			});
			SharedPreferences.Editor editor=share.edit();
			editor.putBoolean("isFirst",true);
			editor.commit();//提交数据--将数据写入应用程序中
		}
	}
	//创建菜单项
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, 0, 0, "环形条visible");
//		menu.add(0, 1, 0, "环形条invisible");
//		menu.add(0, 2, 0, "环形条gone");
//		return super.onCreateOptionsMenu(menu);//回调父类方法以创建菜单
//	}
//	//当菜单被点击时回调此方法--根据id号来决定事件处理
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id=item.getItemId();
//		switch(id){
//		case 0:break;
//		case 1:break;
//		case 2:break;
//		}
//		return super.onOptionsItemSelected(item);//回调此方法处理
//	}
	
}
