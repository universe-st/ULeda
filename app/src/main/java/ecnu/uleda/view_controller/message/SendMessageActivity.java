package ecnu.uleda.view_controller.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;

import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.Message;
import ecnu.uleda.model.Msg;
import ecnu.uleda.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by zhaoning on 2017/8/16.
 */

public class SendMessageActivity extends AppCompatActivity {

    private UserOperatorController uoc;
    private String mFriendId ;
    private String mFriendName;
    private static final String TAG = "SendMessageActivity";
    private View view;
    private TextView title;
    private EditText input;
    private Button mButtonSendMessage;
    private Button mButtonBack;
    private TIMConversation mConversation;
    private ListView listView;


    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;

    private List<MsgInfo> mMsgInfoList = new ArrayList<>();
    private ChatsAdapter mChatsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment_chat_fragment);

        uoc = UserOperatorController.getInstance();
        Intent intent=getIntent();
        mFriendId = intent.getStringExtra("userId");
        mFriendName = intent.getStringExtra("userName");
        title = (TextView)findViewById(R.id.chat_title);
        title.setText(mFriendName);
        input = (EditText)findViewById(R.id.message_edit_text);
        mButtonSendMessage = (Button)findViewById(R.id.button_send_message);
        mButtonBack = (Button)findViewById(R.id.button_chat_back);

        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        listView = (ListView) findViewById(R.id.chat_list);
//        listView.setAdapter(adapter);

        mChatsAdapter = new ChatsAdapter(this);
        listView.setAdapter(mChatsAdapter);

        mConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                mFriendId);

        initConversation(mFriendId);

        Log.e(TAG, "SendMessageActivity:id = "+mFriendId+" ,name = "+mFriendName);

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TIMManager.getInstance().addMessageListener(new TIMMessageListener()
        {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                for (TIMMessage item:list){

                    for(int i = 0; i < item.getElementCount(); ++i) {
                        TIMElem elem = item.getElement(i);

                        TIMTextElem textElem = (TIMTextElem) elem;
                        String m = textElem.getText().toString();
                        if(item.isSelf())
                        {
                            MsgInfo msg = new MsgInfo(null,m);
                            mChatsAdapter.addDataToAdapter(msg);
                            mChatsAdapter.notifyDataSetChanged();
                            listView.setSelection(mMsgInfoList.size()-1);
                        }
                        else
                        {
                            MsgInfo msg = new MsgInfo(m,null);
                            mChatsAdapter.addDataToAdapter(msg);
                            mChatsAdapter.notifyDataSetChanged();
                            listView.setSelection(mMsgInfoList.size()-1);
                        }

                    }
                }
                return false;
            }
        });


        mButtonSendMessage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.e(TAG, "sendButton click!");

                if(input.getText().toString().length()==0)return;
                //WARNING:你忘记做这个判断了~下次注意。-K
                //妈耶我以为我做了。。可能是撒子。 -N

                TIMMessage msg = new TIMMessage();
                TIMTextElem elem = new TIMTextElem();
                elem.setText(input.getText().toString());
                if(msg.addElement(elem) != 0) {
                    Log.e(TAG, "addElement failed");
                    return;
                }
                sendMessage(msg);
            }
        });
    }



    public void sendMessage(final TIMMessage message) {

        mConversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                Log.d(TAG, "send message failed. code: " + code + " errmsg: " + desc);

            }

            @Override
            public void onSuccess(TIMMessage msg) {
                String m = input.getText().toString();
                Log.e(TAG, "Send: "+ m);
                if(msg.isSelf())
                {
                    MsgInfo message = new MsgInfo(null,m);
                    showMessage(message);

                }
                else
                {
                    MsgInfo message = new MsgInfo(m,null);
                    showMessage(message);
                }

                Log.e(TAG, "SendMsg ok");

            }
        });
    }

    public void showMessage(MsgInfo message) {


        //if (message.equals(null)) {
        // 判断null不用equals哦…… -K
        //晓得晓得了 -N
        if(message==null){
            Toast.makeText(SendMessageActivity.this, "发送内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        mMsgInfoList.add(message);
        mChatsAdapter.addDataToAdapter(message);
        mChatsAdapter.notifyDataSetChanged();
        listView.setSelection(mMsgInfoList.size()-1);
        input.setText("");

        }


        void initConversation(String peerID)
        {
            //获取会话扩展实例
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C,peerID);
            TIMConversationExt conExt = new TIMConversationExt(con);

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
                            for(int i = msgs.size()-1;i>=0;i--) {
                                TIMMessage lastMsg = msgs.get(i);

//                                for(int i = 0; i < lastMsg.getElementCount(); ++i) {
                                    TIMElem elem = lastMsg.getElement(0);

                                    TIMTextElem textElem = (TIMTextElem) elem;
                                    String m = textElem.getText().toString();
                                    if(lastMsg.isSelf())
                                    {
                                        MsgInfo msgInfo = new MsgInfo(null,m);
                                        mChatsAdapter.addDataToAdapter(msgInfo);
                                        mChatsAdapter.notifyDataSetChanged();
                                        listView.setSelection(mMsgInfoList.size()-1);
                                    }
                                    else
                                    {
                                        MsgInfo msgInfo = new MsgInfo(m,null);
                                        mChatsAdapter.addDataToAdapter(msgInfo);
                                        mChatsAdapter.notifyDataSetChanged();
                                        listView.setSelection(mMsgInfoList.size()-1);
                                    }

//                                }
                                //可以通过timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
//                                Log.e(TAG, "get msg: " + msg.timestamp() + " self: " + msg.isSelf() + " seq: " + msg.getMsg().seq());

                            }
                        }
                    });
        }


    }


