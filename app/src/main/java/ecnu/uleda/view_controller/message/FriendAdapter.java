package ecnu.uleda.view_controller.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.Friend;

/**
 * Created by zhaoning on 2017/8/5.
 */

public class FriendAdapter extends ArrayAdapter<Friend>{

    private int resourceId;

    public FriendAdapter (Context context, int textViewResourceId, List<Friend> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Friend friend = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        CircleImageView friendImage = (CircleImageView)view.findViewById(R.id.friend_image);
        TextView friendName = (TextView)view.findViewById(R.id.friend_name);
        TextView friendTag = (TextView)view.findViewById(R.id.friend_tag);

        friendImage.setImageResource(friend.getImageId());
        friendName.setText(friend.getUserName());
        friendTag.setText(friend.getUserId());

        return view;
    }
}
