package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import ecnu.uleda.R;
import ecnu.uleda.model.Contacts;
import ecnu.uleda.model.Friend;



/**
 * Created by zhaoning on 2017/5/1.
 * 信息界面右
 */

public class MessageFragmentRightFragment extends Fragment {

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

        view = inflater.inflate(R.layout.message_fragment_right_fragment,container,false);
        //小柠柠早点睡，别熬夜太晚~代码可以明天写，不急不急~
        return view;
    }



}
