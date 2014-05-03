package netease.cheng.main;

import java.util.ArrayList;
import java.util.List;

import netease.cheng.adapters.HeadGalleryAdapter;
import netease.cheng.adapters.NewsAdapter;
import netease.cheng.beans.NewsInfo;
import netease.cheng.utils.ViewUtils;
import netease.cheng.widgets.HeadGallery;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
/**
 * 新闻主页面---当使用选项卡时如果包含多个界面；有些拦截事件将失效--自定义菜单拦截
 * 可以通过重写按键监听---或者分派事件实现
 * @author Administrator
 *
 */
public class MainClass extends Activity {
    private ImageView sliding;
    private HorizontalScrollView hori;
    private ImageButton left,right;
    private LinearLayout lay;
    private ImageView image,indicator;
    private FrameLayout frame;
    private List<String> titles=new ArrayList<String>();
    List<String> descs=new ArrayList<String>();
    List<String> details=new ArrayList<String>();
    private ViewFlipper flipper;
    private LayoutInflater inflater;
    private View view,menuView,root,window,mainView;
    private ListView news_list;
    private PopupWindow popup;
    private TextView refresh_hint,refresh_time;
    int[] resDraw={R.drawable.bg1,
		R.drawable.bg2,R.drawable.bg3,R.drawable.duoyun};
    private List<NewsInfo> news;
    private int screenWidth,screenHeight,patch,slidLoc=0,index=0;
    private int flag=1; 
    //当UI界面创建时回调此方法
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sliding=(ImageView)findViewById(R.id.main_frame_sliding);
        left=(ImageButton)findViewById(R.id.main_left);
        right=(ImageButton)findViewById(R.id.main_right);
        lay=(LinearLayout)findViewById(R.id.linearLayout1);
        hori=(HorizontalScrollView)findViewById(R.id.main_horizontal);
        image=(ImageView)findViewById(R.id.main_frame_sliding);
        frame=(FrameLayout)findViewById(R.id.frameLayout1);
        flipper=(ViewFlipper)findViewById(R.id.main_flipper);
        
