package ecnu.uleda.view_controller.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.Message;
import ecnu.uleda.model.UserInfo;

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

    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
//    private ChatPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment_chat_fragment);

        uoc = UserOperatorController.getInstance();
        Intent intent=getIntent();
        mFriendId = intent.getStringExtra("userId");
        mFriendName = intent.getStringExtra("userName");

        mConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                mFriendId);

        Log.e(TAG, "SendMessageActivity");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_right_fragment,container,false);
        title = (TextView)view.findViewById(R.id.chat_title);
        title.setText(mFriendName);
        input = (EditText)view.findViewById(R.id.input_text);


        return view;
    }

    public void sendMessage(final TIMMessage message) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();

//添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(input.toString());

//将elem添加到消息
        if(msg.addElement(elem) != 0) {
            Log.d(TAG, "addElement failed");
            return;
        }

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
                //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
//                MessageEvent.getInstance().onNewMessage(null);
                Log.e(TAG, "SendMsg ok");

            }
        });
        //message对象为发送中状态
//        MessageEvent.getInstance().onNewMessage(message);
    }




}


