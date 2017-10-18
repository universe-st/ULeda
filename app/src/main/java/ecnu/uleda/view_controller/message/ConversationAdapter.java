package ecnu.uleda.view_controller.message;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.Conversation;
import ecnu.uleda.model.Friend;

/**
 * Created by zhaoning on 2017/10/17.
 */

public class ConversationAdapter extends ArrayAdapter<Conversation> {
    private int resourceId;
    private Uri mURI;

    private static final String TAG = "ConversationAdapter";

    public ConversationAdapter (Context context, int textViewResourceId, List<Conversation> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Conversation conversation = getItem(position);
        View view;
        ConversationAdapter.ViewHolder viewHolder;
        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ConversationAdapter.ViewHolder();
            viewHolder.conversationImage = (CircleImageView)view.findViewById(R.id.conversation_image);
            viewHolder.conversationName = (TextView)view.findViewById(R.id.conversation_name);
            viewHolder.contactsContent = (TextView)view.findViewById(R.id.contacts_content);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ConversationAdapter.ViewHolder)view.getTag();
        }
//
//        mURI = Uri.parse(friend.getImageUrl());
//        Log.d(TAG, "getImageUri: "+mURI);

        Glide.with(getContext())
                .load(conversation.getImageUrl())
                .into(viewHolder.conversationImage);
        Log.e(TAG, "getImageUrl" +conversation.getImageUrl());
//        viewHolder.Image.setImageURI(mURI);
        viewHolder.conversationName.setText(conversation.getConversationName());
        viewHolder.contactsContent.setText(conversation.getContent());

        return view;
    }

    class ViewHolder
    {
        CircleImageView conversationImage;
        TextView conversationName;
        TextView contactsContent;
    }
}
