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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shensheng on 2016/10/17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button mLogin;
    private EditText mUserName;
    private EditText mPassword;
    private TextView mRegister;
    private Handler mHandler=new Handler(){
      @Override
      public void handleMessage(Message msg){
          if(mUOC.getIsLogined()){
              Intent intent=new Intent(LoginActivity.this,UMainActivity.class);
              startActivity(intent);
              LoginActivity.this.finish();
              Toast.makeText(LoginActivity.this,"欢迎您，"+mUOC.getUserName()+"！",Toast.LENGTH_SHORT).show();
          }else{
              Toast.makeText(LoginActivity.this,"登陆错误："+mUOC.getMessage(),Toast.LENGTH_SHORT).show();
              setAllEnabled(true);
          }
      }
    };
    private UserOperatorController mUOC=UserOperatorController.getInstance();
    @Override
    protected void onCreate(Bundle  savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        UStatusBarUtils.setWindowStatusBarColor(LoginActivity.this,R.color.colorUMain);
    }


    protected void init(){
        mLogin=(Button)findViewById(R.id.login_button);
        mUserName=(EditText)findViewById(R.id.user_name);
        mPassword=(EditText)findViewById(R.id.password);
        mRegister = (TextView)findViewById(R.id.login_text);
        //测试
        mUserName.setText("dizy");
        mPassword.setText("zy980018");
        //测试
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllEnabled(false);
                new Thread(){
                    @Override
                    public void run(){
                        mUOC.login(mUserName.getText().toString(),mPassword.getText().toString());
                        try {
                            if(mUOC.getIsLogined()) {
                                UTaskManager.getInstance().refreshTaskInList();
                            }
                        }catch (UServerAccessException e){
                            e.printStackTrace();
                        }
                        Log.d("LoginActivity",mUOC.getMessage());
                        Message message=new Message();
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LoginActivity.this,UserRegister.class);
                startActivity(i);
            }
        });
    }

    private void setAllEnabled(boolean a){
        mUserName.setEnabled(a);
        mPassword.setEnabled(a);
        mLogin.setEnabled(a);
    }
}
