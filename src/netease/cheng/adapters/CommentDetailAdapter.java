package netease.cheng.adapters;

import java.util.List;

import netease.cheng.beans.CommentResult;
import netease.cheng.main.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentDetailAdapter extends BaseAdapter{
	
	private Context context;
	private List<CommentResult> data;
	private LayoutInflater inflater;
	private Bitmap hot,last;
	public CommentDetailAdapter(Context context,List<CommentResult> data){
		this.context=context;
		this.data=data;
		inflater=LayoutInflater.from(context);
		hot=BitmapFactory.decodeResource(context.getResources(),
				R.drawable.hot_comment);
		last=BitmapFactory.decodeResource(context.getResources(),
				R.drawable.latest_comment);
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CommentResult getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		System.out.println(position);
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=inflater.inflate(R.layout.comment_detail_item,null);
			holder.img=(ImageView)convertView.findViewById(R.id.comment_detail_img);
			holder.content=(TextView)convertView.findViewById(R.id.comment_detail_content);
			holder.time=(TextView)convertView.findViewById(R.id.comment_detail_time);
			holder.user=(TextView)convertView.findViewById(R.id.comment_detail_user);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
//		System.out.println("---------------------->"+position);
		if(position==0){
//			System.out.println("0--->"+position);
			holder.img.setVisibility(View.VISIBLE);
			holder.img.setImageBitmap(hot);
		}else if(position==1){
//			System.out.println("1--->"+position);
			holder.img.setVisibility(View.VISIBLE);
			holder.img.setImageBitmap(last);
		}else{
			holder.img.setVisibility(View.GONE);
		}
		CommentResult info=data.get(position);
		holder.content.setText(info.getContent());
		holder.time.setText(info.getTime());
		holder.user.setText(info.getUser());
		
		return convertView;
	}
	private class Holder{
		ImageView img;
		TextView content;
		TextView time;
		TextView user;
	}
}
