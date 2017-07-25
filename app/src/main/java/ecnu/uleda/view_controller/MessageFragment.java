package ecnu.uleda.view_controller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSNSChangeInfo;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.group.TIMGroupAssistantListener;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMUserConfigGroupExt;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.tencent.imsdk.ext.sns.TIMFriendGroup;
import com.tencent.imsdk.ext.sns.TIMFriendshipProxyListener;
import com.tencent.imsdk.ext.sns.TIMUserConfigSnsExt;
import com.tencent.imsdk.protocol.im_common;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.Friend;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.SPUtil;
import ecnu.uleda.view_controller.widgets.SelectableTitleView;
import tencent.tls.platform.TLSSmsLoginListener;


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
    private String tag = "MessageFragment";
    private TIMManager mTIMManager = TIMManager.getInstance();

    private UserOperatorController uoc = UserOperatorController.getInstance();

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

    @Override
    public void onItemSelected(int pos, String title) {
        if (pos == 0) {
            switchToLeftFragment();
        } else if (pos == 1) {
            switchToRightFragment();
        }
    }

}