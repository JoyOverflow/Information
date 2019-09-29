package ouyj.hyena.com.jobcenter;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.allenliu.versionchecklib.AVersionService;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import ouyj.hyena.com.pocenter.common.bean.VersionBean;
import ouyj.hyena.com.pocenter.common.utils.UnsafeOkHttpUtils;

public class VersionService extends AVersionService {

    public static final String SHOW_LAST = "showLast";
    private boolean showLast = false;

    /**
     * 构造方法
     */
    public VersionService() {
        OkHttpUtils.initClient(UnsafeOkHttpUtils.getClient());
    }
    /**
     * 不使用绑定（非绑定服务）
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //取意图内数据
        showLast = intent.getBooleanExtra(SHOW_LAST, showLast);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 重写AVersionService的方法
     * @param service
     * @param response（服务端的返回数据）
     */
    @Override
    public void onResponses(AVersionService service, String response) {
        //从返回串中获取版本对象
        Gson gson = new Gson();
        VersionBean versionBean = gson.fromJson(response, VersionBean.class);

        //对比应用的当前版本号（存放于modle.gradle文件中）
        if (versionBean.versionCode > BuildConfig.VERSION_CODE) {
            //显示新版本消息和下载地址
            service.showVersionDialog(versionBean.apkUrl, versionBean.changeLog);
        } else {
            //已经是最新版本了
            if (showLast) {
                Toast.makeText(
                        service.getApplicationContext(),
                        R.string.tips_already_last_version,
                        Toast.LENGTH_SHORT
                ).show();
            }
            //当不进行版本升级时手动关闭service（版本升级时会自动关闭）
            stopSelf();
        }
    }
}