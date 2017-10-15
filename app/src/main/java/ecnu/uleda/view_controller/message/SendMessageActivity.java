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
    private TIMConversation mConversation;
    private ListView listView;


    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
//    private ChatPresenter presenter;

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

        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        listView = (ListView) findViewById(R.id.chat_list);
//        listView.setAdapter(adapter);

        mChatsAdapter = new ChatsAdapter(this);
        listView.setAdapter(mChatsAdapter);

        mConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                mFriendId);

        Log.e(TAG, "SendMessageActivity:id = "+mFriendId+" ,name = "+mFriendName);


        TIMManager.getInstance().addMessageListener(new TIMMessageListener()
        {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                for (TIMMessage item:list){

                    for(int i = 0; i < item.getElementCount(); ++i) {
                        TIMElem elem = item.getElement(i);

                        TIMTextElem textElem = (TIMTextElem) elem;

//                        //获取当前元素的类型
//                        TIMElemType elemType = elem.getType();
//                        Log.d(TAG, "elem type: " + elemType.name());
//                        if (elemType == TIMElemType.Text) {
//                            //处理文本消息
//                        } else if (elemType == TIMElemType.Image) {
//                            //处理图片消息
//                        }//...处理更多消息
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
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
//                view.onSendMessageFail(code, desc, message);
                Log.d(TAG, "send message failed. code: " + code + " errmsg: " + desc);

            }

            @Override
            public void onSuccess(TIMMessage msg) {
//                发送消息成功,消息状态已在sdk中修改，此时只需更新界面
//                MessageEvent.getInstance().onNewMessage(null);
//                showMessage(msg);
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
        //message对象为发送中状态
//        MessageEvent.getInstance().onNewMessage(message);
    }

    public void showMessage(MsgInfo message) {


        if (message.equals(null)) {
            Toast.makeText(SendMessageActivity.this, "发送内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }


        mMsgInfoList.add(message);
        mChatsAdapter.addDataToAdapter(message);
        mChatsAdapter.notifyDataSetChanged();
        listView.setSelection(mMsgInfoList.size()-1);

        input.setText("");



//        if (message == null) {
//            adapter.notifyDataSetChanged();
//        } else {
//            Message mMessage = new Message(message);
////            if (mMessage != null) {
////                if (mMessage instanceof CustomMessage) {
////                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
////                    switch (messageType) {
////                        case TYPING:
////                            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
////                            title.setTitleText(getString(R.string.chat_typing));
////                            handler.removeCallbacks(resetTitle);
////                            handler.postDelayed(resetTitle, 3000);
////                            break;
////                        default:
////                            break;
////                    }
////                } else {
//                    if (messageList.size() == 0) {
//                        mMessage.setHasTime(null);
//                    } else {
//                        mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
//                    }
//                    messageList.add(mMessage);
//                    adapter.notifyDataSetChanged();
//                    listView.setSelection(adapter.getCount() - 1);
////                }

//            }
        }




    }


