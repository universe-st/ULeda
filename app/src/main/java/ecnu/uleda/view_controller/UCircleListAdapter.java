package ecnu.uleda.view_controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.UCircle;

/**
 * Created by 胡楠 on 2017/1/24.
 */

public class UCircleListAdapter extends ArrayAdapter<UCircle> {
    public UCircleListAdapter(Context context, ArrayList<UCircle> objects)
    {
        super(context, R.layout.u_circle_list_item,objects);
    }
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        UCircle Circle = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.u_circle_list_item,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.photoImage = (CircleImageView) view.findViewById(R.id.photo);

            viewHolder.publishername = (TextView) view.findViewById(R.id.publisher_name);

            viewHolder.title = (TextView) view.findViewById(R.id.Title);

            viewHolder.article = (TextView) view.findViewById(R.id.article);


            viewHolder.publishtime = (TextView) view.findViewById(R.id.publish_time);


            viewHolder.Getzan = (TextView)view.findViewById((R.id.Get_zan));


            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
                Glide.with(getContext())
                        .load("http://118.89.156.167/uploads/avatars/"+Circle.getmPhotoId())
                        .into(viewHolder.photoImage);
        viewHolder.publishername.setText(Circle.getmName());
        viewHolder.title.setText(Circle.getmTitle());
        viewHolder.article.setText(Circle.getmArticle());
        if(Circle.getmDynamic_Photo1().equals("null"))
        {
            viewHolder.dynamicphoto1 = (ImageView) view.findViewById(R.id.dynamic_photo1);
            viewHolder.dynamicphoto1.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.dynamicphoto1 = (ImageView) view.findViewById(R.id.dynamic_photo1);
            Glide.with(getContext())
                    .load("http://118.89.156.167/uploads/avatars/"+Circle.getmDynamic_Photo1() )
                    .into(viewHolder.dynamicphoto1);
        }
        if(Circle.getmDynamic_Photo2().equals("null"))
        {
            viewHolder.dynamicphoto2 = (ImageView) view.findViewById(R.id.dynamic_photo2);
            viewHolder.dynamicphoto2.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.dynamicphoto2 = (ImageView) view.findViewById(R.id.dynamic_photo2);
            Glide.with(getContext())
                    .load("http://118.89.156.167/uploads/avatars/"+Circle.getmDynamic_Photo2() )
                    .into(viewHolder.dynamicphoto2);
        }
        if(Circle.getmDynamic_Photo3().equals("null"))
        {
            viewHolder.dynamicphoto3 = (ImageView) view.findViewById(R.id.dynamic_photo3);
            viewHolder.dynamicphoto3.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.dynamicphoto3 = (ImageView) view.findViewById(R.id.dynamic_photo3);
            Glide.with(getContext())
                    .load("http://118.89.156.167/uploads/avatars/"+Circle.getmDynamic_Photo3() )
                    .into(viewHolder.dynamicphoto3);
        }


        viewHolder.publishtime.setText(Circle.getmTime());
        viewHolder.Getzan.setText(Circle.getmGet());
        return view;
    }
    class ViewHolder
    {
        CircleImageView photoImage;
        TextView publishername;
        TextView title;
        TextView article;
        ImageView dynamicphoto1;
        ImageView dynamicphoto2;
        ImageView dynamicphoto3;
        TextView publishtime;
        TextView Getzan;
    }



}
