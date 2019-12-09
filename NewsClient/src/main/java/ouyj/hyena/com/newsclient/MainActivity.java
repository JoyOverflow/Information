package ouyj.hyena.com.newsclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ouyj.hyena.com.newsclient.fragment.HomeFragment;
import ouyj.hyena.com.newsclient.fragment.JokeFragment;
import ouyj.hyena.com.newsclient.fragment.WeiXinFragment;
import ouyj.hyena.com.newsclient.fragment.WeiboFragment;
import ouyj.hyena.com.newsclient.utils.CommonTools;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<Fragment> fragments;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    private LinearLayout[] linearLayouts;
    private int childCount;
    private FragmentManager manager;
    private long exitTime = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置沉浸模式的状态栏
        CommonTools.initSystemBar(this, R.color.blue);
        ButterKnife.bind(this);

        //片段管理器
        manager = getSupportFragmentManager();
        //初始化片段
        initFragment();
        //初始化底部导航栏
        initBottomLayout();
    }

    /**
     * 初始化片段
     */
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new JokeFragment());
        fragments.add(new WeiboFragment());
        fragments.add(new WeiXinFragment());
        manager.beginTransaction()
                .add(R.id.layout_container, fragments.get(0))
                .add(R.id.layout_container, fragments.get(1))
                .add(R.id.layout_container, fragments.get(2))
                .add(R.id.layout_container, fragments.get(3))
                .hide(fragments.get(1))
                .hide(fragments.get(2))
                .hide(fragments.get(3))
                .commit();
    }

    /**
     * 初始化底部导航栏
     */
    private void initBottomLayout() {
        childCount = layoutBottom.getChildCount();
        linearLayouts = new LinearLayout[childCount];
        for (int i = 0; i < childCount; i++) {
            linearLayouts[i] = (LinearLayout) layoutBottom.getChildAt(i);
            linearLayouts[i].setTag(i);
            linearLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    setSelectedState(position);
                }
            });
        }
        linearLayouts[0].setSelected(true);
    }
    //点击选项卡
    private void setSelectedState(int position) {
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        for (int i = 0; i < childCount; i++) {
            linearLayouts[i].setSelected(i == position);
            fragmentTransaction.hide(fragments.get(i));
        }
        fragmentTransaction.show(fragments.get(position)).commit();
    }


    /**
     * 快速点按退出应用
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(
                    this,
                    "再按一次退出程序",
                    Toast.LENGTH_SHORT
            ).show();
            exitTime = System.currentTimeMillis();
        }
        else
            super.onBackPressed();
    }
}
