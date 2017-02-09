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
 * Created by TonyDanid on 2017/2/9.
 */

public class OptionListAdapter extends ArrayAdapter<AddOptions> {

    private int resourceId1;

    public OptionListAdapter(Context context, int textViewResourceId,
                             List<AddOptions> objects) {
        super(context, textViewResourceId, objects);
        resourceId1 = textViewResourceId;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        AddOptions uoptions = getItem(position);
        View view;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId1,null);
        }else{
            view=convertView;
        }
        ImageView optionsimage=(ImageView)view.findViewById(R.id.optionsimage);
        TextView optionsname=(TextView)view.findViewById(R.id.optionsname);


        optionsimage.setImageResource(uoptions.getmImageId());
        optionsname.setText(uoptions.getmName());
        return view;
    }
}
