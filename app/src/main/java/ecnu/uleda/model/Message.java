package ecnu.uleda.model;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.imsdk.TIMMessage;

import ecnu.uleda.view_controller.message.ChatAdapter;

/**
 * Created by zhaoning on 2017/9/15.
 */

public abstract class Message {
    protected final String TAG = "Message";

    TIMMessage message;

    private boolean hasTime;

    /**
     * 消息描述信息
     */
    private String desc;


    public TIMMessage getMessage() {
        return message;
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    public abstract void showMessage(ChatAdapter.ViewHolder viewHolder, Context context);

    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(ChatAdapter.ViewHolder viewHolder){
        viewHolder.systemMessage.setVisibility(hasTime? View.VISIBLE:View.GONE);
//        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));
        showDesc(viewHolder);
        if (message.isSelf()){
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            return viewHolder.rightMessage;
        }else{
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
//            //群聊显示名称，群名片>个人昵称>identify
//            if (message.getConversation().getType() == TIMConversationType.Group){
//                viewHolder.sender.setVisibility(View.VISIBLE);
//                String name = "";
//                if (message.getSenderGroupMemberProfile()!=null) name = message.getSenderGroupMemberProfile().getNameCard();
//                if (name.equals("")&&message.getSenderProfile()!=null) name = message.getSenderProfile().getNickName();
//                if (name.equals("")) name = message.getSender();
//                viewHolder.sender.setText(name);
//            }else{
                viewHolder.sender.setVisibility(View.GONE);
//            }
            return viewHolder.leftMessage;
        }

    }


    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */
    public void showStatus(ChatAdapter.ViewHolder viewHolder){
        switch (message.status()){
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 判断是否是自己发的
     *
     */
    public boolean isSelf(){
        return message.isSelf();
    }


    private void showDesc(ChatAdapter.ViewHolder viewHolder){

        if (desc == null || desc.equals("")){
            viewHolder.rightDesc.setVisibility(View.GONE);
        }else{
            viewHolder.rightDesc.setVisibility(View.VISIBLE);
            viewHolder.rightDesc.setText(desc);
        }
    }
}
