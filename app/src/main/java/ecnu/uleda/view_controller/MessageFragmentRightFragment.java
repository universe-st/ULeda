package ecnu.uleda.view_controller;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import ecnu.uleda.R;
import ecnu.uleda.model.Contacts;
import ecnu.uleda.model.Friend;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


/**
 * Created by zhaoning on 2017/5/1.
 */

public class MessageFragmentRightFragment extends Fragment implements RongIM.UserInfoProvider{

    private ListView mListView;
    private List<Contacts> contastsList = new ArrayList<>();

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
    public void onCreate(Bundle b) {
        super.onCreate(b);
//        SPUtil.init(this.getContext());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

//        View view = inflater.inflate(R.layout.message_fragment_right_fragment, container, false);
//        initContacts();
//        ContactsAdapter adapter = new ContactsAdapter(
//                MessageFragmentRightFragment.this.getContext(),R.layout.contacts_item,contastsList);
//        mListView=(ListView)view.findViewById(R.id.contacts_list_view);
//        mListView.setAdapter(adapter);
//        final Fragment mfragment= new MessageFragmentChatFragment();
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
//                Contacts contacts=contastsList.get(position);
//                Toast.makeText(view.getContext(),"别急呀～转啥界面？",Toast.LENGTH_SHORT).show();
////                replaceFragment(mfragment);
//            }
//        });
//        return view;
    }

//    private void initContacts(){
//        for(int i=0;i<2;i++) {
//            Contacts dyz = new Contacts("丁义珍",R.drawable.username);
//            contastsList.add(dyz);
//            Contacts hlp = new Contacts("侯亮平",R.drawable.user2);
//            contastsList.add(hlp);
//            Contacts ldk = new Contacts("李达康",R.drawable.username);
//            contastsList.add(ldk);
//            Contacts qtw = new Contacts("祁同伟",R.drawable.user4);
//            contastsList.add(qtw);
//            Contacts srj = new Contacts("沙瑞金",R.drawable.user5);
//            contastsList.add(srj);
//        }
//    }

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

//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.message_fragment_layout,fragment);
//        transaction.commit();
//    }

}
