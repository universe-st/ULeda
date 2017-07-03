package ecnu.uleda.view_controller;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;

public class UserRegister extends AppCompatActivity implements View.OnClickListener{
    private TextView mRegisterBack;
    private Button MessageSendButton;
    private TextView mWorkDone;
    private EditText Username;
    private EditText PhoneNumber;
    private EditText MessageSure;
    private EditText UserPassword;
    private String mUserName;
    private String mUserPassword;
    private String mphone;
    private String mMessageSure;
    private int time = 60;
    private int Flag;
    private boolean Ans = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        init();
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
    }
    protected void init()
    {
        mRegisterBack = (TextView)findViewById(R.id.register_back);
        mRegisterBack.setOnClickListener(this);
        MessageSendButton = (Button)findViewById(R.id.Message_Send);
        MessageSendButton.setOnClickListener(this);
        mWorkDone = (TextView)findViewById(R.id.WorkDone);
        mWorkDone.setOnClickListener(this);
        Username = (EditText)findViewById(R.id.username);
        PhoneNumber = (EditText)findViewById(R.id.Phone_Number);
        MessageSure = (EditText)findViewById(R.id.Message_Sure);
        UserPassword = (EditText)findViewById(R.id.userpassword);
        Username.setOnClickListener(this);
        PhoneNumber.setOnClickListener(this);
        MessageSure.setOnClickListener(this);
        UserPassword.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.register_back:
                finish();
                break;
            case R.id.Message_Send:
                if(TextUtils.isEmpty(PhoneNumber.getText()))
                {
                    Toast.makeText(UserRegister.this,"请输入手机号",
                            Toast.LENGTH_SHORT).show();
                }
                else if(!TextUtils.isEmpty(PhoneNumber.getText()))
                {
                    mphone = PhoneNumber.getText().toString();
                    if(mphone.length() != 11)
                    {
                        Toast.makeText(UserRegister.this,"请输入正确格式的手机号",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mphone = PhoneNumber.getText().toString();
                        SMSSDK.getVerificationCode("86",mphone);
                        Ans = false;
                    }
                }
                break;
            case R.id.WorkDone:
                if(TextUtils.isEmpty(Username.getText())||TextUtils.isEmpty(UserPassword.getText())||
                        TextUtils.isEmpty(PhoneNumber.getText())||TextUtils.isEmpty(MessageSure.getText()))
                {
                    Toast.makeText(UserRegister.this,"请先输入账号或密码或验证码或手机号",
                            Toast.LENGTH_SHORT).show();
                }
                else if(!TextUtils.isEmpty(PhoneNumber.getText()))
                {
                    mphone = PhoneNumber.getText().toString();
                    if(mphone.length()!=11)
                    {
                        Toast.makeText(UserRegister.this,"请输入正确格式的手机号",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if(!TextUtils.isEmpty(MessageSure.getText()))
                    {
                        mMessageSure = MessageSure.getText().toString();
                        if(mMessageSure.length()!=4)
                        {
                            Toast.makeText(UserRegister.this,"请输入正确格式的验证码",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mUserName = Username.getText().toString();
                            mUserPassword = UserPassword.getText().toString();
                            mphone = PhoneNumber.getText().toString();
                            mMessageSure = MessageSure.getText().toString();


                            try
                            {
                                ServerAccessApi.Register(mUserName,mUserPassword,mMessageSure,mphone);
                                Toast.makeText(UserRegister.this,"注册成功",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }catch (UServerAccessException e)
                            {
                                Flag = e.getStatus();
                                if(Flag == 498)
                                {
                                    Toast.makeText(UserRegister.this,"账号已存在",
                                            Toast.LENGTH_SHORT).show();
                                }
                                if(Flag == 497)
                                {
                                    Toast.makeText(UserRegister.this,"手机验证错误",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(UserRegister.this,""+Flag,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

                break;
        }
    }
    private void reminderText() {
        handlerText.sendEmptyMessage(1);
    }
    Handler handlerText =new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(time>0){
                    MessageSendButton.setText(time+"s");
                    time--;
                    handlerText.sendEmptyMessageDelayed(1, 1000);
                }else{
                    MessageSendButton.setText("重新获取");
                    time = 60;
                }
            }
        };
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){//服务器验证码发送成功
                    reminderText();
                    Toast.makeText(UserRegister.this, "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(Ans)
                {
                    Toast.makeText(UserRegister.this, "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
