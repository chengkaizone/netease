package netease.cheng.adapters;

import java.util.List;

import netease.cheng.main.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TopicAdapter extends BaseAdapter{
	private List<String> data;
	private Context context;
	LayoutInflater inflater;
	public TopicAdapter(Context context,List<String> data){
		this.context=context;
		this.data=data;
		inflater=LayoutInflater.from(context);
	}
	public int getCount() {
		return data.size();
	}
	public String getItem(int position) {
		return data.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=inflater.inflate(R.layout.adapter_topic,null);
			holder.text=(TextView)convertView.findViewById(R.id.adapter_topic_title);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		holder.text.setText(data.get(position));
		return convertView;
	}
	private class Holder{
		TextView text;
	}
}
