package ecnu.uleda.view_controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.Friend;
import ecnu.uleda.tool.SPUtil;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

import static ecnu.uleda.R.color.colorUSwitchDark;

/**
 * Created by zhaoning on 2017/4/9.
 */

public class MessageFragmentMessageFragment extends Fragment implements RongIM.UserInfoProvider{


    private ViewPager vpContent;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private Fragment mConversationFragment = null;
    private Fragment mConversationList;
    private String mUserid;
    private static final String token1 = "en8uP9E3+foeCzwKhzm4ctY5U+MiA2747EUqq9dOV5QN6r2825gocqPudjCjiYuoZR4U3zOOedoGNPs8Qy75MQ==";
    private static final String token2 = "+kFtILEgPuQWdchTskz59CwGk6JFyJAXd9m6rCyu7HhOITfx+9XpsFJVo7dzv/jGw5oKenlEuJOqx9gxiMzaqA==";

    private Button mButtonMessage;
    private Button mButtonContacts;

    private List<Friend> userList;
    private List<Friend> userIdList;

    //用户头像地址
    private static final String imageUrl1 = "http://img0.imgtn.bdimg.com/it/u=1985715566,640089742&fm=23&gp=0.jpg";
    private static final String imageUrl2 = "http://b.hiphotos.baidu.com/baike/w%3D268%3Bg%3D0/sign=9e1b2d588d82b9013dadc4354bb6ce4a/e4dde71190ef76c6f608fd549a16fdfaae5167f2.jpg";

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
//        SPUtil.init(this.getContext());

        initViewAndData();

        this.userIdList = new ArrayList();
        this.userIdList.add(new Friend("10010", "中国电信", imageUrl1));
        this.userIdList.add(new Friend("10086", "中国移动", imageUrl2));
        RongIM.setUserInfoProvider(this,true);

        connectServer(token1);
//        if(v.getId() == 2131230781) {
//        this.connectRongServer("4rnspHMw6ruF/ha//z5/YbDS8NWRd4boTj2Vy4QL3GdXZhpbxVBu95Rcuww/pJdcKLu+G5cq0LCM1uI9uTLY0A==");
//        } else if(v.getId() == 2131230782) {
//            this.connectRongServer("bugmIZWR5JGzPHNoNp47EEGFC6hW/OOiwJwgWU0oTvPK1cxi0MjsRwRj4jyW+UFZDt0alvCqoDsBSVHlDVJA1g==");
//        connectServer(token);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.message_fragment_message_fragment,container,false);
        vpContent = (ViewPager)view.findViewById(R.id.vp_content);
//        mButtonMessage= (Button) view.findViewById(R.id.button_message);
//        mButtonContacts= (Button) view.findViewById(R.id.button_contacts);

        mConversationList = initConversationList();//融云会话列表的对象
        mFragments.add(mConversationList);//添加会话fragment
//        mFragments.add(HomeFragment.getInstance());
        mFragments.add(FriendFragment.getInstance());

        adapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        vpContent.setAdapter(adapter);
//        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int arg0) {
////                resetImg();    //将图片全部默认为不选中
//                int currentItem = vpContent.getCurrentItem();
//                switch (currentItem) {
//                    case 0:
//                        mButtonMessage.setBackgroundColor(Color.parseColor("#b71c1c"));
//                        break;
//                    case 1:
//                        mButtonContacts.setBackgroundColor(Color.parseColor("#b71c1c"));
//                        break;
//                    case 2:
//                        mButtonMessage.setBackgroundColor(Color.parseColor("#b71c1c"));
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//
//            }
//        });

        return view;
    }


   public void initViewAndData(){
        userList = new ArrayList<>();
        userList.add(new Friend("10086","中国移动",imageUrl1));
        userList.add(new Friend("10010","中国电信",imageUrl2));
}


    //集成会话列表
    public Fragment initConversationList(){
        if(mConversationFragment == null){
            ConversationListFragment conversationListFragment = new ConversationListFragment();//getInstance();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                    .build();
            conversationListFragment.setUri(uri);
            return conversationListFragment;
        }else{
            return mConversationFragment;
        }
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public void connectServer(String token){
        if (getActivity().getApplicationInfo().packageName.equals(getCurProcessName(getActivity().getApplicationContext()))) {
            //利用token连服务器
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                }
                @Override
                public void onSuccess(String userid) {
                    //userid，是我们在申请token时填入的userid
                    mUserid = userid;
//                    Log.d(TAG, "onSuccess: "+userid);
                    SPUtil.saveUserId("userId",userid);
                    if(userid.equals("10086")){
                        vpContent.setCurrentItem(0);
                    }
//                        btnOne.setText("连接融云服务器成功(用户一)");
//                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                        btnTwo.setEnabled(false);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
//                                finish();
//                            }
//                        },1000);
//                    }else{
//                        btnTwo.setText("连接融云服务器成功(用户二)");
//                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
//                        btnOne.setEnabled(false);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
//                                finish();
//                            }
//                        },1000);
//                    }

                }
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
        }
    }


//    protected void onDestroy() {
//        super.onDestroy();
//        RongIM.getInstance().logout();
//        this.mUser1 = null;
//        this.mUser2 = null;
//    }

//    public void onBackPressed() {
//        if(System.currentTimeMillis() - this.firstClickTime.longValue() <= 2000L) {
//            super.onBackPressed();
//        } else {
//            this.firstClickTime = Long.valueOf(System.currentTimeMillis());
//            Toast.makeText(this.getApplicationContext(), "再按一次返回键退出应用", 0).show();
//        }
//    }

    public UserInfo getUserInfo(String userId) {
        Iterator var3 = this.userIdList.iterator();

        while(var3.hasNext()) {
            Friend i = (Friend)var3.next();
            if(i.userid.equals(userId)) {
//                Log.e("MainActivity", i.getPortraitUri());
                return new UserInfo(i.userid, i.name, Uri.parse(i.imageUrl));
            }
        }

        return null;
    }

//    private void resetImg() {
//        mWeixinImg.setImageResource(R.drawable.study);
//        mFriendImg.setImageResource(R.drawable.study);
//        mTongxunluImg.setImageResource(R.drawable.study);
//        mSetImg.setImageResource(R.drawable.study);
//    }

}
