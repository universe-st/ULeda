package ecnu.uleda;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;


/**
 * Created by zhaoning on 2017/3/7.
 */

public class LocationListAdapter extends  ArrayAdapter<String>{
    public LocationListAdapter(Context context,List<String>objects){
        super(context,android.R.layout.simple_list_item_1,objects);
    }


   /* public View getView(int position, View convertView, @NonNull ViewGroup parent){



    }*/
}




