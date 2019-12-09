package ouyj.hyena.com.newsclient.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ouyj.hyena.com.newsclient.R;
import ouyj.hyena.com.newsclient.adapter.JokeAdapter;
import ouyj.hyena.com.newsclient.bean.HttpResponse;
import ouyj.hyena.com.newsclient.bean.JokeEntity;
import ouyj.hyena.com.newsclient.bean.PageBean;
import ouyj.hyena.com.newsclient.bean.ShowapiResBody;
import ouyj.hyena.com.newsclient.server.ShowApiService;
import ouyj.hyena.com.newsclient.utils.Commons;
import ouyj.hyena.com.newsclient.utils.Tools;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class JokeTypeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "JokeTypeFragment";

    @BindView(R.id.listView)
    PullToRefreshListView listView;
    @BindView(R.id.loading_indicator)
    AVLoadingIndicatorView loadingIndicator;
    private Subscriber<List<JokeEntity>> subscriber;
    private String type;
    private JokeAdapter adapter;
    private int page = 1;
    private List<JokeEntity> jokeEntityList = new ArrayList<>();
    private String search = null;
    private SearchReceiver receiver;
    private PageBean<JokeEntity> pageBean;

    public static JokeTypeFragment getInstance(String type) {
        JokeTypeFragment jokeTypeFragment = new JokeTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        jokeTypeFragment.setArguments(bundle);
        return jokeTypeFragment;
    }

    public JokeTypeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        type = bundle.getString("type");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joke_type, container, false);
        ButterKnife.bind(this, view);
        initPullToRefreshListView();
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        receiver = new SearchReceiver();
        IntentFilter filter = new IntentFilter("search");
        getActivity().registerReceiver(receiver, filter);
        listView.setOnItemClickListener(this);
        loadData(0);
    }
    private void initPullToRefreshListView() {
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        // 下拉刷新
        listView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
        listView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
        listView.getLoadingLayoutProxy(true, false).setReleaseLabel("释放立即刷新");
        // 下拉加载
        listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放立即加载");
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                loadData(0);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                if (page <= pageBean.getAllPages()) {
                    page++;
                    loadData(1);
                }
            }
        });
    }

    private void loadData(int what) {
        loadingIndicator.show();
        subscriber = getSubscriber(what);
        Map<String, String> params = new HashMap<>();
        params.put("showapi_appid", Commons.SHOW_API_APPID);
        params.put("showapi_sign", Commons.SHOW_API_SECRET);
        params.put("type", type);
        if (search != null) {
            params.put("title", search);
        }
        params.put("page", String.valueOf(page));
        Tools.getRetrofit()
                .create(ShowApiService.class)
                .loadJoke(params)
                .map(new Func1<HttpResponse<ShowapiResBody<PageBean<JokeEntity>>>, List<JokeEntity>>() {
                    @Override
                    public List<JokeEntity> call(HttpResponse<ShowapiResBody<PageBean<JokeEntity>>> showapiResBodyHttpResponse) {
                        if (showapiResBodyHttpResponse.getShowapiResCode() != 0) {
                            throw new RuntimeException("showapi_res_code:" + showapiResBodyHttpResponse.getShowapiResCode() + ",showapi_res_error" + showapiResBodyHttpResponse.getShowapiResError());
                        }
                        pageBean = showapiResBodyHttpResponse.getShowapiResBody().getPagebean();
                        return showapiResBodyHttpResponse.getShowapiResBody().getPagebean().getContentlist();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(what));
    }

    private Subscriber<List<JokeEntity>> getSubscriber(final int what) {
        return new Subscriber<List<JokeEntity>>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
                listView.onRefreshComplete();
                loadingIndicator.hide();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(List<JokeEntity> jokeEntities) {
                Log.i(TAG, "onNext: " + jokeEntities.toString());
                if (what == 0) {
                    jokeEntityList.clear();
                    jokeEntityList.addAll(jokeEntities);
                    adapter = new JokeAdapter(getActivity(), jokeEntityList);
                    listView.setAdapter(adapter);
                } else if (what == 1) {
                    jokeEntityList.addAll(jokeEntities);
                    adapter.notifyDataSetChanged();
                }
            }
        };
    }










    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startWebActivity(jokeEntityList.get(i - 1).getWeixinUrl());
    }
    @Override
    public void onStop() {
        super.onStop();
        if (subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            search = intent.getStringExtra("search");
            Log.i(TAG, "onReceive: search=" + search);
            loadData(0);
        }
    }
}
