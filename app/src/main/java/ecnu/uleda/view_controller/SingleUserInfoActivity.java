package ecnu.uleda.view_controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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

import net.phalapi.sdk.PhalApiClient;
import net.phalapi.sdk.PhalApiClientResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;
import ecnu.uleda.BuildConfig;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.message.MessageFragmentRightFragment;
import ecnu.uleda.view_controller.message.SendMessageActivity;

public class SingleUserInfoActivity extends AppCompatActivity {

    private UserInfo mUserInfo;
    private UserOperatorController mUserOperatorController;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what)
            {
                case 0:
                    putInformation();
                    break;
                case 1:
                {
                    String ret = (String) msg.obj;
                    if(ret == ADD_FRIEND_SUCCESS)
                        Toast.makeText(SingleUserInfoActivity.this,"添加好友成功～",Toast.LENGTH_SHORT).show();
                    else if(ret==ADD_FRIEND_ALREADY)
                        Toast.makeText(SingleUserInfoActivity.this,"您已经添加过该好友～",Toast.LENGTH_SHORT).show();
                    else if(ret==ADD_FRIEND_NOT_FOUND)
                        Toast.makeText(SingleUserInfoActivity.this,"抱歉～该用户不存在",Toast.LENGTH_SHORT).show();
                    else if(ret==ADD_FRIEND_NOT_MYSELF)
                        Toast.makeText(SingleUserInfoActivity.this,"不可以添加自己哦～",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SingleUserInfoActivity.this,"添加好友失败！",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                {
                    UServerAccessException exception=(UServerAccessException)msg.obj;
                    Toast.makeText(SingleUserInfoActivity.this,"获取信息失败："+exception.getMessage(),Toast.LENGTH_SHORT).show();
                    break;
                }

            }
//            else{
//                UServerAccessException exception=(UServerAccessException)msg.obj;
//                Toast.makeText(SingleUserInfoActivity.this,"获取信息失败："+exception.getMessage(),Toast.LENGTH_SHORT).show();
//            }
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
    private static final String ADD_FRIEND_SUCCESS="success";
    private static final String ADD_FRIEND_ALREADY="already";
    private static final String ADD_FRIEND_NOT_FOUND="notFound";
    private static final String ADD_FRIEND_NOT_MYSELF="notMyself";
    private static final String ADD_FRIEND_FAILED="failed";


    private void putInformation(){
        //将用户信息显示在屏幕上

        textViewUserName = (TextView) findViewById(R.id.name);
        textViewUserSex = (TextView) findViewById(R.id.user_sex);
        textViewUserAge = (TextView) findViewById(R.id.user_age);
        textViewUsersign = (TextView) findViewById(R.id.text_sign);
        textViewSchoolClass = (TextView) findViewById(R.id.user_class);
        buttonAddUser = (Button) findViewById(R.id.button_adduser);
        buttonSendmsg = (Button) findViewById(R.id.button_sendmsg);

        buttonAddUser.setEnabled(false);

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
            //TODO:设置按钮为不可按，直到获取好友成功 
            //TODO:刷新好友列表

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
                    buttonAddUser.setEnabled(true);
                    Log.e(tag, "getFriendsProfile failed: " + code + " desc");
                }

                @Override
                public void onSuccess(List<TIMUserProfile> result){

                    buttonAddUser.setText("删除好友");
                    buttonAddUser.setEnabled(true);

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
                        buttonAddUser.setText("删除好友");
                        Toast.makeText(SingleUserInfoActivity.this, "添加好友成功",Toast.LENGTH_SHORT).show();
                    }
                    else if(buttonAddUser.getText().equals("删除好友"))
                    {
                        onDelFriends();
                        buttonAddUser.setText("添加好友");
                        Toast.makeText(SingleUserInfoActivity.this, "删除好友成功",Toast.LENGTH_SHORT).show();

                    }

                }
            });

            buttonSendmsg.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    //TODO : 接入单聊
                Intent intent = new Intent(SingleUserInfoActivity.this, SendMessageActivity.class);
                    intent.putExtra("userId", String.valueOf(mUserInfo.getId()));
                    intent.putExtra("userName", String.valueOf(mUserInfo.getUserName()));
                    startActivity(intent);
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
//        //创建请求列表
//        List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
////添加好友请求
//        TIMAddFriendRequest req = new TIMAddFriendRequest(mUserInfo.getId());//identifier
////        req.setIdentifier(mSearchIdentifier);
////        req.setAddrSource("AddSource_Type_Android");
////        req.setAddWording("add me");
////        req.setRemark("Cat");
//        reqList.add(req);
//
////申请添加好友
//        TIMFriendshipManagerExt.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
//            @Override
//            public void onError(int code, String desc){
//                //错误码code和错误描述desc，可用于定位请求失败原因
//                //错误码code列表请参见错误码表
//                Log.e(tag, "addFriend failed: " + code + " desc");
//            }
//            @Override
//            public void onSuccess(List<TIMFriendResult> result){
//                Log.e(tag, "addFriend succ");
//                //EventBus.getDefault().post(new MessageFragmentRightFragment.FriendRefreshEvent(){});
//                for(TIMFriendResult res : result){
//                    Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
//                }
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String ret = onAddFriend(mUserInfo.getId());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = ret;
                    mHandler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    public static String onAddFriend(@NonNull String inviteByID )throws UServerAccessException{
        UserOperatorController user = UserOperatorController.getInstance();
        String id = user.getId();
        id = UrlEncode(id);
        String passport = user.getPassport();
        passport = UrlEncode(passport);
        PhalApiClient client=createClient();
        PhalApiClientResponse response=client
                .withService("User.InviteFriend")//接口的名称
                .withParams("id",id)
                .withParams("passport",passport)
                .withParams("inviteByID",inviteByID)
                .request();
        if(response.getRet()==200) {
            try{
                JSONObject data=new JSONObject(response.getData());
                return ADD_FRIEND_SUCCESS;// "success"
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                //数据包无法解析，向上抛出一个异常
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }
        else if(response.getRet()==410)
        {
            return ADD_FRIEND_ALREADY;
        }
        else if(response.getRet()==408)
        {
            return ADD_FRIEND_NOT_FOUND;
        }
        else if(response.getRet()==409)
        {
            return ADD_FRIEND_NOT_MYSELF;
        }
        else {
            throw new UServerAccessException(response.getRet());
        }
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


    private static String UrlEncode(String str)throws UServerAccessException{
        try{
            if(str==null)return null;
            return URLEncoder.encode(str,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new UServerAccessException(UServerAccessException.PARAMS_ERROR);
        }
    }

    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("http://118.89.156.167/mobile/");
    }

}
