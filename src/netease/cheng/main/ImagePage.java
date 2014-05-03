package netease.cheng.main;

import java.util.ArrayList;
import java.util.List;

import netease.cheng.adapters.ImageAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 图片页--GridView的用法
 * @author Administrator
 *
 */
public class ImagePage extends Activity{
	private GridView grid;
	private TextView text;
	private ProgressBar pb;
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.image_detail_page);
		text=(TextView)findViewById(R.id.top_title);
		pb=(ProgressBar)findViewById(R.id.top_progress);
		pb.setVisibility(View.INVISIBLE);
		text.setText("图片");
		grid=(GridView)findViewById(R.id.image_detail_grid);
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println(position+"--->");
			}
		});
		setGridAdapter();
	}
	private void setGridAdapter(){
		List<String> data=new ArrayList<String>();
		for(int i=0;i<15;i++){
			data.add("图片信息->"+i);
		}
		ImageAdapter adapter=new ImageAdapter(this,data);
//		grid.addView(createImageView(), 150, 160);
		grid.setAdapter(adapter);
		
	}
	private ImageView createImageView(){
		ImageView img=new ImageView(this);
		img.setBackgroundResource(R.drawable.picture_more);
		img.setLayoutParams(new LayoutParams(-2,-2));
		return img;
	}
}
