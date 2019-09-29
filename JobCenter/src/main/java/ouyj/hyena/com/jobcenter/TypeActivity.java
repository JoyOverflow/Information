package ouyj.hyena.com.jobcenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ouyj.hyena.com.pocenter.databinding.ActivityTypeBinding;
import ouyj.hyena.com.pocenter.features.contract.TypeContract;
import ouyj.hyena.com.pocenter.features.contract.TypePresenter;

public class TypeActivity extends MvpActivity<TypeContract.View, TypeContract.Presenter>
        implements TypeContract.View {

    private ActivityTypeBinding bind;
    private MyTagAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_type);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_type);

        initView();
        presenter.loadData();
        initEvent();
    }
    private void initView() {
        setSupportActionBar(bind.tb);
    }
    private void initEvent() {
        bind.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<Integer> list = bind.tfl.getSelectedList();
                if (list.isEmpty()) {
                    Toast.makeText(
                            TypeActivity.this,
                            R.string.toast_select_one_or_more_type,
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }
                //保存选中项
                MyApplication.getRepertories().setTypes(list);

                //重定向到主活动页
                if (presenter.isFirstSetting())
                    startActivity(new Intent(TypeActivity.this, MainActivity.class));

                //关闭当前活动
                finish();
            }
        });
    }



    /**
     * 实现MvpActivity接口
     * @return
     */
    @NonNull
    @Override
    public TypeContract.Presenter createPresenter() {
        return new TypePresenter();
    }

    /**
     * 实现TypeContract.View接口
     * @param data
     * @param types
     */
    @Override
    public void setData(List<String> data, int[] types) {
        adapter = new MyTagAdapter(data);
        bind.tfl.setAdapter(adapter);
        // set selected list
        if (null != types)
            adapter.setSelectedList(types);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_type, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.i_select_all:
                selectAll();
                break;
            case R.id.i_cancel_all:
                cancelAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void selectAll() {
        int count = adapter.getCount();
        int[] list = new int[count];
        for (int i = 0; i < count; i++) {
            list[i] = i;
        }
        adapter.setSelectedList(list);
    }
    private void cancelAll() {
        adapter.setSelectedList(new HashSet<Integer>(0));
    }



    private static class MyTagAdapter extends TagAdapter<String> {
        public MyTagAdapter(List<String> data) {
            super(data);
        }
        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.view_type,
                    parent,
                    false
            );
            tv.setText(s);
            return tv;
        }
    }

}
