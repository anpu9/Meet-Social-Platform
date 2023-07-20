package com.tianyuyang.meet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.meet.MainActivity;
import com.imooc.meet.R;
import com.liuguilin.framework.base.BaseUIActivity;
import com.liuguilin.framework.bmob.BmobManager;
import com.liuguilin.framework.bmob.IMUser;
import com.liuguilin.framework.entity.Constants;
import com.liuguilin.framework.utils.LogUtils;
import com.liuguilin.framework.utils.SpUtils;
import com.liuguilin.framework.view.LodingView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * FileName: LoginActivity
 * Founder: tianyuyang
 * Profile: 登录页
 */
public class LoginActivity extends BaseUIActivity implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_code;
    private Button btn_send_code;
    private Button btn_login;

    private TextView tv_test_login;
    private LodingView mLoadingView;

    /**
     *1.点击发送的按钮。图片验证码通过后
     * 2。发送验证码，同时按钮变成不可点击，按钮开始倒计时，
     * 3。通过手机号码和验证进行登陆 login()
     * 4。登陆后获取本地对象 getUser()
     */
    private static final int H_TIME = 1001;
    //60s倒计时
    private static int TIME = 60;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case H_TIME:
                    TIME -= 1;
                    btn_send_code.setText(TIME + "s");
                    if (TIME > 0) {
                        mHandler.sendEmptyMessageDelayed(H_TIME, 1000);
                    } else {
                        btn_send_code.setEnabled(true);
                        btn_send_code.setText(getString(R.string.text_login_send));
                        TIME = 60;
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    public void initView(){
        initDaialogView();
        tv_test_login = findViewById(R.id.tv_test_login);
        tv_test_login.setOnClickListener(this);

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_send_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);


        String phone = SpUtils.getInstance().getString(Constants.SP_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            et_phone.setText(phone);
        }
        LogUtils.i("entered login page");
    }

    private void initDaialogView() {
        mLoadingView = new LodingView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_code:
                sendSMS();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_test_login:
                startActivity(new Intent(this, TestLoginActivity.class));
                break;
        }
    }

    public void login() {
        //1.手机号码和手机不为空
        String phone = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }
        String code = et_code.getText().toString().trim();
        if(TextUtils.isEmpty(code)) {
            Toast.makeText(this, getString(R.string.text_login_code_null), Toast.LENGTH_SHORT).show();
            return;
        }
        mLoadingView.show("正在登录");
        BmobManager.getInstance().signOrLoginByMobilePhone(phone, code, new LogInListener<IMUser>() {
            @Override
            public void done(IMUser imUser, BmobException e) {
                if(e == null){
                    //登陆成功
                    mLoadingView.hide();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    //保存手机号
                    SpUtils.getInstance().putString(Constants.SP_PHONE, phone);
                    LogUtils.i("login successfully!");

                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,"ERROR"+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    /**
     * 发送短信验证码
     */
    public void sendSMS() {
        //1.获取手机号码
        String phone = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }
        //2。请求短信验证码
        BmobManager.getInstance().requestSMS(phone, new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e == null) {
                    btn_send_code.setEnabled(false);
                    mHandler.sendEmptyMessage(H_TIME);
                    Toast.makeText(LoginActivity.this,
                            "短信验证码发送成功",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this,
                            "短信验证码发送失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
