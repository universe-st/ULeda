package ecnu.uleda.view_controller.message;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.Friend;

/**
 * Created by zhaoning on 2017/8/5.
 */

public class FriendAdapter extends ArrayAdapter<Friend>{

    private int resourceId;
    private Uri mURI;

    private static final String TAG = "FriendAdapter";

    public FriendAdapter (Context context, int textViewResourceId, List<Friend> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Friend friend = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.friendImage = (CircleImageView)view.findViewById(R.id.friend_image);
            viewHolder.friendName = (TextView)view.findViewById(R.id.friend_name);
            viewHolder.friendTag = (TextView)view.findViewById(R.id.friend_tag);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
//
//        mURI = Uri.parse(friend.getImageUrl());
//        Log.d(TAG, "getImageUri: "+mURI);

        Glide.with(getContext())
                .load(friend.getImageUrl())
                .into(viewHolder.friendImage);
//        viewHolder.friendImage.setImageURI(mURI);
        viewHolder.friendName.setText(friend.getUserName());
        viewHolder.friendTag.setText(friend.getUserTag());

        return view;
    }

    class ViewHolder
    {
        CircleImageView friendImage;
        TextView friendName;
        TextView friendTag;
    }
}
