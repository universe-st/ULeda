package ecnu.uleda;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by zhaoning on 2017/3/7.
 */

public class LocationListAdapter extends  ArrayAdapter<String[]>{
    public LocationListAdapter(Context context,List<String[]>objects){
        super(context,android.R.layout.simple_list_item_1,objects);
    }
    @NonNull
    public View getView(int position, View convertView,@NonNull ViewGroup parent){
        View v;
        if(convertView==null){
            v=View.inflate(getContext(),R.layout.location_list_item,null);
        }else{
            v=convertView;
        }
        String[] ot=getItem(position);
        if(ot!=null) {
            ((TextView) v.findViewById(R.id.location_item_title)).setText(ot[0]);
            ((TextView) v.findViewById(R.id.location_item_address)).setText(ot[1]);
        }
        return v;
    }
}




