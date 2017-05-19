package ecnu.uleda.view_controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.model.Friend;
import ecnu.uleda.tool.SPUtil;
import ecnu.uleda.view_controller.widgets.SelectableTitleView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class MessageFragment extends Fragment implements SelectableTitleView.OnTitleSelectedListener {


    private static List<String> titles;
    private Activity mActivity;
    private Button mButtonMessage;
    private Button mButtonContacts;
    private Fragment mFragmentMessage;
    private Fragment mFragmentContacts;
    private Context mContext;
    private List<Friend> userIdList;
    private Unbinder mUnbinder;
    private MessageFragmentLeftFragment mFragmentLeft;
    private FriendFragment mFragmentRight;
//    private MessageFragmentRightFragment mFragmentRight;


    @BindView(R.id.titles)
    SelectableTitleView mTitleView;

    static {
        titles = new ArrayList<>();
        titles.add("消息");
        titles.add("好友");
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        SPUtil.init(this.getContext());
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = getActivity();
        View view = inflater.inflate(R.layout.message_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTitleView.setTitles(titles);
        mTitleView.setOnTitleSelectedListner(this);
        switchToLeftFragment();
    }

    private void switchToLeftFragment() {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mFragmentLeft == null) {
            mFragmentLeft = new MessageFragmentLeftFragment();
            transaction.add(R.id.message_fragment_layout, mFragmentLeft);
        }
        transaction.show(mFragmentLeft);
        if (mFragmentRight != null && mFragmentRight.isAdded()) {
            transaction.hide(mFragmentRight);
        }
        transaction.commit();
    }

    private void switchToRightFragment() {
        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mFragmentRight == null) {
            mFragmentRight = new FriendFragment();//MessageFragmentRightFragment();
            transaction.add(R.id.message_fragment_layout, mFragmentRight);
        }
        transaction.show(mFragmentRight);
        if (mFragmentLeft != null && mFragmentLeft.isAdded()) {
            transaction.hide(mFragmentLeft);
        }
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.add_friends)
    void addFriends() {
        // 添加好友按钮点击事件
    }

    @OnClick(R.id.my_friends)
    void myFriends() {
        // 左边按钮的点击事件（ps：这个按钮干啥的）
        //我怎么知道
    }

    @Override
    public void onItemSelected(int pos, String title) {
        if (pos == 0) {
            switchToLeftFragment();
        } else if (pos == 1) {
            switchToRightFragment();
        }
    }
}

