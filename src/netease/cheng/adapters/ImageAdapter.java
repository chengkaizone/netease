package netease.cheng.adapters;

import java.util.List;

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

public class ImageAdapter extends BaseAdapter{
	private Context context;
	private List<String> data;
	private LayoutInflater inflater;
	private Bitmap bitmap,more;
	public ImageAdapter(Context context,List<String> data){
		this.context=context;
		this.data=data;
		inflater=LayoutInflater.from(context);
		bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.bg1);
		more=BitmapFactory.decodeResource(context.getResources(),
				R.drawable.picture_more);
	}
	@Override
	public int getCount() {
		return data.size()+1;
	}

	@Override
	public String getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		System.out.println("--->"+position);
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=inflater.inflate(R.layout.image_detail_item,null);
			holder.img=(ImageView)convertView.findViewById(R.id.image_detail_img);
			holder.text=(TextView)convertView.findViewById(R.id.image_detail_info);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		if(position<data.size()){
			holder.img.setImageBitmap(bitmap);
			holder.text.setText(data.get(position));
		}else{
			holder.img.setImageBitmap(more);
		}
		return convertView;
	}
	private class Holder{
		ImageView img;
		TextView text;
	}
}
