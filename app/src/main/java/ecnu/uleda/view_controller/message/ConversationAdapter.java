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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.Conversation;
import ecnu.uleda.model.Friend;

/**
 * Created by zhaoning on 2017/10/17.
 */

public class ConversationAdapter extends ArrayAdapter<Object> {
    private int resourceId1;
    private int resourceId2;

    //Invites类的type标志
    private static final int TYPE_INVATES = 0;
    //Conversation类的type标志
    private static final int TYPE_CONVERSATION = 1;

    private static final String TAG = "ConversationAdapter";

    public ConversationAdapter (Context context, int textViewResourceId1,int textViewResourceId2, List<Object> objects)
    {
        super(context,textViewResourceId1,objects);
        resourceId1 = textViewResourceId1;
        resourceId2 = textViewResourceId2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Object obj = getItem(position);
        View view;

        if(obj instanceof Conversation)
        {
            Conversation conversation = (Conversation)obj;

            ConversationAdapter.ViewHolder1 viewHolder;

            if(convertView==null)
            {
                view = LayoutInflater.from(getContext()).inflate(resourceId1,parent,false);
                viewHolder = new ConversationAdapter.ViewHolder1();
                viewHolder.conversationImage = (CircleImageView)view.findViewById(R.id.conversation_image);
                viewHolder.conversationName = (TextView)view.findViewById(R.id.conversation_name);
                viewHolder.contactsContent = (TextView)view.findViewById(R.id.contacts_content);
                view.setTag(viewHolder);
            }
            else
            {
                view = convertView;
                viewHolder = (ConversationAdapter.ViewHolder1)view.getTag();
            }


            Glide.with(getContext())
                    .load(conversation.getImageUrl())
                    .into(viewHolder.conversationImage);
            Log.e(TAG, "Conversation getImageUrl" +conversation.getImageUrl());
//        viewHolder.Image.setImageURI(mURI);
            viewHolder.conversationName.setText(conversation.getConversationName());
            viewHolder.contactsContent.setText(conversation.getContent());

        }
        else
        {
            ConversationAdapter.ViewHolder2 viewHolder;

            Invites invites = (Invites)obj;

            if(convertView==null)
            {
                view = LayoutInflater.from(getContext()).inflate(resourceId2,parent,false);
                viewHolder = new ConversationAdapter.ViewHolder2();
                viewHolder.invitesImage = (CircleImageView)view.findViewById(R.id.invites_image);
                viewHolder.invitesName = (TextView)view.findViewById(R.id.invites_name);
                viewHolder.invitesContent = (TextView)view.findViewById(R.id.invites_content);
                view.setTag(viewHolder);
            }
            else
            {
                view = convertView;
                viewHolder = (ConversationAdapter.ViewHolder2)view.getTag();
            }


            Glide.with(getContext())
                    .load(invites.getImageUrl())
                    .into(viewHolder.invitesImage);
            Log.e(TAG, "Invites getImageUrl" +invites.getImageUrl());
//        viewHolder.Image.setImageURI(mURI);
            viewHolder.invitesName.setText(invites.getInvitesName());
//            viewHolder.invitesContent.setText(invites.getContent());

        }

        return view;
    }

    class ViewHolder1
    {
        CircleImageView conversationImage;
        TextView conversationName;
        TextView contactsContent;
    }
    class ViewHolder2
    {
        CircleImageView invitesImage;
        TextView invitesName;
        TextView invitesContent;
    }

}
