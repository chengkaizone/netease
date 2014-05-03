package netease.cheng.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
/**
 * 欢迎页面
 * @author Administrator
 *
 */
public class WelcomeAct extends Activity{
	//消息处理器
	Handler handler=new Handler();
	ImageView img;
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.root_welcome_main);
		img=(ImageView)findViewById(R.id.welcome_main_img);
		//延迟两秒执行run1线程
		handler.postAtTime(run1,2000);
	}
	Runnable run1=new Runnable(){
		public void run(){
			//设置延迟两秒执行线程run2
			handler.postDelayed(run2,2000);
			//设置图片可见
			img.setVisibility(View.VISIBLE);
		}
	};
	Runnable run2=new Runnable(){
		public void run(){
			//跳转到详情页
			startActivity(new Intent(WelcomeAct.this,TabTest.class));
			//销毁当前页面
			finish();
		}
	};
}
