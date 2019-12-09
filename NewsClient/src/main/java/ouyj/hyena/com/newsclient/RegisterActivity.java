package ouyj.hyena.com.newsclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import ouyj.hyena.com.newsclient.bean.User;
import ouyj.hyena.com.newsclient.db.UserDao;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener{

    private static final String TAG = "RegisterActivity";
    private static final String APPKEY = "17ccfa22b2003";
    private static final String APPSECRET = "4233a3ce5a09d48a703c057a49a3880e";


    @BindView(R.id.txt_phone)
    @NotEmpty(message = "号码不能为空", trim = true)
    EditText txtPhone;
    @BindView(R.id.btn_clear)
    Button btnClear;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.et_username)
    @NotEmpty(message = "用户名不能为空")
    EditText etUsername;
    @BindView(R.id.txt_password)
    @Password(message = "密码不能为空，且不小于6位")
    EditText txtPassword;
    @BindView(R.id.et_email)
    @Email(message = "邮箱格式不正确")
    EditText etEmail;
    @BindView(R.id.btn_eye)
    Button btnEye;
    @BindView(R.id.et_sms_code)
    @NotEmpty(trim = true, message = "请输入手机验证码")
    EditText etSmsCode;
    @BindView(R.id.btn_sms_code)
    Button btnSmsCode;
    private boolean isVisible = false;
    private EventHandler eventHandler;
    private Validator validator;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    //已接收到验证码
                    boolean isUse = (boolean) msg.obj;
                    if (isUse) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "手机号已注册！",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                RegisterActivity.this,
                                "发送验证码成功！",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;
                case 0x002:
                    //创建用户对象
                    String userName = etUsername.getText().toString().trim();
                    String userPassword = txtPassword.getText().toString().trim();
                    String userEmail = etEmail.getText().toString().trim();
                    String phone = txtPhone.getText().toString().trim();
                    User user = new User();
                    user.setUserName(userName);
                    user.setUserPassword(userPassword);
                    user.setUserEmail(userEmail);
                    user.setUserPhone(phone);
                    //加入用户
                    UserDao userDao = new UserDao(RegisterActivity.this);
                    boolean result = userDao.addUser(user);
                    if (result) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "用户注册成功！",
                                Toast.LENGTH_SHORT
                        ).show();

                        //发送意图启动登录页面
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("user", user);
                        setResult(0x002, intent);
                        //关闭当前页
                        finish();
                    } else {
                        Toast.makeText(
                                RegisterActivity.this,
                                "注册失败，请重新注册！",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;
                case 0x003:
                    //详细的错误描述
                    String des = (String) msg.obj;
                    Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                    break;
                case 0x004:
                    Toast.makeText(
                            RegisterActivity.this,
                            "数据解析异常，请稍后再试！",
                            Toast.LENGTH_SHORT
                    ).show();
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        //ouyj
        //initSMSSDK();


        //文本框事件监听（电话号码）
        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            //文本内容发生改变后
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    btnClear.setVisibility(View.GONE);
                    btnSubmit.setEnabled(false);
                    btnSmsCode.setEnabled(false);
                } else {
                    btnClear.setVisibility(View.VISIBLE);
                    btnSubmit.setEnabled(true);
                    btnSmsCode.setEnabled(true);
                }
            }
        });
    }
    private void initSMSSDK() {
        SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
        //短信验证后执行事件
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //验证码校验通过时触发（获取反馈数据）
                        HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                        String country = (String) phoneMap.get("country");
                        String phone = (String) phoneMap.get("phone");

                        //加入用户
                        handler.sendEmptyMessage(0x002);
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //验证码已发送成功（获取反馈数据）
                        boolean isUse = (boolean) data;

                        Message msg = Message.obtain();
                        msg.what = 0x001;
                        msg.obj = isUse;
                        handler.sendMessage(msg);
                    }
                    else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        ArrayList<HashMap<String, Object>> countrys = (ArrayList<HashMap<String, Object>>) data;
                    }
                }
                else {
                    //出现错误
                    Throwable throwable = ((Throwable) data);
                    throwable.printStackTrace();
                    try {
                        //获取详细的错误描述
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");
                        Message msg = Message.obtain();
                        msg.what = 0x003;
                        msg.obj = des;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        //解析Json出现错误
                        handler.sendEmptyMessage(0x004);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }



    @OnClick({R.id.img_back, R.id.btn_clear, R.id.btn_submit, R.id.btn_eye, R.id.btn_sms_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_clear:
                txtPhone.setText("");
                break;
            case R.id.btn_submit:
                //注册
                Log.d(TAG, "btn_submit onclick！");
                validator.validate();
                break;
            case R.id.btn_eye:
                //设置密码是否可见
                if (isVisible) {
                    isVisible = false;
                    btnEye.setBackgroundResource(R.drawable.eye_unenable);
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    isVisible = true;
                    btnEye.setBackgroundResource(R.drawable.eye_enable);
                    txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
            case R.id.btn_sms_code:
                //申请短信验证码
                String phone=txtPhone.getText().toString().trim();
                validatePhone(phone);
                break;
            default:
                break;
        }
    }
    private void validatePhone(String phone) {
    }


    /**
     * 文本框输入验证通过（不为空）时触发
     */
    @Override
    public void onValidationSucceeded() {
        String phone = txtPhone.getText().toString().trim();
        String code = etSmsCode.getText().toString().trim();
        String result=String.format("电话：%s，验证码：%s",phone,code);

        //提交电话号码和验证码（送往短信服务端去验证）
        //SMSSDK.submitVerificationCode("86", phone, code);
        Log.d(TAG, "onValidationSucceeded！ " + result);

        //加入用户（由ouyj临时添加）
        //handler.sendEmptyMessage(0x002);
    }
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Log.d(TAG, "onValidationFailed！");
        for(ValidationError error : errors) {
            //异常信息
            String message = error.getCollatedErrorMessage(this);
            //显示异常信息
            View view = error.getView();
            if (view instanceof EditText)
                ((EditText) view).setError(message);
            else {
                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消短信组件的注册
        if(eventHandler != null)
            SMSSDK.unregisterEventHandler(eventHandler);
    }
}
