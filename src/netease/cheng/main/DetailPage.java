package netease.cheng.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
/**
 * ฯ๊ว้าณ
 * @author Administrator
 *
 */
public class DetailPage extends Activity implements OnClickListener{
	ImageButton imgbtn;
	LinearLayout lay;
	Button btn;
	boolean flag=false;
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.detail_page);
//		View reply=LayoutInflater.from(this).inflate(R.layout.reply_frame,null);
		imgbtn=(ImageButton)findViewById(R.id.reply_img_button);
		lay=(LinearLayout)findViewById(R.id.reply_edittext_layout);
		btn=(Button)findViewById(R.id.reply_button);
		btn.setOnClickListener(this);
		imgbtn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.reply_button:
			lay.setVisibility(View.GONE);
			break;
		case R.id.reply_img_button:
			lay.setVisibility(View.VISIBLE);
			break;
		}
	}
}
