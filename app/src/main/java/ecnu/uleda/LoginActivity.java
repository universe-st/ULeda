package ecnu.uleda;

import android.content.Intent;
import android.content.res.ObbInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Shensheng on 2016/10/17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button mLogin;
    private EditText mUserName;
    private EditText mPassword;
    private UserLoginController mUserLoginController;
    private Handler mLoginHandler=new Handler(){
      @Override
      public void handleMessage(Message msg){
          switch (msg.what){
              default:
              case UserLoginController.NOT_LOGIN:
              case UserLoginController.UNKNOWN_WRONG:
                  Toast.makeText(LoginActivity.this,"登陆失败！未知错误",Toast.LENGTH_SHORT).show();
                  setAllEnabled(true);
                  break;
              case UserLoginController.SUCCESS_LOGIN: {
                  Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                  Intent i=new Intent();
                  i.putExtra("login","success");
                  setResult(RESULT_OK,i);
                  finish();
                  break;
              }
              case UserLoginController.INTERNET_ERROR:
                  Toast.makeText(LoginActivity.this,"登录失败！网络连接异常",Toast.LENGTH_SHORT).show();
                  setAllEnabled(true);
                  break;
              case UserLoginController.WRONG_PASSWORD_OR_USERNAME:
                  Toast.makeText(LoginActivity.this,"登陆失败！用户名或密码错误",Toast.LENGTH_SHORT).show();
                  setAllEnabled(true);
                  break;
          }
          super.handleMessage(msg);
      }
    };
    @Override
    protected void onCreate(Bundle  savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        UStatusBarUtils.setWindowStatusBarColor(LoginActivity.this,R.color.colorUMain);

        //if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
          //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);}
    }


    protected void init(){
        mLogin=(Button)findViewById(R.id.login_button);
        mUserName=(EditText)findViewById(R.id.user_name);
        mPassword=(EditText)findViewById(R.id.password);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserName.length()==0 || mPassword.length()<8){
                    Toast.makeText(LoginActivity.this,"请输入合法的用户名/密码！",Toast.LENGTH_SHORT).show();
                    return;
                }
                setAllEnabled(false);
                mUserLoginController=UserLoginController.getInstance(mUserName.getText().toString(),mPassword.getText().toString());
                //mUserLoginController=new UserLoginController(mUserName.getText().toString(),mPassword.getText().toString());
                Thread loginThread=new Thread(){
                    @Override
                    public void run(){
                        mUserLoginController.login();
                        Message msg=new Message();
                        msg.what=mUserLoginController.getSuccessNumber();
                        mLoginHandler.sendMessage(msg);
                    }
                };
                loginThread.start();
            }
        });
    }

    private void setAllEnabled(boolean a){
        mUserName.setEnabled(a);
        mPassword.setEnabled(a);
        mLogin.setEnabled(a);
    }
}
