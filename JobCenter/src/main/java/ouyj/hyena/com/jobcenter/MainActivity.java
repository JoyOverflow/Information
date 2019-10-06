package ouyj.hyena.com.jobcenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //创建JsonObject对象
        JSONObject users = new JSONObject();
        try {
            users.put("name","ouyj1");
            users.put("age", 42);
            users.put("gender", true);
            users.put("salary", 25000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
