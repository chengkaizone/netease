package netease.cheng.main;

import java.util.ArrayList;
import java.util.List;

import netease.cheng.adapters.TopicAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 话题主页
 * @author Administrator
 *
 */
public class Topic extends Activity implements OnItemClickListener{
	private ListView list;
	private List<String> data=new ArrayList<String>();
	private TextView text;
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.topic);
		text=(TextView)findViewById(R.id.top_title);
		text.setText("话题");
		list=(ListView)findViewById(R.id.topic_list);
		init();
		list.setOnItemClickListener(this);
	}
	//初始化数据
	private void init(){
		for(int i=0;i<15;i++){
			data.add("发生的回复可见当时看多少积--->"+i);
		}
		TopicAdapter adapter=new TopicAdapter(this,data);
		View view=LayoutInflater.from(this).inflate(R.layout.topic_list_head,null);
		list.addHeaderView(view);
		list.addFooterView(LayoutInflater.from(this).inflate(R.layout.button,null));
		list.setAdapter(adapter);
//		list.addFooterView(createButton("查看下面十条"));
	}
	//重写单击选项的方法---必须重写
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//测试使用--用于提示用户
		Toast.makeText(Topic.this,"--->"+position,2000).show();
		//界面跳转--到详情页
		startActivity(new Intent(Topic.this,DetailPage.class));
	}
}
