package ouyj.hyena.com.viewgroupdemo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ouyj.hyena.com.viewgroupdemo.R;
import ouyj.hyena.com.viewrolling.DetailsViewGroup;
import ouyj.hyena.com.viewrolling.ICommentLayout;

/**
 * 
 * @Description: 评论列表
 * @version V1.0.0
 */
public class ArticleCommentLayout extends LinearLayout
		implements View.OnClickListener, ICommentLayout {

	private Context mContext;

	protected CommentListView mPullToRefreshListView;

	private ArticleAdapter mArticleAdapter;

	//评论列表
	private List<String> commentList = new ArrayList<>();

	private DetailsViewGroup mDetailsViewGroup;

	/**
	 * 构造方法
	 * @param context
	 * @param detailsViewGroup
	 */
	public ArticleCommentLayout(Context context, DetailsViewGroup detailsViewGroup) {
		super(context);
		mContext = context;
		this.mDetailsViewGroup = detailsViewGroup;
		afterViews();
	}
	public void afterViews() {
		LayoutInflater.from(mContext).inflate(
				R.layout.article_comment, 
				this, 
				true
		);

		//加入评论记录
		for (int i = 0; i < 230; i++){
			String tmp=String.format("家黄豆，野黄豆掉在谁家谁家收~！%d",i);
			commentList.add(tmp);
		}


		mPullToRefreshListView = findViewById(R.id.articleListView);
		mPullToRefreshListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mArticleAdapter = new ArticleAdapter();
		mPullToRefreshListView.setAdapter(mArticleAdapter);
		mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!mDetailsViewGroup.isScrollerFinished()){
					return ;
				}
				Toast.makeText(mContext, "position : " + position, Toast.LENGTH_SHORT).show();
			}
		});
	}



	@Override
	public boolean isFirstViewTop() {
		return mPullToRefreshListView
				.isFirstViewTop();
	}

	@Override
	public void setIsScroll(boolean isScroll) {
		mPullToRefreshListView
				.setIsScroll(isScroll);
	}

	@Override
	public void onClick(View v) {

	}

	class ArticleAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return commentList.size();
		}

		@Override
		public Object getItem(int position) {
			return commentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView textView = null;

			if(null == convertView){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_article, null);

			}else {

			}
			textView = (TextView) convertView.findViewById(R.id.textView);
			textView.setText(commentList.get(position));

			return convertView;
		}
	}

}
