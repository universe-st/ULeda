package ecnu.uleda;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

/**
 * Created by TonyDanid on 2017/3/18.
 */

public class MyOrderAdapter extends ArrayAdapter<MyOrder> {
    public MyOrderAdapter(Context context, List<MyOrder> objects){
        super(context,R.layout.my_order_item,objects);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        MyOrder order=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.my_order_item,parent,false);
        }
        if(order==null)
            return LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.my_order_item,parent,false);
        TextView tv=(TextView)convertView.findViewById(R.id.task_type);
        tv.setText(order.getTag().substring(0,2));
        tv=(TextView)convertView.findViewById(R.id.publisher_name);
        tv.setText(order.getAuthorUserName());
        tv=(TextView)convertView.findViewById(R.id.publisher_stars);
        tv.setText(order.getStarString());
        tv=(TextView)convertView.findViewById(R.id.task_info);
        tv.setText(order.getTitle());
        tv=(TextView)convertView.findViewById(R.id.task_reward);
        tv.setText(String.format(Locale.ENGLISH,"¥%.2f",order.getPrice()));
        long hour;
        long min;
        long sec;
        long time=order.getLeftTime();
        hour=time/3600;
        min=(time-hour*3600)/60;
        sec=time%60;
        String s;
        if(time<=0){
            s="已失效";
        }else if(min==0 && hour==0){
            s=sec+"秒";
        }
        else if(hour==0){
            s=min+"分钟";
        }else{
            s=hour+"小时"+min+"分钟";
        }
        tv=(TextView)convertView.findViewById(R.id.time_limit);
        tv.setText(s);
        String f=order.getFromWhere();
        String t=order.getToWhere();
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
