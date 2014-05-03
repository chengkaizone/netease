package sutras.cheng.listener;

/**
 * 用于拖拉ListView的监听器
 * 
 * @author chengkai
 * 
 */
public interface OnDragListViewListener {
	public void drag(int from, int to);

	public void drop(int from, int to);

	public void remove(int which);
}
