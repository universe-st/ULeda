package ecnu.uleda.view_controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ecnu.uleda.R;
import ecnu.uleda.model.ChatMessage;
import ecnu.uleda.model.Friend;


/**
 * Created by zhaoning on 2017/5/1.
 * 信息界面左
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
                                        /*I think so
                                            * -KSS*/

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_fragment_message_fragment,container,false);
        //小柠柠早点睡，别熬夜太晚~代码可以明天写，不急不急~
        return view;

    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.message_fragment_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
