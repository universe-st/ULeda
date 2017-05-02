package ecnu.uleda.view_controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.Friend;
import ecnu.uleda.tool.SPUtil;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class MessageFragment extends Fragment {


    private Activity mActivity;
    private Button mButtonMessage;
    private Button mButtonContacts;
    private Fragment mFragmentMessage;
    private Fragment mFragmentContacts;
    private Context mContext;
    private List<Friend> userIdList;
    private Button mButtonLeft;
    private Button mButtonRight;
    private Fragment mFragmentLeft;
    private Fragment mFragmentRight;

//    private static final String token1 = "en8uP9E3+foeCzwKhzm4ctY5U+MiA2747EUqq9dOV5QN6r2825gocqPudjCjiYuoZR4U3zOOedoGNPs8Qy75MQ==";
//    private static final String token2 = "+kFtILEgPuQWdchTskz59CwGk6JFyJAXd9m6rCyu7HhOITfx+9XpsFJVo7dzv/jGw5oKenlEuJOqx9gxiMzaqA==";


    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        SPUtil.init(this.getContext());
//        RongIM.init(this.getContext());
//        if (this.getContext().getApplicationInfo().packageName.equals(getCurProcessName(getActivity().getApplicationContext())) ||
//                "io.rong.push".equals(getCurProcessName(getActivity().getApplicationContext()))) {
//
//            /**
//             * IMKit SDK调用第一步 初始化
//             */
//            RongIM.init(this.getContext());
//
//            }
//        initUserInfo();
//        connectRongServer(token1);

//        this.mContext = getActivity();


    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = getActivity();
        View view = inflater.inflate(R.layout.message_fragment,container,false);

//        mButtonMessage= (Button) view.findViewById(R.id.button_message);
//        mButtonContacts= (Button) view.findViewById(R.id.button_contacts);

        mButtonLeft= (Button) view.findViewById(R.id.button_message);
        mButtonRight= (Button) view.findViewById(R.id.button_contacts);

//        mFragmentMessage=new MessageFragmentMessageFragment();
//        mFragmentContacts=new FriendFragment();

        mFragmentLeft=new MessageFragmentLeftFragment();
        mFragmentRight=new MessageFragmentRightFragment();

        mButtonLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                replaceFragment1(mFragmentLeft);
//                backgroundChanged(mButtonLeft);
            }
        });
        mButtonRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                replaceFragment2(mFragmentRight);
            }
        });

        return view;
    }

    private void replaceFragment1(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.message_fragment_layout,fragment);
        transaction.commit();
    }
    private void replaceFragment2(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.message_fragment_layout,fragment);
        transaction.commit();
    }
//    private void backgroundChanged(Button mButton){
//        mButton.setBackgroundColor(Color.parseColor("#8f1515"));
//
//    }

//    public static String getCurProcessName(Context context) {
//
//        int pid = android.os.Process.myPid();
//
//        ActivityManager activityManager = (ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
//                .getRunningAppProcesses()) {
//
//            if (appProcess.pid == pid) {
//                return appProcess.processName;
//            }
//        }
//        return null;
//    }
//
//    private void connectRongServer(String token) {
//
//        RongIM.connect(token, new RongIMClient.ConnectCallback() {
//
//
//            @Override
//            public void onSuccess(String userId) {
//
//                if (userId.equals("10010")) {
//
////                    mUser1.setText("用户1连接服务器成功");
////                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
//                    Toast.makeText(getActivity(), "connect server success 10010", Toast.LENGTH_SHORT).show();
//
//                } else {
////                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
//                    Toast.makeText(getActivity(), "connect server success 10086", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//                // Log.e("onError", "onError userid:" + errorCode.getValue());//获取错误的错误码
////                Log.e(TAG, "connect failure errorCode is : " + errorCode.getValue());
//            }
//
//
//            @Override
//            public void onTokenIncorrect() {
////                Log.e(TAG, "token is error ,please check token and appkey");
//            }
//        });
//
//    }
//
//    private void initUserInfo() {
//        userIdList = new ArrayList<Friend>();
//        userIdList.add(new Friend("10010", "联通", "http://www.51zxw.net/bbs/UploadFile/2013-4/201341122335711220.jpg"));//联通图标
//        userIdList.add(new Friend("10086", "移动", "http://img02.tooopen.com/Download/2010/5/22/20100522103223994012.jpg"));//移动图标
//        userIdList.add(new Friend("KEFU144542424649464","在线客服","http://img02.tooopen.com/Download/2010/5/22/20100522103223994012.jpg"));
//        RongIM.setUserInfoProvider(this, true);
//    }



/**
 * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #init(Context)} 之后调用。</p>
 * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
 * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
 *
 * @param token    从服务端获取的用户身份令牌（Token）。
 * @param callback 连接回调。
 * @return RongIM  客户端核心类的实例。
 */
//    private void connect(String token) {
//
////        if (getActivity().getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {
//
//            RongIM.connect(token, new RongIMClient.ConnectCallback() {
//
//                /**
//                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
//                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
//                 */
//                @Override
//                public void onTokenIncorrect() {
//
//                }
//
//                /**
//                 * 连接融云成功
//                 * @param userid 当前 token 对应的用户 id
//                 */
//                @Override
//                public void onSuccess(String userid) {
////                    Log.d("LoginActivity", "--onSuccess" + userid);
////                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
////                    finish();
//                }
//
//                /**
//                 * 连接融云失败
//                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
//                 */
//                @Override
//                public void onError(RongIMClient.ErrorCode errorCode) {
//
//                }
//            });
//        }
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//
//        MessageList=createUser();//初始化消息列表
//        adapter=new MessageListAdapter(this.getActivity().getApplicationContext()
//                , R.layout.message_list_item,MessageList);
//        View view=inflater.inflate(R.layout.message_fragment,container,false);
//        MessageListview=(ListView)view.findViewById(R.id.message_list_view);
//        MessageListview.setAdapter(adapter);
//        MessageListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    if(view.getTag().equals(1))
//                    {
//                        Intent i = new Intent(MessageFragment.this.getActivity(),Community.class);
//                        startActivity(i);
//                    }
//                    else if(view.getTag().equals(3))
//                    {
//                        Intent i = new Intent(MessageFragment.this.getActivity(),Chart.class);
//                        startActivity(i);
//
//                    }
//
//                    }
//
//        });
//
//        AddFriends = (Button)view.findViewById(R.id.add_friends);
//        AddFriends.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MessageFragment.this.getActivity(),AddNewFriends.class);
//                startActivity(i);
//            }
//        });
//
//        return view;
//    }
//
//    private List<UMessage>createUser(){
//        List<UMessage> MList= new ArrayList<>();
//
//        UMessage m1=new UMessage("任务已完成",R.drawable.user2,
//                "刚刚","点击对对方进行评价",R.drawable.white);
//        MList.add(m1);
//        UMessage m2=new UMessage("社区消息",R.drawable.user3,"昨天","[管理员@了你]",
//                R.drawable.white);
//        MList.add(m2);
//        UMessage m3=new UMessage("致幻Trance",R.drawable.user1,"刚刚","在吗?",
//                R.drawable.oneo);
//        MList.add(m3);
//        UMessage m4=new UMessage("赵铁柱",R.drawable.user4,"星期三",
//                "我没看到 我不是和你们一起去的吗", R.drawable.white);
//        MList.add(m4);
//        UMessage m5=new UMessage("恭喜你发现一枚美少女",R.drawable.user5,"08-21","好吧",
//                R.drawable.white);
//        MList.add(m5);
//
//
//
//        return MList;
//
//    }


}

