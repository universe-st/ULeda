package ecnu.uleda.view_controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import ecnu.uleda.R;
import ecnu.uleda.model.Contacts;

/**
 * Created by zhaoning on 2017/5/2.
 */

public class ContactsAdapter extends ArrayAdapter<Contacts> {
    private int resourceId;

    public ContactsAdapter(Context context, int textviewResourceId, List<Contacts> objects){
        super(context,textviewResourceId,objects);
        resourceId=textviewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Contacts contacts=getItem(position);//获取当前Contacts实例～
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.contactsImage=(CircleImageView)view.findViewById(R.id.contacts_image);
            viewHolder.contactsName=(TextView)view.findViewById(R.id.contacts_name);
            view.setTag(viewHolder);//将viewHolder存储在View中
        } else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.contactsImage.setImageResource(contacts.getImageId());
        viewHolder.contactsName.setText(contacts.getName());
        return view;
    }

    class ViewHolder {
        CircleImageView contactsImage;
        TextView contactsName;
    }
}
