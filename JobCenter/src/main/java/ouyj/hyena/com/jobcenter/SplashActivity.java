package ouyj.hyena.com.jobcenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class SplashActivity extends AppCompatActivity {

    private static final long SHOW_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Observable.just(new Object())
                .delay(SHOW_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        redirect();
                    }
                });
    }
    private void redirect() {
        //未有设置则重定向到类型页
        if (!MyApplication.getRepertories().isSettingTypes())
            startActivity(new Intent(this, TypeActivity.class));
        else
            startActivity(new Intent(this, MainActivity.class));

        //关闭当前活动
        finish();
    }


}
