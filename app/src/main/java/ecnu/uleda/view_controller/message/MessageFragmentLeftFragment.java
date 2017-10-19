package ecnu.uleda.view_controller.message;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;

import net.phalapi.sdk.PhalApiClient;
import net.phalapi.sdk.PhalApiClientResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.ChatMessage;
import ecnu.uleda.model.Conversation;
import ecnu.uleda.model.Friend;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.view_controller.SingleUserInfoActivity;


/**
 * Created by zhaoning on 2017/5/1.
 * 信息界面左
 */

public class MessageFragmentLeftFragment extends Fragment {

    private List<Object> mConversationList = new ArrayList<>();
    private ListView mListView;
    private  String TAG="MFLF";//MessageFragmentLeftFragment is too long(interesting)      -KSS
    private TextView lastMessagg;
    private ConversationAdapter mConversationAdapter;
    private String message;
    private String peerId;
    private UserInfo userInfo;

    private static final String INVITES_SUCCESS="success";
    private static final String USERE_NOT_FOUND="notFound";
    private static final String PASSWORD_ERROR="passwordError";


//    private Handler mHandler=new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                getActivity().runOnUiThread(new MyUiRunnable());
//            }
//
//        }
//    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.message_fragment_left_fragment,container,false);
        mListView = (ListView)view.findViewById(R.id.conversation_list);
        lastMessagg = (TextView)view.findViewById(R.id.contacts_content);

        accessServer();

//        new Thread(new MyInvitesRunnable()).start();

//        try {
//            initConversation();
//        }
//        catch (UServerAccessException e)
//        {
//            e.printStackTrace();
//            System.exit(1);
//        }


        mConversationAdapter = new ConversationAdapter(MessageFragmentLeftFragment.this.getContext(),R.layout.conversation_item,R.layout.invites_item,mConversationList);
        mListView.setAdapter(mConversationAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = mConversationList.get(position);
                if(obj instanceof Conversation)
                {
                    Conversation conversation1 = (Conversation)obj;
                    Intent intent = new Intent(getContext(), SendMessageActivity.class);
                    intent.putExtra("userId", String.valueOf(conversation1.getConversationuId()));
                    intent.putExtra("userName", String.valueOf(conversation1.getConversationName()));
                    startActivity(intent);
                }
                else
                {

                }

            }
        });
        return view;

    }
