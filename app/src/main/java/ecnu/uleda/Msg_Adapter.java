package ecnu.uleda;

/**
 * Created by Jimi on 2017/3/9.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class Msg_Adapter extends ArrayAdapter<Msg> {
    private int resourceId;

    public Msg_Adapter(Context context, int textViewResourceId, List<Msg> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Msg msg = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView content = (TextView) view.findViewById(R.id.message_content);
        content.setText(msg.getContent());
        return  view;
    }
}

