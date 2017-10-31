package ecnu.uleda.view_controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.UCircle;

/**
 * Created by VinnyHu on 2017/10/4.
 */

public class UcircleCommentAdapter extends ArrayAdapter<UCircle>
{
    public UcircleCommentAdapter(Context context, ArrayList<UCircle> objects)
    {
        super(context, R.layout.commentitem,objects);
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        UCircle Circle = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.commentitem,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.CommentName = (TextView)view.findViewById(R.id.comment_name);
            viewHolder.CommentTime = (TextView)view.findViewById(R.id.CommentTime);
            viewHolder.CommentContent = (TextView)view.findViewById(R.id.Commentcontent);
            viewHolder.CommentImage = (CircleImageView) view.findViewById(R.id.CommentImage);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.CommentName.setText(Circle.getCommentName());
        viewHolder.CommentTime.setText(Circle.getCommentTime());
        viewHolder.CommentContent.setText(Circle.getCommentcontent());
        Glide.with(getContext())
                .load("http://118.89.156.167/uploads/avatars/"+Circle.getCommentImage() )
                .into(viewHolder.CommentImage);
        return view;
    }
    class ViewHolder
    {
        TextView CommentName;
        TextView CommentTime;
        TextView CommentContent;
        CircleImageView CommentImage;
    }
}
