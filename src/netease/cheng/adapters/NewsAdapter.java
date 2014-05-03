package netease.cheng.adapters;

import java.util.List;

import netease.cheng.main.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private Bitmap demo;
	private List<String> descs,details;
	private boolean flag=false;
	public NewsAdapter(Context context,List<String> descs,List<String> details){
		this.context=context;
		demo=BitmapFactory.decodeResource(context.getResources(),R.drawable.skin5);
		inflater=LayoutInflater.from(context);
		this.descs=descs;
		this.details=details;
		flag=descs.size()>details.size();
	}
	@Override
	public int getCount() {
		return flag?details.size():descs.size();
	}

	@Override
	public Object getItem(int location) {
		return flag?details.get(location):descs.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=inflater.inflate(R.layout.adapter_news,null);
			holder.image=(ImageView)convertView.findViewById(R.id.adapter_news_img);
			holder.desc=(TextView)convertView.findViewById(R.id.adapter_news_desc);
			holder.detail=(TextView)convertView.findViewById(R.id.adapter_news_detail);
			holder.comment=(TextView)convertView.findViewById(R.id.adapter_news_comment);
			convertView.setTag(holder);
		}
		else{
			holder=(Holder)convertView.getTag();
		}
		holder.image.setImageBitmap(demo);
		holder.desc.setText(descs.get(position));
		holder.detail.setText(details.get(position));
		if(position==1){
			holder.comment.setBackgroundResource(R.drawable.topcomment_column);
			holder.comment.setTextColor(Color.argb(255, 255, 255, 255));
			holder.comment.setText("×¨Ìâ");
		}else{
			holder.comment.setBackgroundColor(Color.argb(0, 0, 0, 0));
			holder.comment.setTextColor(Color.argb(0, 0, 0, 0));
			holder.comment.setText("1¸úÌû");
		}
		return convertView;
	}
	private class Holder{
		ImageView image;
		TextView desc;
		TextView detail;
		TextView comment;
	}
	
}
