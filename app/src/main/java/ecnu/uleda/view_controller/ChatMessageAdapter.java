package ecnu.uleda.view_controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.ChatMessage;

/**
 * Created by zhaoning on 2017/5/2.
 * 消息界面adapter（MessageFragment的LeftFragment）
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>{
    private List<ChatMessage> mChatMessageList;
    private OnItemClickListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View messageView;
        CircleImageView mImageView;
        TextView mTextView;

        public ViewHolder(View view){
            super(view);
            messageView=view;
            mImageView=(CircleImageView)view.findViewById(R.id.chat_message_image);
            mTextView=(TextView)view.findViewById(R.id.chat_message_name);
        }
    }

    public ChatMessageAdapter(List<ChatMessage> ChatMessageList){
        mChatMessageList=ChatMessageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewTYpe){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
//        Fragment mFragment =new MessageFragmentChatFragment();
        holder.messageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                ChatMessage chatMessage=mChatMessageList.get(position);
                //TODO
                Toast.makeText(v.getContext(),"嘿嘿嘿你选中我了～",Toast.LENGTH_SHORT).show();
                if (mListener != null) {
                    mListener.onItemClicked(v, (ChatMessage) v.getTag());
                }
//                replaceFragment(mFragment);
            }
        });
//        holder.mImageView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                int position = holder.getAdapterPosition();
//                ChatMessage chatMessage=mChatMessageList.get(position);
//                //TODO
//                if (onClickListener != null) {
//                    onClickListener.onClick(v,(ChatMessage) v.getTag());
//                }
//            }
//        });
//        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClicked(View v, ChatMessage chatMessage);
    }


//    private OnItemClickListener onClickListener;
//    private OnItemLongClickListener onLongClickListener;

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        ChatMessage chatMessage=mChatMessageList.get(position);
        holder.mImageView.setImageResource(chatMessage.getImageId());
        holder.mTextView.setText(chatMessage.getName());
//        holder.itemView.setTag(chatMessage);//???
    }

    @Override
    public int getItemCount(){
        return mChatMessageList.size();
    }

//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getFragmentManager();//getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.message_fragment_layout,fragment);
//        transaction.commit();
//    }
}
//public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
//    private int resourceId;
//
//    public ChatMessageAdapter(Context context, int textviewResourceId, List<ChatMessage>objects){
//        super(context,textviewResourceId,objects);
//        resourceId=textviewResourceId;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        ChatMessage message=getItem(position);//获取当前messagr实例～
//        View view;
//        ViewHolder viewHolder;
//        if(convertView==null){
//            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
//            viewHolder=new ViewHolder();
//            viewHolder.messageImage=(CircleImageView)view.findViewById(R.id.chat_message_image);
//            viewHolder.messageName=(TextView)view.findViewById(R.id.chat_message_name);
//            view.setTag(viewHolder);//将viewHolder存储在View中
//        } else {
//            view=convertView;
//            viewHolder=(ViewHolder)view.getTag();
//        }
//        viewHolder.messageImage.setImageResource(message.getImageId());
//        viewHolder.messageName.setText(message.getName());
//        return view;
//    }
//
//    class ViewHolder {
//        CircleImageView messageImage;
//        TextView messageName;
//    }
//}
