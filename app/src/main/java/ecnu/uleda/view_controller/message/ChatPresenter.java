//package ecnu.uleda.view_controller.message;
//
//import com.tencent.imsdk.TIMConversation;
//import com.tencent.imsdk.TIMConversationType;
//import com.tencent.imsdk.TIMManager;
//
//import java.util.Observer;
//
///**
// * Created by zhaoning on 2017/10/5.
// */
//
//public class ChatPresenter implements Observer {
//
//    private ChatView view;
//    private TIMConversation conversation;
//    private boolean isGetingMessage = false;
//    private final int LAST_MESSAGE_NUM = 20;
//    private final static String TAG = "ChatPresenter";
//
//    public ChatPresenter(ChatView view,String identify,TIMConversationType type){
//        this.view = view;
//        conversation = TIMManager.getInstance().getConversation(type,identify);
//    }
//
//
//}
