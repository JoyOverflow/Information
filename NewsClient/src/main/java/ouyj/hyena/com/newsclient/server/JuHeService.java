package ouyj.hyena.com.newsclient.server;

import java.util.Map;

import ouyj.hyena.com.newsclient.bean.HttpResult;
import ouyj.hyena.com.newsclient.bean.Response;
import ouyj.hyena.com.newsclient.bean.WeiXinEntity;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/15.
 */

public interface JuHeService {
    @FormUrlEncoded
    @POST("toutiao/index")
    public Observable<HttpResult> getSearchNews(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("weixin/query")
    public Observable<Response<WeiXinEntity>> loadWeiXin(@FieldMap Map<String, String> params);
}
