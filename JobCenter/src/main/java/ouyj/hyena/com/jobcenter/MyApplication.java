package ouyj.hyena.com.jobcenter;

import android.app.Application;
import android.content.Context;

import ouyj.hyena.com.pocenter.common.api.Repertories;

public class MyApplication extends Application {

    private static Context context;
    private static Repertories repertories;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        repertories = new Repertories(context.getCacheDir());

        tinkerPatchConfig();
        initLogger();
        initLeakCanary();
    }
    public static Context getContext() {
        return context;
    }
    public static Repertories getRepertories() {
        return repertories;
    }

    /**
     *  热修复框架
     */
    private void tinkerPatchConfig() {

    }
    /**
     * 内存泄露检测
     */
    private void initLeakCanary() {

    }
    /**
     * 开源的日志框架
     */
    private void initLogger() {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
