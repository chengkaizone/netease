package netease.cheng.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 投票页面--横向切屏技术
 * @author Administrator
 *
 */
public class VotePage extends Activity{
	Button btn;
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.vote_main);
		btn=(Button)findViewById(R.id.vote_btn);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(VotePage.this,VoteSubmit.class));
			}
		});
	}
}
