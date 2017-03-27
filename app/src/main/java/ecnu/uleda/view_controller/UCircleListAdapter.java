package ecnu.uleda.view_controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

            viewHolder.photoImage = (ImageView) view.findViewById(R.id.photo);

            viewHolder.publishername = (TextView) view.findViewById(R.id.publisher_name);

            viewHolder.title = (TextView) view.findViewById(R.id.Title);

            viewHolder.article = (TextView) view.findViewById(R.id.article);

            if(Circle.getmDynamic_Photo() != 0)
            {
               viewHolder.dynamicphoto = (ImageView) view.findViewById(R.id.dynamic_photo);
            }
            viewHolder.publishtime = (TextView) view.findViewById(R.id.publish_time);


            viewHolder.Getzan = (TextView)view.findViewById((R.id.Get_zan));


            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.photoImage.setImageResource(Circle.getmPhotoId());
        viewHolder.publishername.setText(Circle.getmName());
        viewHolder.title.setText(Circle.getmTitle());
        viewHolder.article.setText(Circle.getmArticle());
        if(Circle.getmDynamic_Photo() != 0)
        {
            viewHolder.dynamicphoto = (ImageView) view.findViewById(R.id.dynamic_photo);
            viewHolder.dynamicphoto.setImageResource(Circle.getmDynamic_Photo());
        }
        else
        {
            viewHolder.dynamicphoto = (ImageView) view.findViewById(R.id.dynamic_photo);
            viewHolder.dynamicphoto.setVisibility(View.GONE);
        }
        viewHolder.publishtime.setText(Circle.getmTime());
        viewHolder.Getzan.setText(Circle.getmGet());
        return view;
    }
    class ViewHolder
    {
        ImageView photoImage;
        TextView publishername;
        TextView title;
        TextView article;
        ImageView dynamicphoto;
        TextView publishtime;
        TextView Getzan;
    }



}
