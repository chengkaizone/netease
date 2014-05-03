package netease.cheng.adapters;

import java.util.List;

import netease.cheng.beans.NewsInfo;
import netease.cheng.main.R;
import netease.cheng.widgets.MyImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HeadGalleryAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private List<NewsInfo> news;
//	private int[] res;
	public HeadGalleryAdapter(Context context,List<NewsInfo> news){
		this.context=context;
//		this.res=res;
		this.news=news;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		if(news.size()!=0){
			return 3;
		}
		return 0;
	}
	public int getLoc(int loc){
		if (loc >= 0) {
			int i = getCount();
			if (i > 0){
				loc %= i;
			}
		}
		return loc;
	}
	
	public int getInitLoc(int loc){
		int k=getCount();
		if(loc<=0){
			return 0;
		}else{
			if(k>0){
				int j=getLoc(loc);
				k=j+(k/2-k/2%k);
			}else{
				k=0;
			}
		}
		return k;
	}
	public NewsInfo getItem(int position) {
		return news.get(getLoc(position));
	}
	public long getItemId(int position) {
		return getLoc(position);
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=inflater.inflate(R.layout.head_gallery_item,null);
			holder.img=(MyImageView)convertView.findViewById(R.id.head_gallery_image_item);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		int i=getLoc(position);
		if(getCount()!=0){
			holder.img.setImageBitmap(news.get(i).getBitmap());
//			holder.img.setImageResource(res[i]);
			
		}
		return convertView;
	}
	public class Holder{
		MyImageView img;
	}
}
