package ecnu.uleda.view_controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.UMessage;


/**
 * Created by TonyDanid on 2017/1/20.
 */

public class MessageListAdapter extends ArrayAdapter<UMessage> {

    private int resourceId1;

    public MessageListAdapter(Context context, int textViewResourceId,
                              List<UMessage> objects) {
        super(context, textViewResourceId, objects);
        resourceId1 = textViewResourceId;
    }

    @Override
        public View getView ( int position, View convertView, ViewGroup parent) {
        UMessage umessage = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.message_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.userImage=(ImageView)view.findViewById(R.id.userImage);
            viewHolder.userName=(TextView)view.findViewById(R.id.userName);
            viewHolder.time=(TextView)view.findViewById(R.id.time);
            viewHolder.message=(TextView)view.findViewById(R.id.message);
            viewHolder.hint=(ImageView)view.findViewById(R.id.hint);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.userImage.setImageResource(umessage.getImageId());
        viewHolder.userName.setText(umessage.getName());
        viewHolder.time.setText(umessage.getTime());
        viewHolder.message.setText(umessage.getMessage());
        viewHolder.hint.setImageResource(umessage.getHint());
        switch (umessage.getName())
        {
            case "社区消息":
                view.setTag(1);
                break;
            case "任务已完成":
                view.setTag(2);
                break;
            default:
                view.setTag(3);
                break;
        }
        return view;


    }
    class ViewHolder
    {
        ImageView userImage;
        TextView userName;
        TextView time;
        TextView message;
        ImageView hint;

    }
}