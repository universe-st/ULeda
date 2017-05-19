package ecnu.uleda.view_controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tencent.imsdk.TIMManager;

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


/**
 * Created by Shensheng on 2016/11/11.
 * 信息界面
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
    private MessageFragmentRightFragment mFragmentRight;
    //TODO:我帮你初始化好了，当然这只是个测试，如果觉得不合适可以删除了 -KSS
    private TIMManager mTIMManager = TIMManager.getInstance();

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
            mFragmentRight = new MessageFragmentRightFragment();
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

        //todo: 不知道你写啥玩意儿捏~ 2333 -KSS
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

