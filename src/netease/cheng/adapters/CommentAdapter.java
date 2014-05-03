package netease.cheng.adapters;

import java.util.List;

import netease.cheng.beans.CommentInfo;
import netease.cheng.main.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter{
	private Context context;
	private List<CommentInfo> data;
	private LayoutInflater inflater;
	public CommentAdapter(Context context,List<CommentInfo> data){
		this.context=context;
		this.data=data;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CommentInfo getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=inflater.inflate(R.layout.top_comments_item,null);
			holder.content=(TextView)convertView.findViewById(R.id.top_comments_content);
			holder.source=(TextView)convertView.findViewById(R.id.top_comments_source);
			holder.username=(TextView)convertView.findViewById(R.id.top_comments_username);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		CommentInfo info=data.get(position);
		holder.content.setText(info.getContent());
		holder.source.setText(info.getSource());
		holder.username.setText(info.getUsername());
		return convertView;
	}
	private class Holder{
		TextView content;
		TextView source;
		TextView username;
	}
}
