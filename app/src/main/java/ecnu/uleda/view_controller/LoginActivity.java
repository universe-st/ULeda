package ecnu.uleda.view_controller;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.UTaskManager;
import ecnu.uleda.function_module.UserConfig;
import ecnu.uleda.function_module.UserOperatorController;


/**
 * Created by Shensheng on 2016/10/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MSG_LOGIN = 0;
    private static final int MSG_ANIM = 1;
    private static final String[] sLoginWaitDot = {"", " . ", " . . ", " . . . ", " . . . "};

    private UserConfig mUserConfig;

    private Button mLogin;
    private EditText mUserName;
    private EditText mPassword;
    private PopupWindow mPopupWindow;
    private TextView mRegister;
    private TextView mPasswordForget;
    private Button FindPassWord;
    private Button MessageLogin;
    private Button CancelFindBack;

    private ValueAnimator mAnimator;

    long mExitTime = System.currentTimeMillis();
    int keyCode;
    KeyEvent event;
    private boolean mIsWaiting = false;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOGIN: {
                    mIsWaiting = false;
                    mAnimator.cancel();
                    if (mUOC.getIsLogined()) {
                        Intent intent = new Intent(LoginActivity.this, UMainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        Toast.makeText(LoginActivity.this, "欢迎您，" + mUOC.getUserName() + "！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "登陆错误：" + mUOC.getMessage(), Toast.LENGTH_SHORT).show();
                        setAllEnabled(true);
                        mLogin.setText("登 陆");
                    }
                    break;
                }
            }
        }
    };
    private UserOperatorController mUOC = UserOperatorController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mUserConfig = UserConfig.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        onKeyDown(keyCode, event);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(0xFFFDFDFD);
        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(requestCode == 1)
        {
            Toast.makeText(LoginActivity.this,"自动登陆",
                    Toast.LENGTH_SHORT).show();
            LoginUser();
        }
    }
    private void LoginUser()
    {
        setAllEnabled(false);
        mIsWaiting = true;
        mAnimator.start();
        mUserConfig.setSavedUsernamePassword(mUserName.getText().toString(), mPassword.getText().toString());
        new Thread() {
            @Override
            public void run() {
                mUOC.login(mUserName.getText().toString(), mPassword.getText().toString());
                Log.d("LoginActivity", mUOC.getMessage());
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }.start();
    }
    protected void init() {
        mLogin = (Button) findViewById(R.id.login_button);
        mUserName = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.password);
        mRegister = (TextView) findViewById(R.id.login_text);

        mPasswordForget = (TextView) findViewById(R.id.password_forgotten);

        //测试
        mUserName.setText(mUserConfig.getSavedUsername());
        mPassword.setText(mUserConfig.getSavedPassword());
        //测试
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LoginUser();
            }
        });
        mLogin.setText("登 陆");
        mRegister.setOnClickListener(this);
        mPasswordForget.setOnClickListener(this);
        mAnimator = ValueAnimator.ofInt(0, sLoginWaitDot.length - 1)
                .setDuration(300 * (sLoginWaitDot.length - 1));
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                if (mIsWaiting) {
                    mLogin.setText(sLoginWaitDot[val] + "登 陆" + sLoginWaitDot[val]);
                } else {
                    animation.cancel();
                }
            }
        });
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }


    private void setAllEnabled(boolean a) {
        mUserName.setEnabled(a);
        mPassword.setEnabled(a);
        mLogin.setEnabled(a);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
                Intent intent = new Intent(this, UMainActivity.class);
                intent.putExtra(UMainActivity.TAG_EXIT, true);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_text: {
                Intent i = new Intent(LoginActivity.this, UserRegister.class);
                startActivity(i);
                break;
            }
            case R.id.password_forgotten: {
                showPopMenu();
                break;
            }
            case R.id.cancelFindBack: {
                mPopupWindow.dismiss();
                break;
            }
            case R.id.findBackPassword: {
                mPopupWindow.dismiss();
                break;
            }
            case R.id.messageLogin: {
                Intent i = new Intent(LoginActivity.this, GetBackByNumber.class);
                startActivity(i);
            }
        }
    }

    private void showPopMenu() {
        View view = View.inflate(this.getApplicationContext(), R.layout.activity_forget_password, null);

        FindPassWord = (Button) view.findViewById(R.id.findBackPassword);
        MessageLogin = (Button) view.findViewById(R.id.messageLogin);
        CancelFindBack = (Button) view.findViewById(R.id.cancelFindBack);

        FindPassWord.setOnClickListener(this);
        MessageLogin.setOnClickListener(this);
        CancelFindBack.setOnClickListener(this);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        view.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this.getApplicationContext(), R.anim.fade_in));
        LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.forget_password);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this.getApplicationContext(), R.anim.push_bottom_in));

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(LoginActivity.this);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            Point point = UPublicTool.getScreenSize(this, 1, 1);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createBitmap(point.x, point.y, Bitmap.Config.ALPHA_8)));
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
        }
        mPopupWindow.setContentView(view);
        mPopupWindow.showAtLocation(mPasswordForget, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();

    }


}
