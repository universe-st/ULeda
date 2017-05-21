package ecnu.uleda.view_controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import ecnu.uleda.R;

public class UserRegister extends AppCompatActivity {

    // 填写从短信SDK应用后台注册得到的APPKEY
    //此APPKEY仅供测试使用，且不定期失效，请到mob.com后台申请正式APPKEY
    private static String APPKEY = "1e0a2ecf617f8";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "6409b8f299de786caafbe906e88f14aa";
    private boolean ready;

    private ImageButton mRegisterBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        init();
    }
    protected void init()
    {
        mRegisterBack = (ImageButton)findViewById(R.id.register_back);
        mRegisterBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initSDK() {
        SMSSDK.initSDK(this,APPKEY,APPSECRET);
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    public interface OnSendMessageHandler {

        //#if def{lang} == cn
        /**
         * 此方法在发送验证短信前被调用，传入参数为接收者号码
         * 返回true表示此号码无须实际接收短信
         */
        //#elif def{lang} == en
        /**
         * This method will be called before verification message being to sent,
         * input params are the message receiver
         * return true means this number won't actually receive the message
         */
        //#endif
        public boolean onSendMessage(String country, String phone);

    }

    protected void onDestroy() {
        if (ready) {
            // 销毁回调监听接口
            SMSSDK.unregisterAllEventHandler();
        }
        super.onDestroy();
    }


}
