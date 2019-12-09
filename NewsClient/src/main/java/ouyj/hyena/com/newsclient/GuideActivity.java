package ouyj.hyena.com.newsclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.btn_entry)
    Button btnEntry;

    @BindView(R.id.layout_points)
    LinearLayout layoutPoints;

    private ImageView[] pointImages;
    private int[] imageIds = new int[]{
            R.drawable.image2,
            R.drawable.image4,
            R.drawable.image5,
    };
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //显示登录页
        sp = getSharedPreferences("guide", Context.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isFirst", true);
        if (!isFirst) {
            Intent intent = new Intent("activity.login.news");
            startActivity(intent);
            finish();
        }




        setContentView(R.layout.activity_guide);

        ButterKnife.bind(this);
        initView();
        setViewPagerAdapter();
    }

    private void initView() {
        int count = layoutPoints.getChildCount();

        pointImages = new ImageView[count];
        for (int i = 0; i < count; i++) {
            pointImages[i] = (ImageView) layoutPoints.getChildAt(i);
            pointImages[i].setTag(i);
            pointImages[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int item = (Integer) v.getTag();
                    viewPager.setCurrentItem(item);
                }
            });
        }
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                selectedPosition(position);
                if (position == 2) {
                    btnEntry.setVisibility(View.VISIBLE);
                } else {
                    btnEntry.setVisibility(View.GONE);
                }
            }
        });
    }
    private void selectedPosition(int position) {
        for (int i = 0; i < imageIds.length; i++) {
            pointImages[i].setImageResource((position == i) ? R.drawable.point_select : R.drawable.point_normal);
        }
    }




    private void setViewPagerAdapter() {

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pointImages.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackgroundResource(imageIds[position]);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                return imageView;
            }
        });
    }

    /**
     * 点击立即进入按钮
     */
    @OnClick(R.id.btn_entry)
    public void onClick() {
        //是否为第一次启动
        sp.edit().putBoolean("isFirst", false).apply();
        //发送隐式意图（未有接收者会发生异常，应用退出）
        Intent intent = new Intent("activity.login.news");
        startActivity(intent);
        finish();
    }



}
