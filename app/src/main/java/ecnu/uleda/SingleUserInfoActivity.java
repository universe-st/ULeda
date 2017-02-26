package ecnu.uleda;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SingleUserInfoActivity extends AppCompatActivity {

    private UserInfo mUserInfo;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                putInformation();
            }else{
                UServerAccessException exception=(UServerAccessException)msg.obj;
                Toast.makeText(SingleUserInfoActivity.this,"获取信息失败："+exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void putInformation(){
        //TODO:将用户信息显示在屏幕上
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_user_info_activity);
        Button buttonBack=(Button) findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Intent intent=getIntent();
        if(intent.getBooleanExtra("isGet",false)){
            mUserInfo=(UserInfo)intent.getSerializableExtra("userinfo");
            putInformation();
        }else {
            final String id = intent.getStringExtra("userid");
            new Thread() {
                @Override
                public void run() {
                    try {
                        mUserInfo = UserOperatorController.getInstance().getUserBaseInfo(id);
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    } catch (UServerAccessException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what = 1;
                        message.obj = e;
                        mHandler.sendMessage(message);
                    }
                }
            }.start();
        }
    }
}
