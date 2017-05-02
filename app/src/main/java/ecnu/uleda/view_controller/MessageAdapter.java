package ecnu.uleda.view_controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.ChatMessage;
import ecnu.uleda.model.Msg;

/**
 * Created by zhaoning on 2017/5/2.
 * 聊天界面的adapter
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    //TODO
    //还有点问题
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView head1;
        ImageView head2;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_message_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_message_layout);
            leftMsg = (TextView) view.findViewById(R.id.left_message);
            rightMsg = (TextView) view.findViewById(R.id.right_message);

//            head1 = (ImageView)view.findViewById(R.id.head_left);
//            head2 = (ImageView)view.findViewById(R.id.head_right);

        }
    }

    public MessageAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewTYpe) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
//            holder.head1.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
//            holder.head2.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SEND) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
//            holder.head1.setVisibility(View.GONE);
//            holder.head2.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
        }
    }


    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}