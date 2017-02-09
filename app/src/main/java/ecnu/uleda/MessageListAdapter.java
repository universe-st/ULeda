package ecnu.uleda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


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
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId1,null);
        }else{
            view=convertView;
        }
        ImageView userimage=(ImageView)view.findViewById(R.id.userImage);
        TextView  username=(TextView)view.findViewById(R.id.userName);
        TextView  time=(TextView)view.findViewById(R.id.time);
        TextView  message=(TextView)view.findViewById(R.id.message);
        ImageView  hint=(ImageView)view.findViewById(R.id.hint);

        userimage.setImageResource(umessage.getImageId());
        username.setText(umessage.getName());
        time.setText(umessage.getTime());
        message.setText(umessage.getMessage());
        hint.setImageResource(umessage.getHint());

        return view;


    }
}