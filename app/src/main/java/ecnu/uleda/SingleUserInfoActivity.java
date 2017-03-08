package ecnu.uleda;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;

public class SingleUserInfoActivity extends AppCompatActivity {

    private UserInfo mUserInfo;
    private UserOperatorController mUserOperatorController;
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
    private TextView textViewUserName;
    private TextView textViewUserSex;
    private TextView textViewUserAge;
    private TextView  textViewUsersign;
    private TextView textViewSchoolClass;
    private String sYear;
    private String schoolClass;
    private String userClass;//userClass[0]:入学年份 userClass[1]:院系 userClass[2]:专业 userClass[3]:班级
    private Button buttonAddUser;
    private Button buttonSendmsg;

    private void putInformation(){
        //将用户信息显示在屏幕上

        textViewUserName = (TextView) findViewById(R.id.name);
        textViewUserSex = (TextView) findViewById(R.id.user_sex);
        textViewUserAge = (TextView) findViewById(R.id.user_age);
        textViewUsersign = (TextView) findViewById(R.id.text_sign);
        textViewSchoolClass = (TextView) findViewById(R.id.user_class);
        buttonAddUser = (Button) findViewById(R.id.button_adduser);
        buttonSendmsg = (Button) findViewById(R.id.button_sendmsg);

        textViewUserName.setText(mUserInfo.getUserName());

        if(mUserInfo.getSex()==0)
            textViewUserSex.setText("♂");
        else
            textViewUserSex.setText("♀");

        int nowYear,userYear,year;
        Calendar c = Calendar.getInstance();
        nowYear = c.get(Calendar.YEAR);
        sYear = mUserInfo.getBirthday().substring(0,4);
        userYear = Integer.valueOf(sYear).intValue();
        year = nowYear-userYear;
        sYear = String.valueOf(year);
        textViewUserAge.setText(sYear);

        textViewUsersign.setText(mUserInfo.getSignature());

        schoolClass = mUserInfo.getSchoolClass();
        //userClass = schoolClass.split("\\|");
        userClass = schoolClass.replaceAll("\\|"," ");
        textViewSchoolClass.setText(userClass);

        if(mUserInfo.getId().equals(mUserOperatorController.getId()))
        {
            buttonAddUser.setVisibility(View.INVISIBLE);
            buttonSendmsg.setVisibility(View.INVISIBLE);
        }
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
            //mUserOperatorController=(UserOperatorController)intent.getSerializableExtra("operatorcontroller");
            putInformation();
        }else {
            final String id = intent.getStringExtra("userid");
            new Thread() {
                @Override
                public void run() {
                    try {
                        mUserInfo = UserOperatorController.getInstance().getUserBaseInfo(id);
                        mUserOperatorController=UserOperatorController.getInstance();
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
