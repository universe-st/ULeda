package ecnu.uleda.view_controller.message;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;

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


    private ListView mListView;
    private  String TAG="MFLF";//MessageFragmentLeftFragment is too long(interesting)      -KSS


    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_fragment_left_fragment,container,false);
        mListView = (ListView)view.findViewById(R.id.conversation_list);

        initFriendRequest();
        initConversation();


        return view;

    }

    void initFriendRequest()
    {

    }

    void initConversation()
    {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversionList();
        for(int i=0;i<list.size();i++)
        {
            String peerId = list.get(i).getPeer();
            Log.e(TAG, "initConversation: " +peerId);
            //获取会话扩展实例
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerId);
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
                            for(TIMMessage msg : msgs) {
                                TIMMessage lastMsg = msg;
                                //可以通过timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                                Log.e(TAG, "get msg: " + msg.timestamp() + " self: " + msg.isSelf() + " seq: " + msg.getMsg().seq());

                            }
                        }
                    });
        }

    }

}
