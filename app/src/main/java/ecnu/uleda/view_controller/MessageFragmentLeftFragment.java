package ecnu.uleda.view_controller;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.ChatMessage;
import ecnu.uleda.model.Friend;
import ecnu.uleda.tool.SPUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zhaoning on 2017/5/1.
 */

public class MessageFragmentLeftFragment extends Fragment {
    private List<ChatMessage> mChatMessageList = new ArrayList<>();

    private ViewPager vpContent;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private Fragment mConversationFragment = null;
    private Fragment mConversationList;
    private String mUserid;


    private List<Friend> userIdList;
    private List<Friend> userList;

    private  String TAG="MFLF";//MessageFragmentLeftFragment is too long(interesting)

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

//    public static TaskMissionFragment getInstance() {
//        if (mInstance == null) {
//            synchronized (TaskMissionFragment.class) {
//                if (mInstance == null) {
//                    mInstance = new TaskMissionFragment();
//                }
//            }
//        }
//        return mInstance;
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_fragment_message_fragment,container,false);
//        vpContent = (ViewPager)view.findViewById(R.id.vp_content);
//
////        mFragments.add(mConversationList);//添加会话fragment
////        mFragments.add(HomeFragment.getInstance());
//
//        adapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return mFragments.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return mFragments.size();
//            }
//        };
//        vpContent.setAdapter(adapter);
        return view;

    }


//    原leftfragment
//    private void initMessages() {
//        for (int i = 0; i < 3; i++) {
//            ChatMessage dyz = new ChatMessage("丁义珍", R.drawable.username);
//            mChatMessageList.add(dyz);
//            ChatMessage hlp = new ChatMessage("侯亮平", R.drawable.user2);
//            mChatMessageList.add(hlp);
//            ChatMessage ldk = new ChatMessage("李达康", R.drawable.username);
//            mChatMessageList.add(ldk);
//            ChatMessage qtw = new ChatMessage("祁同伟", R.drawable.user4);
//            mChatMessageList.add(qtw);
//            ChatMessage srj = new ChatMessage("沙瑞金", R.drawable.user5);
//            mChatMessageList.add(srj);
//        }
//    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.message_fragment_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
