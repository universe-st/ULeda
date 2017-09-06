package ecnu.uleda.function_module;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSNSChangeInfo;
import com.tencent.imsdk.TIMSdkConfig;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.AESUtils;
import ecnu.uleda.tool.MD5Utils;

import static android.R.attr.type;


/**
 * Created by Shensheng on 2017/2/21.
 * 控制用户的操作
 */

public class UserOperatorController {
    private static UserOperatorController sUOC = null;
    public static final int sdkAppId = 1400036078;

    public static UserOperatorController getInstance() {
        if (sUOC == null) {
            sUOC = new UserOperatorController();
        }
        return sUOC;
    }

    private String mUserName;
    private String mPassword;
    private String mToken;
    private String mMainKey;
    private boolean mIsLogined = false;
    private int mStatus;
    private String mMessage = "undefined";
    private String mId;
    private String mUserSig;
    private String tag = "UserOperatorController";

    public String getMessage() {
        return mMessage;
    }

    public String getId() {
        return mId;
    }

    public boolean getIsLogined() {
        return mIsLogined;
    }

    public void exitLogin() {
        mToken = null;
        mPassword = null;
        mUserName = null;
        mIsLogined = false;
    }

    public void setIsLogined(boolean x) {
        mIsLogined = x;
    }

    public void login(String userName, String password) {
        if (mIsLogined) {
            return;
        }
        try {
            mUserName = userName;
            mPassword = password;
            String passport = getLoginPassport();
            Log.d("login", passport);
            JSONObject json = ServerAccessApi.login(mUserName, passport);
            mId = json.getString("id");
            mToken = json.getString("accessToken");
            Log.d("login", "Token: " + mToken);
            mUserSig = json.getString("tc_usersig");
            mIsLogined = true;
            mMessage = "successful login";
        } catch (UServerAccessException e) {
            e.printStackTrace();
            mIsLogined = false;
            mMessage = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            mIsLogined = false;
            System.exit(1);
        }
    }

    private void login() {
        login(mUserName, mPassword);
    }

    private String getLoginPassport() throws UServerAccessException {
        String loginToken = ServerAccessApi.getLoginToken(mUserName);
        String aes_key = MD5Utils.MD5(getMainKey()).substring(0, 16);
        String aes = AESUtils.encrypt(mPassword, aes_key);
        String md5 = MD5Utils.MD5(aes);
        String ret = loginToken + md5;
        Log.d("LoginToken", loginToken);
        Log.d("AES", md5);
        return MD5Utils.MD5(ret);
    }

    private String getMainKey() throws UServerAccessException {
        if (mMainKey == null) {
            mMainKey = ServerAccessApi.getMainKey();
        }
        Log.d("MainKey", mMainKey);
        return mMainKey;
    }

    public String getPassport() {
        long timeStamp = System.currentTimeMillis() / 100000;
        Log.e("manager", "musername = " + mUserName + ", mToken = " + mToken + ", timestamp: " + timeStamp);
        return MD5Utils.MD5(mUserName + mToken + timeStamp);
    }

    public UserInfo getMyInfo() throws UServerAccessException {
        return getUserBaseInfo(mId);
    }

