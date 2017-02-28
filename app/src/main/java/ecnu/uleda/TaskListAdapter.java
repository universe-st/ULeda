package ecnu.uleda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Shensheng on 2017/1/15.
 */

public class TaskListAdapter extends ArrayAdapter<UTask> {
    public TaskListAdapter(Context context,List<UTask> objects){
        super(context,R.layout.task_list_item,objects);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView,@NonNull ViewGroup parent){
        UTask task=getItem(position);
        //Log.d("TaskListAdapter",position+task.getInformation());
        if(convertView==null){
            convertView=LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.task_list_item,parent,false);
        }
        if(task==null)return LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.task_list_item,parent,false);
        TextView tv=(TextView)convertView.findViewById(R.id.task_type);
        tv.setText(task.getTag().substring(0,2));
        tv=(TextView)convertView.findViewById(R.id.publisher_name);
        tv.setText(task.getAuthorUserName());
        tv=(TextView)convertView.findViewById(R.id.publisher_stars);
        tv.setText(task.getStarString());
        tv=(TextView)convertView.findViewById(R.id.task_info);
        tv.setText(task.getTitle());
        tv=(TextView)convertView.findViewById(R.id.task_reward);
        tv.setText(String.format(Locale.ENGLISH,"¥%.2f",task.getPrice()));
        long h=0;
        long m=task.getLeftTime();
        h+=(m/60);
        m%=60;
        String s;
        if(m<=0){
            s="已失效";
        }else if(h==0){
            s=m+"分钟";
        }else{
            s=h+"小时"+m+"分钟";
        }
        tv=(TextView)convertView.findViewById(R.id.time_limit);
        tv.setText(s);
        String f=task.getFromWhere();
        String t=task.getToWhere();
        tv=(TextView)convertView.findViewById(R.id.from_and_to);
        String oc="";
        if(f.length()==0 && t.length()==0){
            tv.setText("");
        }else if(f.length()==0){
            oc="到 #LO "+t;
        }else if(t.length()==0){
            oc="从 #LO "+t;
        }else{
            oc="从 #LO "+f+"到 #LO "+t;
        }
        if(mSize==null){
            mSize=UPublicTool.getScreenSize(getContext(),0.03,0.03);
        }
        SpannableStringBuilder ssb=UPublicTool.addICONtoString(getContext(),oc,"#LO",R.drawable.location,mSize.x,mSize.y);
        tv.setText(ssb);
        return convertView;
    }
    //记录标识地点的徽标的长度和宽度
    private Point mSize=null;
}