        screenWidth=this.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight=this.getWindowManager().getDefaultDisplay().getHeight();
        patch=(screenWidth-hori.getPaddingLeft()-hori.getPaddingRight())/6;
        System.out.println(patch);
        initData();
        left.setVisibility(View.INVISIBLE);
        if(lay.getChildCount()<=5){
        	right.setVisibility(View.INVISIBLE);
        }
        hori.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				//这里需要监听像素值
//				System.out.println(v.getScrollX());
				//先获取滚动条上的X坐标位置
				if(lay.getChildCount()>5){
					int dis=v.getScrollX();//超出滚动部分
					if(v.getScrollX()!=0){
						left.setVisibility(View.VISIBLE);
					}else{
						left.setVisibility(View.INVISIBLE);
					}
					int num=patch*(lay.getChildCount()-6);
					if(dis>=num-2){
						right.setVisibility(View.INVISIBLE);
					}else{
						right.setVisibility(View.VISIBLE);
					}
				}
				return v.onTouchEvent(event);//此处不能返回直接true，否则HorizontalScrollView不响应
			}
		});
    }
    //初始化模拟数据
    private void initData(){
    	titles.add("头条");
    	titles.add("手机");
    	titles.add("军事");
    	titles.add("科技");
    	titles.add("国际");
//    	titles.add("国内");
    	titles.add("财经");
    	titles.add("更多");
    	for(int i=0;i<15;i++){
    		descs.add("头条两会正在举行"+(i+1));
    		details.add("风刀霜剑氟喹啉结发上课防守打法能收到甲方"+i);
    	}
    	//动态添加TextView
    	for(int i=0;i<titles.size();i++){
    		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(patch,-2);
    		lay.addView(ViewUtils.createText(this,i,titles.get(i)), i, params);
    	}
    	for(int i=0;i<lay.getChildCount();i++){
    		lay.getChildAt(i).setOnClickListener(scrollListener);
    	}
    	for(int i=0;i<lay.getChildCount();i++){
    		flipper.addView(ViewUtils.createListView(this));
    	}
    	news=new ArrayList<NewsInfo>();
    	for(int i=0;i<resDraw.length;i++){
    		Bitmap b=BitmapFactory.decodeResource(getResources(), resDraw[i]);
    		NewsInfo info=new NewsInfo();
    		info.setBitmap(b);
    		if(i==1){
    			info.setTag("");
    		}else{
    			info.setTag("图集"+i);
    		}
    		info.setTitle("两会正在举行-->"+i);
    		news.add(info);
    	}
    	requestData();
    	initMenu();
    }
    //单击监听器
    OnClickListener scrollListener=new OnClickListener(){
		public void onClick(View v) {
			setAnim(v.getLeft());//设置滑动块的动画效果
			switch(v.getId()){
			case 0:break;
			case 1:break;
			case 2:break;
			case 3:break;
			case 4:break;
			case 5:break;
			case 6:break;
			case 7:break;
			}
			flipper.setDisplayedChild(v.getId());
			Toast.makeText(MainClass.this,titles.get(v.getId()),2000).show();
		}
    };
    //请求数据
    private void requestData(){
    	ListView list=(ListView)flipper.getChildAt(0);
		NewsAdapter adapter=new NewsAdapter(MainClass.this,descs,details);
		View v=LayoutInflater.from(MainClass.this).inflate(R.layout.head_advertisment,null);
		HeadGallery head=(HeadGallery)v.findViewById(R.id.head_advert_grally);
		HeadGalleryAdapter gAdapter=new HeadGalleryAdapter(this,news);
		head.setAdapter(gAdapter);
		list.addHeaderView(v);//必须在设置适配器之前添加头
		list.addFooterView(LayoutInflater.from(this).inflate(R.layout.button,null));
		list.setAdapter(adapter);
		flipper.setDisplayedChild(0);
    }
    //封装动画的方法--用于控制滑块滑动
    private void setAnim(int loc){
    	TranslateAnimation tran=new TranslateAnimation(slidLoc,loc,
    			image.getTop()-frame.getPaddingTop(),
    			image.getTop()-frame.getPaddingTop());
    	//设置动画时间
    	tran.setDuration(100);
    	//设置保留动画的最后状态
    	tran.setFillAfter(true);
    	image.startAnimation(tran);
    	slidLoc=loc;
    	tran=null;//利于回收
    }
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch(id){
		case 0:
			startActivity(new Intent(this,CommentNew.class));
			break;
		case 1:
			startActivity(new Intent(this,VotePage.class));
			break;
		case 2:
			startActivity(new Intent(this,ImagePage.class));
			break;
		case 3:
			exit();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		System.out.println("打开菜单！");
		popup.showAtLocation(mainView, Gravity.BOTTOM|
				Gravity.CENTER_HORIZONTAL, 0, 0);
		return super.onCreateOptionsMenu(menu);
	}
	/**
	 * 初始化自定义菜单
	 */
	private void initMenu(){
		window=LayoutInflater.from(this).inflate(R.layout.olympic_news_normal_menu,null);
		mainView=LayoutInflater.from(this).inflate(R.layout.kong,null);
		LinearLayout lay1=(LinearLayout)window.findViewById(R.id.menu_my_comment);
		LinearLayout lay2=(LinearLayout)window.findViewById(R.id.menu_my_collect);
		LinearLayout lay3=(LinearLayout)window.findViewById(R.id.menu_setting);
		lay1.setOnClickListener(menuListener);
		lay2.setOnClickListener(menuListener);
		lay3.setOnClickListener(menuListener);
		//第一个参数菜单布局文件、 第二个参数菜单宽度 、第三个参数菜单高度、4菜单是否可获焦
		popup=new PopupWindow(window,-1,-2,true);
		popup.setAnimationStyle(R.style.menu_animation);
		popup.setOutsideTouchable(true);//设置popup之外可以触摸
		//必须设置才能响应视图层
		window.setFocusableInTouchMode(true);		
//		popup.setBackgroundDrawable(new BitmapDrawable());
		//焦点在这一层的时候响应
		window.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch(keyCode){
				case KeyEvent.KEYCODE_MENU:
				case KeyEvent.KEYCODE_BACK:
					System.out.println("响应视图层");
					if(flag==1){
						flag=2;
					}else if(flag==2){
						popup.dismiss();						
					}
					break;
				}
				return true;
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				System.out.println("响应菜单键");
				flag=1;
				popup.showAtLocation(mainView, Gravity.BOTTOM|
						Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		System.out.println("响应活动层");
		popup.showAtLocation(mainView, Gravity.BOTTOM|
				Gravity.CENTER_HORIZONTAL, 0, 0);
		return false;
	}

	//菜单单击监听器
	View.OnClickListener menuListener=new View.OnClickListener(){
		public void onClick(View v) {
			int id=v.getId();
			switch(id){
			case R.id.menu_my_comment:
				startActivity(new Intent(MainClass.this,CommentNew.class));
				popup.dismiss();
				break;
			case R.id.menu_my_collect:
				startActivity(new Intent(MainClass.this,VoteSubmit.class));
				popup.dismiss();
				break;
			case R.id.menu_setting:
				startActivity(new Intent(MainClass.this,CommentPage.class));
				popup.dismiss();
				break;
			
			}
		}
	};
	private void exit(){
		finish();
	}
}