package ouyj.hyena.com.newsclient.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ouyj.hyena.com.newsclient.R;
import ouyj.hyena.com.newsclient.adapter.MainPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.pager_type)
    ViewPager pagerType;
    @BindView(R.id.tab_indicator)
    TabPageIndicator tabIndicator;
    private List<Fragment> fragments;
    private String[] titles = new String[]{
            "头条", "社会", "国内", "国际", "娱乐",
            "体育", "军事", "科技", "财经", "时尚"
    };

    /**
     * 构造方法
     */
    public HomeFragment() {
    }
    /**
     * 片段创建视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    /**
     * 片段创建完视图的回调
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initNewsTypeFragment();
    }
    private void initNewsTypeFragment() {
        //加入以下片段列表
        fragments = new ArrayList<>();
        fragments.add(NewsFragment.getInstance("top"));
        fragments.add(NewsFragment.getInstance("shehui"));
        fragments.add(NewsFragment.getInstance("guonei"));
        fragments.add(NewsFragment.getInstance("guoji"));
        fragments.add(NewsFragment.getInstance("yule"));
        fragments.add(NewsFragment.getInstance("tiyu"));
        fragments.add(NewsFragment.getInstance("junshi"));
        fragments.add(NewsFragment.getInstance("keji"));
        fragments.add(NewsFragment.getInstance("caijing"));
        fragments.add(NewsFragment.getInstance("shishang"));
        //创建适配器
        MainPagerAdapter adapter = new MainPagerAdapter(getChildFragmentManager(), fragments, titles);
        pagerType.setAdapter(adapter);
        tabIndicator.setViewPager(pagerType);
    }





}
