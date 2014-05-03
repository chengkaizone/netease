package netease.cheng.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class VoteSubmit extends Activity{
	private Animation see;//使用加速---减速插值器
	private TextView blue,green,red;
	private Button btn;
	public void onCreate(Bundle save){
		super.onCreate(save);
		//请求无主题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vote_submit);
		blue=(TextView)findViewById(R.id.vote_blue);
		green=(TextView)findViewById(R.id.vote_green);
		red=(TextView)findViewById(R.id.vote_red);
		btn=(Button)findViewById(R.id.vote_submit_btn);
		see=AnimationUtils.loadAnimation(this,R.anim.vote_see);
		blue.startAnimation(see);
		green.startAnimation(see);
		red.startAnimation(see);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		see.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				blue.setVisibility(View.VISIBLE);
				red.setVisibility(View.VISIBLE);
				green.setVisibility(View.VISIBLE);
			}
			public void onAnimationRepeat(Animation animation) {
				
			}
			public void onAnimationEnd(Animation animation) {
				
			}
		});
	}
}
