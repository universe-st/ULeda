package ecnu.uleda.view_controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMAddFriendRequest;
import com.tencent.imsdk.ext.sns.TIMDelFriendType;
import com.tencent.imsdk.ext.sns.TIMFriendResult;
import com.tencent.imsdk.ext.sns.TIMFriendStatus;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.function_module.UserOperatorController;

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
    private String tag = "SingleUserInfoActivity";

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
        {
            textViewUserSex.setText("♀");
            textViewUserSex.setTextColor(Color.parseColor("#FF4081"));
        }

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

        if(mUserOperatorController.getId().equals(mUserInfo.getId())) //判断是否是用户本人
        {
            buttonAddUser.setVisibility(View.INVISIBLE);
            buttonSendmsg.setVisibility(View.INVISIBLE);
        }
        else //判断是否是好友
        {
            //TODO： 待定
            //待获取用户资料的好友列表
            List<String> users = new ArrayList<String>();
            users.add(mUserInfo.getId());
//获取好友资料
            TIMFriendshipManagerExt.getInstance().getFriendsProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                @Override
                public void onError(int code, String desc){
                    //错误码code和错误描述desc，可用于定位请求失败原因
                    //错误码code列表请参见错误码表
                    buttonAddUser.setText("添加好友");
                    buttonSendmsg.setText("发消息");
                    Log.e(tag, "getFriendsProfile failed: " + code + " desc");
                }

                @Override
                public void onSuccess(List<TIMUserProfile> result){

                    buttonAddUser.setText("删除好友");
                    buttonSendmsg.setText("发消息");

                    Log.e(tag, "getFriendsProfile succ");
                    for(TIMUserProfile res : result){
                        Log.e(tag, "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
                                + " remark: " + res.getRemark());
                    }
                }
            });

            buttonAddUser.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(buttonAddUser.getText().equals("添加好友"))
                    {
                        onAddFriends();
                    }
                    else if(buttonAddUser.getText().equals("删除好友"))
                    {
                        onDelFriends();
                    }

                }
            });

            buttonSendmsg.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //TODO : 接入单聊
//                Intent intent = new Intent(SingleUserInfoActivity.this, SendMessage.class);
//                startActivity(intent);
                }
            });

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

    void onAddFriends()
    {
        //创建请求列表
        List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
//添加好友请求
        TIMAddFriendRequest req = new TIMAddFriendRequest(mUserInfo.getId());//identifier
//        req.setIdentifier(mSearchIdentifier);
//        req.setAddrSource("AddSource_Type_Android");
//        req.setAddWording("add me");
//        req.setRemark("Cat");
        reqList.add(req);

//申请添加好友
        TIMFriendshipManagerExt.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "addFriend failed: " + code + " desc");
            }
            @Override
            public void onSuccess(List<TIMFriendResult> result){
                Log.e(tag, "addFriend succ");
                for(TIMFriendResult res : result){
                    Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
                }
            }
        });
    }

    void onDelFriends()
    {
        //删除好友
//        List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
//        TIMAddFriendRequest req = new TIMAddFriendRequest(mUserInfo.getId());
//        reqList.add(req);
        List<String>reqList = new ArrayList<String>();
        String req = mUserInfo.getId().toString();
        reqList.add(req);
        TIMFriendshipManagerExt.DeleteFriendParam deleteFriendParam = new TIMFriendshipManagerExt.DeleteFriendParam();
        deleteFriendParam.setUsers(reqList);

//指定删除双向好友
        TIMFriendshipManagerExt.getInstance().delFriend(deleteFriendParam, new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "delFriend failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMFriendResult> result){
                for(TIMFriendResult res : result){
                    Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
                }
            }
        });
    }
}