    public UserInfo getUserBaseInfo(String id) throws UServerAccessException {
        try {
            JSONObject json = ServerAccessApi.getBasicInfo(mId, getPassport(), id);
            UserInfo userInfo = new UserInfo();
            userInfo.setAvatar(json.getString("avatar"))
                    .setBirthday(json.getString("birthday"))
                    .setPhone(json.getString("phone"))
                    .setSex(json.getInt("sex"))
                    .setRealName(json.getString("realname"))
                    .setSchool(json.getString("school"))
                    .setSchoolClass(json.getString("class"))
                    .setStudentId(json.getString("studentid"))
                    .setUserName(json.getString("username"))
                    .setId(id)
                    .setSignature(json.getString("signature"));
            return userInfo;
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public String getUserName() {
        return mUserName;
    }

    UserOperatorController() {
        //单例模式

        //初始化SDK基本配置
        TIMSdkConfig config = new TIMSdkConfig(sdkAppId)
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG).setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/");
        //初始化SDK
        TIMManager.getInstance().init(App.getContext(), config);
        Log.d("UserOperatorController", TIMManager.getInstance().getVersion() + "xxx");//fine

        setUserConfig();

    }

    public String getUserSig() {
        return mUserSig;
    }

    public void setUserSig(String userSig) {
        this.mUserSig = userSig;
    }

    public void setUserConfig() {
        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
//                //设置群组资料拉取字段
//                .setGroupSettings(initGroupSettings())
//                //设置资料关系链拉取字段
//                .setFriendshipSettings(initFriendshipSettings())
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        Log.i(tag, "onForceOffline");
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新userSig重新登录SDK
                        Log.i(tag, "onUserSigExpired");
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(tag, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(tag, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(tag, "onWifiNeedAuth");
                    }
                })
                //设置群组事件监听器
                .setGroupEventListener(new TIMGroupEventListener() {
                    @Override
                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                        Log.i(tag, "onGroupTipsEvent, type: " + elem.getTipsType());
                    }
                })
                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(tag, "onRefresh");
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        Log.i(tag, "onRefreshConversation, conversation size: " + conversations.size());
                    }
                });

////消息扩展用户配置
//        userConfig = new TIMUserConfigMsgExt(userConfig)
//                //禁用消息存储
//                .enableStorage(false)
//                //开启消息已读回执
//                .enableReadReceipt(true);
//
////资料关系链扩展用户配置
//        userConfig = new TIMUserConfigSnsExt(userConfig)
//                //开启资料关系链本地存储
//                .enableFriendshipStorage(true)
//                //设置关系链变更事件监听器
//                .setFriendshipProxyListener(new TIMFriendshipProxyListener() {
//                    @Override
//                    public void OnAddFriends(List<TIMUserProfile> users) {
//                        Log.i(tag, "OnAddFriends");
//                    }
//
//                    @Override
//                    public void OnDelFriends(List<String> identifiers) {
//                        Log.i(tag, "OnDelFriends");
//                    }
//
//                    @Override
//                    public void OnFriendProfileUpdate(List<TIMUserProfile> profiles) {
//                        Log.i(tag, "OnFriendProfileUpdate");
//                    }
//
//                    @Override
//                    public void OnAddFriendReqs(List<TIMSNSChangeInfo> reqs) {
//                        Log.i(tag, "OnAddFriendReqs");
//                    }
//
//                    @Override
//                    public void OnAddFriendGroups(List<TIMFriendGroup> friendgroups) {
//                        Log.i(tag, "OnAddFriendGroups");
//                    }
//
//                    @Override
//                    public void OnDelFriendGroups(List<String> names) {
//                        Log.i(tag, "OnDelFriendGroups");
//                    }
//
//                    @Override
//                    public void OnFriendGroupUpdate(List<TIMFriendGroup> friendgroups) {
//                        Log.i(tag, "OnFriendGroupUpdate");
//                    }
//                });
//
////群组管理扩展用户配置
//        userConfig = new TIMUserConfigGroupExt(userConfig)
//                //开启群组资料本地存储
//                .enableGroupStorage(true)
//                //设置群组资料变更事件监听器
//                .setGroupAssistantListener(new TIMGroupAssistantListener() {
//                    @Override
//                    public void onMemberJoin(String groupId, List<TIMGroupMemberInfo> memberInfos) {
//                        Log.i(tag, "onMemberJoin");
//                    }
//
//                    @Override
//                    public void onMemberQuit(String groupId, List<String> members) {
//                        Log.i(tag, "onMemberQuit");
//                    }
//
//                    @Override
//                    public void onMemberUpdate(String groupId, List<TIMGroupMemberInfo> memberInfos) {
//                        Log.i(tag, "onMemberUpdate");
//                    }
//
//                    @Override
//                    public void onGroupAdd(TIMGroupCacheInfo groupCacheInfo) {
//                        Log.i(tag, "onGroupAdd");
//                    }
//
//                    @Override
//                    public void onGroupDelete(String groupId) {
//                        Log.i(tag, "onGroupDelete");
//                    }
//
//                    @Override
//                    public void onGroupUpdate(TIMGroupCacheInfo groupCacheInfo) {
//                        Log.i(tag, "onGroupUpdate");
//                    }
//                });

//将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);
    }
}
