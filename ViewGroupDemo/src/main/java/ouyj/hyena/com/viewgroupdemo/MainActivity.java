package ouyj.hyena.com.viewgroupdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import ouyj.hyena.com.viewgroupdemo.view.ArticleCommentLayout;
import ouyj.hyena.com.viewgroupdemo.view.ArticleDetailLayout;
import ouyj.hyena.com.viewrolling.DetailsViewGroup;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //查找视图引用
        DetailsViewGroup detailGroup = findViewById(R.id.detailsViewGroup);
        ArticleDetailLayout detailLayout = new ArticleDetailLayout(this,detailGroup);
        ArticleCommentLayout commentLayout = new ArticleCommentLayout(this,detailGroup);
        //将实现IArticleLayout和ICommentLayout接口的View放入ViewGroup容器中
        detailGroup.addArticleView(detailLayout);
        detailGroup.addCommentView(commentLayout);

        //视图requestLayout方法为请求视图重新进行布局（会导致View.onMeasure、onLayout、onDraw方法被调用）
        //而invalidate方法则只会导致View.onDraw方法被调用
        detailGroup.requestLayout();
    }
}