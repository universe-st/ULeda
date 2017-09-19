package ecnu.uleda.view_controller.message;

/**
 * Created by zhaoning on 2017/9/15.
 * IMSDK提供的刷新和被动更新的通知,登录前注册
 */

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;

import java.util.List;
import java.util.Observable;

public class RefreshEvent extends Observable implements TIMRefreshListener {


    private volatile static RefreshEvent instance;

    private RefreshEvent(){
        //注册监听器
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                return false;
            }
        });
    }

    public static RefreshEvent getInstance(){
        if (instance == null) {
            synchronized (RefreshEvent.class) {
                if (instance == null) {
                    instance = new RefreshEvent();
                }
            }
        }
        return instance;
    }

    /**
     * 数据刷新通知，如未读技术、会话列表等
     */
    @Override
    public void onRefresh() {
        setChanged();
        notifyObservers();
    }


    /**
     * 部分会话刷新，多终端数据同步
     */
    @Override
    public void onRefreshConversation(List<TIMConversation> list) {
        setChanged();
        notifyObservers();

    }
}
