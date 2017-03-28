package ecnu.uleda.view_controller;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ecnu.uleda.model.MyOrder;
import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;

/**
 * Created by VinnyHu on 2017/3/19.
 */

public class My_toevaluateAdapter extends ArrayAdapter<MyOrder> {
    public My_toevaluateAdapter(Context context, List<MyOrder> objects){
        super(context, R.layout.to_evaluate,objects);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        MyOrder order=getItem(position);
        View v;
        ViewHolder viewHolder;

        if(convertView==null)
        {
            v = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.to_evaluate,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tasktype = (TextView) v.findViewById(R.id.task_type);
            viewHolder.publishername = (TextView)v.findViewById(R.id.publisher_name);
            viewHolder.publisherstars = (TextView)v.findViewById(R.id.publisher_stars);
            viewHolder.taskinfo = (TextView)v.findViewById(R.id.task_info);
            viewHolder.taskreward = (TextView)v.findViewById(R.id.task_reward);
            viewHolder.timelimit = (TextView)v.findViewById(R.id.time_limit);
            viewHolder.fromandto = (TextView)v.findViewById(R.id.from_and_to);
            viewHolder.getperson = (TextView)v.findViewById(R.id.getperson_name);
            v.setTag(viewHolder);
        }
        else
        {
            v = convertView;
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.tasktype.setText(order.getTag().substring(0,2));

        viewHolder.publishername.setText(order.getAuthorUserName());

        viewHolder.publisherstars.setText(order.getStarString());

        viewHolder.taskinfo.setText(order.getTitle());
        viewHolder.taskreward.setText(String.format(Locale.ENGLISH,"¥%.2f",order.getPrice()));
        viewHolder.getperson.setText(order.getGetperson());
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

        viewHolder.timelimit.setText(s);
        String f=order.getFromWhere();
        String t=order.getToWhere();

        String oc="";
        if(f.length()==0 && t.length()==0){
            viewHolder.fromandto.setText("");
        }else if(f.length()==0){
            oc="到 #LO "+t;
        }else if(t.length()==0){
            oc="从 #LO "+t;
        }else{
            oc="从 #LO "+f+"到 #LO "+t;
        }
        if(mSize==null){
            mSize= UPublicTool.getScreenSize(getContext(),0.03,0.03);
        }
        SpannableStringBuilder ssb=UPublicTool.addICONtoString(getContext(),oc,"#LO",R.drawable.location,mSize.x,mSize.y);
        viewHolder.fromandto.setText(ssb);
        return v;
    }
    private Point mSize=null;

    class ViewHolder
    {
        TextView tasktype;
        TextView publishername;
        TextView publisherstars;
        TextView taskinfo;
        TextView taskreward;
        TextView timelimit;
        TextView fromandto;
        TextView getperson;
    }

}
