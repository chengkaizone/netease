package netease.cheng.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * ListView--暂未使用--用作参考
 * @author Administrator
 *
 */
public class PullListView extends ListView implements
		PullRefreshListView.PullRefreshCallback {
	private ListAdapter mAdapter;
	private ArrayList<ListView.FixedViewInfo> mHeaders = new ArrayList<ListView.FixedViewInfo>();
	private ArrayList<ListView.FixedViewInfo> mFooters = new ArrayList<ListView.FixedViewInfo>();
	private FrameLayout mHeaderhContainer;
	private int mLastMotionX;
	private PullRefreshListView mPullRefreshListView;
	private int mTouchSlop;//偏离值

	public PullListView(Context paramContext) {
		super(paramContext);
		init(paramContext);
	}

	public PullListView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext);
	}

	public PullListView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init(paramContext);
	}

	private void init(Context paramContext) {
		this.mHeaderhContainer = new FrameLayout(paramContext);
		this.mTouchSlop = ViewConfiguration.get(paramContext)
				.getScaledTouchSlop();//wander偏离值
	}
	@Override//添加尾部
	public void addFooterView(View paramView, Object paramObject,
			boolean paramBoolean) {
		ListView.FixedViewInfo localFixedViewInfo = new ListView.FixedViewInfo();
		localFixedViewInfo.view = paramView;
		//适配器getItem的返回值
		localFixedViewInfo.data = paramObject;
		//是否可选
		localFixedViewInfo.isSelectable = paramBoolean;
		this.mFooters.add(localFixedViewInfo);
		super.addFooterView(paramView, paramObject, paramBoolean);
	}
	@Override//添加头部
	public void addHeaderView(View paramView, Object paramObject,
			boolean paramBoolean) {
		if (this.mHeaderhContainer != paramView) {
			//只有三部分内容--视图--数据--是否可选
			ListView.FixedViewInfo localFixedViewInfo = new ListView.FixedViewInfo();
			localFixedViewInfo.view = paramView;
			localFixedViewInfo.data = paramObject;
			localFixedViewInfo.isSelectable = paramBoolean;
			this.mHeaders.add(localFixedViewInfo);
		}
		super.addHeaderView(paramView, paramObject, paramBoolean);
	}
	@Override//移除所有出具等待新的数据
	public void clearRefreshViewForList() {
		removeHeaderView(this.mHeaderhContainer);
		this.mHeaderhContainer.removeAllViews();
	}
	@Override//清除触摸事件
	public boolean clearTouch(MotionEvent paramMotionEvent) {
		return super.onTouchEvent(paramMotionEvent);
	}
	@Override
	public ListView getListView() {
		return this;
	}
	@Override
	public boolean hasRefreshView() {
		if (this.mHeaderhContainer.getChildCount() <= 0)
			return false;
		else
			return true;
	}
	@Override
	public void setPullRefreshListView(
			PullRefreshListView paramPullRefreshListView) {
		this.mPullRefreshListView = paramPullRefreshListView;
	}
	@Override
	public boolean onTouch(MotionEvent paramMotionEvent) {
		boolean bool;
		if ((this.mPullRefreshListView == null)
				|| (!this.mPullRefreshListView.isInterpreterTouch()))
			bool = super.onTouchEvent(paramMotionEvent);
		else
			bool = true;
		return bool;
	}
	protected void onDrawHorizontalScrollBar(Canvas paramCanvas,
			Drawable paramDrawable, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		paramDrawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
		if ((this.mPullRefreshListView != null)
				&& (this.mPullRefreshListView.getMoveDistance() != 0))
			return;
		paramDrawable.draw(paramCanvas);
	}
	protected void onDrawVerticalScrollBar(Canvas paramCanvas,
			Drawable paramDrawable, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		paramDrawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
		if ((this.mPullRefreshListView != null)
				&& (this.mPullRefreshListView.getMoveDistance() != 0))
			return;
		paramDrawable.draw(paramCanvas);
	}
	@Override//拦截触摸事件
	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		int k = 0;
		int i;
		if ((this.mPullRefreshListView == null)
				|| (!this.mPullRefreshListView.isInterpreterTouch()))
			i = 0;
		else
			i = 1;
		if (i == 0)
			switch (paramMotionEvent.getAction()) {
			case 0:
				this.mLastMotionX = (int) paramMotionEvent.getX();
				break;
			case 2:
				int j = (int) paramMotionEvent.getX() - this.mLastMotionX;
				this.mLastMotionX = (int) paramMotionEvent.getX();
				if (Math.abs(j) > this.mTouchSlop)
					break;
			case 1:
			}
		if ((super.onInterceptTouchEvent(paramMotionEvent)) || (i != 0))
			i = 1;
		else
			i = 0;
		k = i;
		if (k == 0) {
			return false;
		}
		return true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		boolean bool;
		if (this.mPullRefreshListView != null) {
			MotionEvent localMotionEvent = this.mPullRefreshListView
					.getProperMotionEvent(paramMotionEvent);
			bool = this.mPullRefreshListView.onTouch(localMotionEvent);
		} else {
			bool = onTouch(paramMotionEvent);
		}
		return bool;
	}
	@Override
	public boolean removeFooterView(View paramView) {
		for(int i=0;i<mFooters.size();i++){
			ListView.FixedViewInfo tmp=mFooters.get(i);
			removeFooterView(tmp.view);
		}
		return super.removeFooterView(paramView);
	}
	@Override
	public boolean removeHeaderView(View paramView) {
		//帧布局
		if (this.mHeaderhContainer != paramView) {
			if (mHeaders.size() > 0) {
				ListView.FixedViewInfo info = (ListView.FixedViewInfo) mHeaders
						.get(0);
				mHeaders.remove(info);
			}
		}
		return super.removeHeaderView(paramView);
	}
	@Override
	public void setAdapter(ListAdapter paramListAdapter) {
		this.mAdapter = paramListAdapter;
		super.setAdapter(paramListAdapter);
	}
	@Override
	public void setDivider(Drawable paramDrawable) {
		super.setDivider(paramDrawable);
		if ((this.mPullRefreshListView == null)
				|| (this.mPullRefreshListView.getRefreshViewContainer() == null))
			return;
		this.mPullRefreshListView.getRefreshViewContainer().setPadding(0, 0, 0,
				getDividerHeight());
	}
	@Override
	public void setDividerHeight(int paramInt) {
		super.setDividerHeight(paramInt);
		if ((this.mPullRefreshListView == null)
				|| (this.mPullRefreshListView.getRefreshViewContainer() == null))
			return;
		this.mPullRefreshListView.getRefreshViewContainer().setPadding(0, 0, 0,
				getDividerHeight());
	}
	public void setRefreshViewForList(View paramView)
	  {
	    Object localObject1=null;
	    Object localObject2=null;
	    if (this.mHeaderhContainer.getChildAt(0) != paramView)
	    {
	      clearRefreshViewForList();
	      if (paramView != null)
	      {
	        localObject1 = this.mAdapter;
	        Object localObject3 = new ArrayList();
	        ((ArrayList)localObject3).addAll(this.mHeaders);
	        localObject2 = this.mHeaders.iterator();
	        while (((Iterator)localObject2).hasNext())
	          removeHeaderView(((ListView.FixedViewInfo)((Iterator)localObject2).next()).view);
	        localObject2 = new ArrayList();
	        ((ArrayList)localObject2).addAll(this.mFooters);
	        Object localObject4 = this.mFooters.iterator();
	        while (((Iterator)localObject4).hasNext())
	          removeFooterView(((ListView.FixedViewInfo)((Iterator)localObject4).next()).view);
	        this.mHeaderhContainer.addView(paramView);
	        setAdapter(null);
	        addHeaderView(this.mHeaderhContainer);
	        localObject3 = ((ArrayList)localObject3).iterator();
	        while (((Iterator)localObject3).hasNext())
	        {
	          localObject4 = (ListView.FixedViewInfo)((Iterator)localObject3).next();
	          addHeaderView(((ListView.FixedViewInfo)localObject4).view, ((ListView.FixedViewInfo)localObject4).data, ((ListView.FixedViewInfo)localObject4).isSelectable);
	        }
	        setAdapter((ListAdapter)localObject1);
	        localObject1 = ((ArrayList)localObject2).iterator();
	      }
	    }
	    while (true)
	    {
	      if (!((Iterator)localObject1).hasNext())
	        return;
	      localObject2 = (ListView.FixedViewInfo)((Iterator)localObject1).next();
	      addFooterView(((ListView.FixedViewInfo)localObject2).view, ((ListView.FixedViewInfo)localObject2).data, ((ListView.FixedViewInfo)localObject2).isSelectable);
	    }
	  }
}