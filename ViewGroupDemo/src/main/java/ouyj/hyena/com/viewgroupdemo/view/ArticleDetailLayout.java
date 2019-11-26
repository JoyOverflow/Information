package ouyj.hyena.com.viewgroupdemo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import ouyj.hyena.com.viewgroupdemo.R;
import ouyj.hyena.com.viewrolling.DetailsViewGroup;
import ouyj.hyena.com.viewrolling.IArticleLayout;

/**
 * 
 * @Title: ArticleDetailsLayout.java
 * @Package hbyc.china.medical.view.modules.home.articleviewpager.fragment
 * @ClassName: ArticleDetailsLayout
 * @Description: 详情
 * @author jiahongfei jiahongfeinew@163.com
 * @date 2015-7-13 下午3:23:22
 * @version V1.0.0
 */
public class ArticleDetailLayout extends LinearLayout implements IArticleLayout {

	private Context mContext;
	private ArticleWebView webView;
	private ViewGroup mArticleDetailsLayout;

	public ArticleDetailLayout(Context context, DetailsViewGroup viewGroup) {
		super(context);

		//获取到上下文
		mContext = context;
		//加载布局
		LayoutInflater.from(mContext).inflate(
				R.layout.article_detail,
				this,
				true
		);

		webView = new ArticleWebView(mContext);
		webView.loadUrl("http://www.sohu.com/a/354400460_463728");
		//使用自定义WebView来加载URL，并且页面链接的跳转也限制在WebView中，不再使用默认浏览器打开
		webView.setWebViewClient(new WebViewClient(){
			//在点击页面链接加载新URL时添加额外控制（true=系统来处理URL，false=当前WebView处理URL）
			//默认为false
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}
		});
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT
		);
		webView.setLayoutParams(params);

		//获取相对布局引用，在其内加入浏览器组件
		mArticleDetailsLayout = findViewById(R.id.articleDetailsLayout);
		mArticleDetailsLayout.removeAllViews();
		mArticleDetailsLayout.addView(webView);
	}

	@Override
	public void setIsScroll(boolean isScroll) {
		webView.setIsScroll(isScroll);
	}
	@Override
	public boolean isScrollBottom() {
		return webView.isScrollBottom();
	}
}
