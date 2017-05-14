package ecnu.uleda.view_controller;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.Friend;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zhaoning on 2017/4/9.
 */

public class MessageFragmentContactsFragment extends Fragment implements RongIM.UserInfoProvider {

    private List<Friend> userList;
    private ViewPager vpContent;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private Fragment mConversationFragment = null;
    private Fragment mConversationList;
    private LayoutInflater inflater;
    private ViewGroup container;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.message_fragment_friend_fragment,container,false);
        /**
         * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
         *
         * @param userInfoProvider 用户信息提供者。
         * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
         *                         如果 App 提供的 UserInfoProvider
         *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地内存，会影响用户信息的加载速度；<br>
         *                         此时最好将本参数设置为 true，由 IMKit 将用户信息缓存到本地内存中。
         * @see UserInfoProvider
         */

//        vpContent = (ViewPager)view.findViewById(R.id);

//        mConversationList = initConversationList();//融云会话列表的对象
//        mFragments.add(mConversationList);//添加会话fragment
//        mFragments.add(HomeFragment.getInstance());
//        mFragments.add(FriendFragment.getInstance());
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



        return view;
    }



    @Override
    public UserInfo getUserInfo(String s) {
        for (Friend i:userList){
            if(i.userid.equals(s)){
                //从缓存或者自己服务端获取到数据后返回给融云SDK
                return new UserInfo(i.userid,i.name, Uri.parse(i.imageUrl));
            }
        }
        return null;
    }
}
