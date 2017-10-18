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

import org.json.JSONException;
import org.json.JSONObject;

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

    private List<Conversation> mConversationList = new ArrayList<>();
    private ListView mListView;
    private  String TAG="MFLF";//MessageFragmentLeftFragment is too long(interesting)      -KSS
    private TextView lastMessagg;
    private ConversationAdapter mConversationAdapter;
    private String message;
    private String peerId;
    private UserInfo userInfo;

//    private Handler mHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.what==1)
//            {
//            }
//
//        }
//        }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.message_fragment_left_fragment,container,false);
        mListView = (ListView)view.findViewById(R.id.conversation_list);
        lastMessagg = (TextView)view.findViewById(R.id.contacts_content);


        //test
        final Conversation conversation = new Conversation().setConversationuId("8")
                .setConversationName("赵宁")
                .setContent("hahaha");
        mConversationList.add(conversation);

        initFriendRequest();

        try {
            initConversation();
        }
        catch (UServerAccessException e)
        {
            e.printStackTrace();
            System.exit(1);
        }


        mConversationAdapter = new ConversationAdapter(MessageFragmentLeftFragment.this.getContext(),R.layout.conversation_item,mConversationList);
        mListView.setAdapter(mConversationAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation1 = mConversationList.get(position);
                Intent intent = new Intent(getContext(), SendMessageActivity.class);
                intent.putExtra("userId", String.valueOf(conversation1.getConversationuId()));
                intent.putExtra("userName", String.valueOf(conversation1.getConversationName()));
                startActivity(intent);
            }
        });
        return view;

    }

    void initFriendRequest()
    {

    }


    void initConversation() throws UServerAccessException
    {
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
                            Conversation conversation = new Conversation().setConversationuId(peerId)
                                    .setConversationName(peerId)
                                    .setContent(message);
                            mConversationList.add(conversation);
                            Log.e(TAG, "initConversation: "+ mConversationList.size() );

                            mConversationAdapter.notifyDataSetChanged();
                            }

                            new Thread(new MyRunnable()).start();


                        }
                    });
        }


    }


    public UserInfo getUserId(String id) throws UServerAccessException
    {
        UserOperatorController mUOC = UserOperatorController.getInstance();
        String mId = mUOC.getId();
        String mPwd = mUOC.getPassport();
        UserInfo userInfo = new UserInfo();
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
        return userInfo;
    }


    class MyRunnable implements Runnable {

        @Override
        public void run()
        {
            for( Conversation mConver:mConversationList)
            {
                try
                {
                    userInfo = getUserId(mConver.getConversationuId());
                    mConver.setConversationName(userInfo.getUserName());
                    mConver.setImageUrl("http://118.89.156.167/uploads/avatars/"+userInfo.getAvatar());

                    getActivity().runOnUiThread(new MyUiRunnable());

                }
                catch (UServerAccessException e)
                {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    class MyUiRunnable implements Runnable{
        @Override
        public void run()
        {
            mConversationAdapter.notifyDataSetChanged();
        }
    }

}
