package ecnu.uleda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 胡楠 on 2017/1/24.
 */

public class UCircleListAdapter extends ArrayAdapter<UCircle> {

    public UCircleListAdapter(Context context, List<UCircle>objects)
    {
        super(context,R.layout.u_circle_list_item,objects);
    }
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        UCircle Circle = getItem(position);
        if(convertView == null)
        {
            convertView= LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.u_circle_list_item,parent,false);
            if(Circle == null)
                return convertView;
            ImageView Iv = (ImageView) convertView.findViewById(R.id.photo);
            Iv.setImageResource(Circle.getmPhotoId());
            TextView Tv = (TextView) convertView.findViewById(R.id.publisher_name);
            Tv.setText(Circle.getmName());
            Tv = (TextView) convertView.findViewById(R.id.Title);
            Tv.setText(Circle.getmTitle());
            Tv = (TextView) convertView.findViewById(R.id.article);
            Tv.setText(Circle.getmArticle());


            if(Circle.getmDynamic_Photo() != 0)
            {
                Iv = (ImageView) convertView.findViewById(R.id.dynamic_photo);
                Iv.setImageResource(Circle.getmDynamic_Photo());

            }
            else
            {
                Iv = (ImageView) convertView.findViewById(R.id.dynamic_photo);
                Iv.setVisibility(View.GONE);
            }




            Tv = (TextView) convertView.findViewById(R.id.publish_time);
            Tv.setText(Circle.getmTime());

            Tv = (TextView)convertView.findViewById((R.id.Get_zan));
            Tv.setText(Circle.getmGet());


        }
        return convertView;
    }



}
