package com.supermap.mytracks.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import java.util.Timer;
import java.util.TimerTask;
import com.supermap.mytracks.R;
import com.supermap.mytracks.common.Params;
import com.supermap.mytracks.customUI.CustomProgressDialog;
import com.supermap.mytracks.utils.CommonUtil;
import com.supermap.mytracks.utils.DialogUtils;
import com.supermap.mytracks.utils.LoginActive;

/**
 * <p>
 * 登录界面
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @ViewInject(R.id.tv_header_title)
    private TextView tv_header_title;
    private Handler handler = null;
    @ViewInject(R.id.edt_login_username)
    private EditText edt_login_username;
    @ViewInject(R.id.edt_login_pwd)
    private EditText edt_login_pwd;
    private CustomProgressDialog pg;
    private String userName = null;
    @ViewInject(R.id.lv_login_clear)
    private ImageView lv_login_clear;
    // 输入法状态
    private boolean isInputMethodOpened;
    private InputMethodManager imm;
    private View focusedView;
    private long lastTime;
    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (pg != null) {
                DialogUtils.stopProgressDialog(pg);
            }
            switch (msg.what) {
            case Params.SUCCESS:// 登录成功
                saveLoginInfo();
                Toast.makeText(context, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                // todo 跳转到创建地图的页面
                Intent intent = new Intent(LoginActivity.this,CreateMapActivity.class);
                startActivity(intent);
//                finish();
                break;
            case Params.ERROR:// 登录失败
                if (!CommonUtil.isNetworkConnected(context)) {
                    Toast.makeText(context, getString(R.string.tip_no_network), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getString(R.string.login_failed_user_or_pwd_error), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pg != null) {
            DialogUtils.stopProgressDialog(pg);
        }
    }

    /**
     * <p>
     * 初始化视图
     * </p>
     * @since 1.0.0
     */
    private void initView() {
        handler = new LoginHandler();
        // 输入法
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 标题
        tv_header_title.setText(getString(R.string.login));
        SharedPreferences sp = getSharedPreferences(Params.NAME, MODE_PRIVATE);
        String username = sp.getString(Params.USERNAME, "");
        if (!TextUtils.isEmpty(username)) {
            edt_login_username.setText(username);
            edt_login_pwd.requestFocus();
        } else {
            edt_login_username.requestFocus();
        }
        edt_login_username.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    lv_login_clear.setVisibility(View.GONE);
                }
                if (hasFocus && !TextUtils.isEmpty(edt_login_username.getText())) {
                    lv_login_clear.setVisibility(View.VISIBLE);
                }
            }
        });

        edt_login_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(edt_login_username.getText().toString())) {
                    // 添加清除图片
                    lv_login_clear.setVisibility(View.GONE);
                } else {
                    lv_login_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
    }

    @OnClick({ R.id.btn_login, R.id.lv_login_clear })
    private void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_login://登录
            login();
            break;
        case R.id.lv_login_clear:// 清除用户名全部内容
            edt_login_username.setText("");
            break;
        }
    }

    /**
     * <p>
     * 实现登录
     * </p>
     * @since 1.0.0
     */
    private void login() {
        this.userName = edt_login_username.getText().toString();
        String pwd = edt_login_pwd.getText().toString();
        if (!isInputAvailable(userName, pwd)) {
            Toast.makeText(context, getString(R.string.login_input_inavailable), Toast.LENGTH_SHORT).show();
            return;
        }
        LoginActive.iClouldlogin(userName, pwd, handler);
        pg = DialogUtils.startProgressDialog(context, getString(R.string.login_logining));
        pg.setCanceledOnTouchOutside(false);
    }

    /**
     * <p>
     * 保存登录后的用户名以及登录成功的cookie_Jessionid（后续任何请求都需要在请求头添加已登录的cookie_Jessionid）
     * </p>
     * @since 1.0.0
     */
    private void saveLoginInfo() {
        SharedPreferences sp = getSharedPreferences(Params.NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // 用户名
        String userName = edt_login_username.getText().toString();
        editor.putString(Params.USERNAME, userName);
        editor.putString(Params.COOKIE_JESSIONID, Params.cookie_Jessionid);
        editor.putLong(Params.LOGINTIME, System.currentTimeMillis()); // 添加login时间，用于判断用户的登陆过期时间
        editor.commit();
    }

    /**
     * <p>
     * 判断用户名和密码是否为空
     * </p>
     * @param userName
     * @param pwd
     * @return
     * @since 1.0.0
     */
    private boolean isInputAvailable(String userName, String pwd) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        isInputMethodOpened = imm.isActive();// 记录输入法的状态
        if (isInputMethodOpened) {
            imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);// InputMethodManager.HIDE_NOT_ALWAYS
        }
        focusedView = LoginActivity.this.getCurrentFocus();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInputMethodOpened) {
            // imm.showSoftInput(search_keywords, 0);
            // imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (focusedView == null) {
                        focusedView = edt_login_username;
                    }
                    imm.showSoftInput(focusedView, 0);
                    isInputMethodOpened = true;
                }
            }, 700);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTime > 2000) {
            lastTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            this.finish();
            System.exit(0);
        }
    }
}