//
//    void initFriendRequest()
//    {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try
//                {
//                    JSONArray ret = onInitFriendRequest();
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = ret;
//                    mHandler.sendMessage(msg);
//                }catch (UServerAccessException e)
//                {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }).start();
//    }

    public List<Invites> onInitFriendRequest() throws UServerAccessException
    {
        List<Invites> invitesList = new ArrayList<>();
        UserOperatorController user = UserOperatorController.getInstance();
        String id = user.getId();
        id = UrlEncode(id);
        String passport = user.getPassport();
        passport = UrlEncode(passport);
        PhalApiClient client=createClient();
        PhalApiClientResponse response=client
                .withService("User.ListInvites")//接口的名称
                .withParams("id",id)
                .withParams("passport",passport)
                .request();
        if(response.getRet()==200) {
            try{
                JSONArray data=new JSONArray(response.getData());
                for(int i = 0;i <data.length();i++)
                {
                    JSONObject jsonObj = data.getJSONObject(i);
                    String user1id = jsonObj.getString("user1");
                    Invites invites = new Invites().setInvitesId(user1id)
                            .setInvitesName("hahaha")
                            .setImageUrl("http://118.89.156.167/uploads/avatars/avatar-5.png");
                    UserInfo userInfo = getUserId(user1id);
                    invites.setInvitesName(userInfo.getUserName())
                            .setImageUrl("http://118.89.156.167/uploads/avatars/"+userInfo.getAvatar());
                    invitesList.add(invites);
//                    new Thread(new MyRunnable()).start();
                    //TODO:多线程对mConversationList进行操作
                }
                return invitesList;
            }catch (JSONException e){
                Log.e("ServerAccessApi",e.toString());
                throw new UServerAccessException(UServerAccessException.ERROR_DATA);
            }
        }
//        else if(response.getRet()==401)
//        {
//            return USERE_NOT_FOUND;
//        }
//        else if(response.getRet()==405)
//        {
//            return PASSWORD_ERROR;
//        }
        else {
            throw new UServerAccessException(response.getRet());
        }
    }

    List<Conversation> initConversation() throws UServerAccessException
    {
        final List<Conversation> conversations = new ArrayList<>();
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversionList();
        for(TIMConversation timConversation : list)
        {
            peerId = timConversation.getPeer();
//            Log.e(TAG, "initConversation: " +peerId);
//            //获取会话扩展实例
//            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerId);
            TIMConversationExt conExt = new TIMConversationExt(timConversation);

//获取此会话的消息
            conExt.getLocalMessage(10, //获取此会话最近的10条消息
                    null, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                    new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                        @Override
                        public void onError(int code, String desc) {//获取消息失败
                            //接口返回了错误码code和错误描述desc，可用于定位请求失败原因
                            //错误码code含义请参见错误码表
                            Log.d(TAG, "get message failed. code: " + code + " errmsg: " + desc);
                        }

                        @Override
                        public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                            //遍历取得的消息
                            TIMMessage lastMsg;
//                            for(TIMMessage msg : msgs) {
                                lastMsg = msgs.get(0);
                                //可以通过timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                                Log.e(TAG, "get msg: " + lastMsg.timestamp() + " self: " + lastMsg.isSelf() + " seq: " + lastMsg.getMsg().seq());

//                            }
                            if(peerId.length()!=0)
                            {
                                for(int i = 0; i < lastMsg.getElementCount(); ++i) {
                                    TIMElem elem = lastMsg.getElement(0);

                                        TIMTextElem textElem = (TIMTextElem) elem;

                                    message = textElem.getText().toString();
                                }
                            final Conversation conversation = new Conversation().setConversationuId(peerId)
                                    .setConversationName(peerId)
                                    .setContent(message)
                                    .setImageUrl("http://118.89.156.167/uploads/avatars/avatar-5.png");

                                new Thread(){
                                    @Override
                                    public void run(){
                                        try {
                                            UserInfo userInfo = getUserId(peerId);
                                            conversation.setConversationName(userInfo.getUserName())
                                                    .setImageUrl("http://118.89.156.167/uploads/avatars/"+userInfo.getAvatar());
                                        } catch (UServerAccessException e) {
                                            e.printStackTrace();
                                            System.exit(1);
                                        }
                                    }
                                }.start();

                                conversations.add(conversation);

                            Log.e(TAG, "initConversation: "+ conversations.size() );

//                            mConversationAdapter.notifyDataSetChanged();
                            }

//                            new Thread(new MyRunnable()).start();
                        }
                    });
        }
        return conversations;

    }


    public UserInfo getUserId(String id) throws UServerAccessException
    {
        UserOperatorController mUOC = UserOperatorController.getInstance();
        final String mId = mUOC.getId();
        final String mPwd = mUOC.getPassport();
        final UserInfo userInfo = new UserInfo();

//        new Thread(){
//            @Override
//            public void run(){
                try {
                    JSONObject json = ServerAccessApi.getBasicInfo(mId, mPwd, id);
                    userInfo.setAvatar(json.getString("avatar"))
                            .setPhone(json.getString("phone"))
                            .setSex(json.getInt("sex"))
                            .setRealName(json.getString("realname"))
                            .setSchool(json.getString("school"))
                            .setUserName(json.getString("username"))
                            .setSignature(json.getString("signature"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                catch (UServerAccessException e)
                {
                    e.printStackTrace();
                    System.exit(1);
                }
//            }
//        }.start();

        return userInfo;
    }


    private static String UrlEncode(String str)throws UServerAccessException{
        try{
            if(str==null)return null;
            return URLEncoder.encode(str,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new UServerAccessException(UServerAccessException.PARAMS_ERROR);
        }
    }

    private static PhalApiClient createClient(){
        //这个函数创造一个客户端实例
        return PhalApiClient.create()
                .withHost("http://118.89.156.167/mobile/");
    }

//    //更新UI
//    class MyRunnable implements Runnable {
//
//        @Override
//        public void run()
//        {
//            for( Object object : mConversationList)
//            {
//                try
//                {
//                    if(object instanceof Conversation)
//                    {
//                        Conversation mConver = (Conversation)object;
//                        userInfo = getUserId(mConver.getConversationuId());
//                        mConver.setConversationName(userInfo.getUserName());
//                        mConver.setImageUrl("http://118.89.156.167/uploads/avatars/"+userInfo.getAvatar());
////                        Message msg = new Message();
////                        msg.what = 2;
////                        msg.obj = INVITES_SUCCESS;
////                        mHandler.sendMessage(msg);
//                    }
//                    else if(object instanceof Invites)
//                    {
//                        Invites invites = (Invites)object;
//                        userInfo = getUserId(invites.getInvitesId());
//                        Log.e(TAG, "invites.getInvitesId()"+invites.getInvitesId() );
//                        invites.setInvitesName(userInfo.getUserName());
//                        invites.setImageUrl("http://118.89.156.167/uploads/avatars/"+userInfo.getAvatar());
//                    }
//                    getActivity().runOnUiThread(new MyUiRunnable());
//
//                }
//                catch (UServerAccessException e)
//                {
//                    e.printStackTrace();
//                    System.exit(1);
//                }
//            }
//        }
//    }
//
//    class MyUiRunnable implements Runnable{
//        @Override
//        public void run()
//        {
//
//            mConversationAdapter.notifyDataSetChanged();
//        }
//    }
//
//    class MyInvitesRunnable implements Runnable
//    {
//        @Override
//        public void run()
//        {
//            try
//            {
//                onInitFriendRequest();
////                getActivity().runOnUiThread(new MyUiRunnable());
////                new Thread(new MyRunnable()).start();
//
//            }
//            catch (UServerAccessException e)
//            {
//                e.printStackTrace();
//                System.exit(1);
//            }
//        }
//    }

    public void accessServer(){
        new Thread(){
            @Override
            public void run(){
                try{
                    final List<Conversation> conversations = initConversation();
                    final List<Invites> invites = onInitFriendRequest();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConversationList.addAll(invites);
                            mConversationList.addAll(conversations);
                            mConversationAdapter.notifyDataSetChanged();
                        }
                    });
                }
                catch (UServerAccessException e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }.start();
    }



}
