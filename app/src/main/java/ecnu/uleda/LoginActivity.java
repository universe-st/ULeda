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
    }

    private void setAllEnabled(boolean a){
        mUserName.setEnabled(a);
        mPassword.setEnabled(a);
        mLogin.setEnabled(a);
    }
}
