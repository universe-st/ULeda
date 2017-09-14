package ecnu.uleda.view_controller.message;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.imsdk.TIMConversation;

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


    private  String TAG="MFLF";//MessageFragmentLeftFragment is too long(interesting)      -KSS


    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_fragment_left_fragment,container,false);
        return view;

    }

}
