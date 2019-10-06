package ouyj.hyena.com.jobcenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        //通过JsonObject创建原生Json对象
        JSONObject users = new JSONObject();
        try {
            users.put("name","ouyj1");
            users.put("age", 42);
            users.put("gender", true);
            users.put("salary", null);
            JSONArray lans = new JSONArray();
            lans.put(0,"Java");
            lans.put(1,"Python");
            lans.put(2,"Swift");
            users.put("languages",lans);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, users.toString());
        */


        //Json字串
        String data="{\"name\":\"ouyj\",\"age\":42,\"gender\":true," +
                "\"languages\":[\"Java\",\"Python\",\"Swift\"]}";

        //当读取的键不存在时会抛异常
        //键值类型与读取类型不符会抛异常
        try {
            JSONObject user = new JSONObject(data);
            //读取指定的键（键值为对象）
            //JSONObject user = users.getJSONObject("user");

            //返回字串（键值为字串）
            String name=user.getString("name");
            //返回int（其内部为调用Integer.parseeInt）
            int age=user.getInt("age");
            //返回布尔（键值必须为true或false）
            boolean male=user.getBoolean("gender");

            String result=String.format("我的名字是：%s，年龄：%d，性别：%b",name,age,male);
            Log.d(TAG, result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
