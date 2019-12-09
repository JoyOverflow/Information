package ouyj.hyena.com.newsclient.server;


import java.util.Map;

import ouyj.hyena.com.newsclient.bean.HttpResponse;
import ouyj.hyena.com.newsclient.bean.JokeEntity;
import ouyj.hyena.com.newsclient.bean.NewsChannel;
import ouyj.hyena.com.newsclient.bean.NewsPageBean;
import ouyj.hyena.com.newsclient.bean.PageBean;
import ouyj.hyena.com.newsclient.bean.ShowapiResBody;
import ouyj.hyena.com.newsclient.bean.WeiboHot;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/17.
 */

public interface ShowApiService {
    @FormUrlEncoded
    @POST("109-34")
    public Observable<HttpResponse<NewsChannel>> loadNewsChannel(@FieldMap Map<String, String>
                                                                             params);

    @FormUrlEncoded
    @POST("109-35")
    public Observable<HttpResponse<ShowapiResBody<NewsPageBean>>> loadNews(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("255-1")
    public Observable<HttpResponse<ShowapiResBody<PageBean<JokeEntity>>>> loadJoke(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("254-1")
    public Observable<HttpResponse<ShowapiResBody<PageBean<WeiboHot>>>> loadWeibo(@FieldMap Map<String, String> params);

}
