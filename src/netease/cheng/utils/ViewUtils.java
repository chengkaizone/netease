package netease.cheng.utils;

import netease.cheng.main.R;
import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewUtils {
	
	public static int getScreenWidth(Activity act){
		Display dis=act.getWindowManager().getDefaultDisplay();
		return dis.getWidth();
	}
	public static int getScreenHeight(Activity act){
		Display dis=act.getWindowManager().getDefaultDisplay();
		return dis.getHeight();
	}
	public static ListView createListView(Context context){
		ListView list=(ListView)LayoutInflater.from(context)
		.inflate(R.layout.listview,null);
		return list;
	}
	public static TextView createText(Context context,int id,String str){
    	TextView text=(TextView)LayoutInflater.from(context)
    	.inflate(R.layout.title_text,null);
    	text.setId(id);
    	text.setText(str);
    	return text;
    }
	public Button createButton(Context context,String str){
		Button text=new Button(context);
		text.setText(str);
		text.setGravity(Gravity.CENTER);
		text.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		return text;
	}
	
}
