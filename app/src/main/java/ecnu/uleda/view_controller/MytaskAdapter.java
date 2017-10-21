package ecnu.uleda.view_controller;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ecnu.uleda.R;
import ecnu.uleda.model.MyOrder;
import ecnu.uleda.tool.UPublicTool;

/**
 * Created by VinnyHu on 2017/3/19.
 */

public class MytaskAdapter extends RecyclerView.Adapter<MytaskAdapter.MyDoneViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MyOrder> objects;
    private boolean isEmpty = false;

    public MytaskAdapter(Context context, List<MyOrder> objects) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public void setEmpty() {
        isEmpty = true;
        objects.add(new MyOrder());
        notifyItemInserted(0);
    }

    private Point mSize = null;

    @Override
    public MyDoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            TextView emptyView = new TextView(mContext);
            emptyView.setText("未找到符合条件的订单");
            emptyView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextSize(16);
            return new MyDoneViewHolder(emptyView);
        }
        return new MyDoneViewHolder(mInflater.inflate(R.layout.my_task, parent, false));
    }

    @Override
    public void onBindViewHolder(MyDoneViewHolder viewHolder, int position) {
        MyOrder order = objects.get(position);
        if (isEmpty || order == null) return;
        viewHolder.tasktype.setText(order.getTag().substring(0,2));
        viewHolder.publishername.setText(order.getAuthorUserName());
        viewHolder.publisherstars.setText(order.getStarString());
        viewHolder.taskinfo.setText(order.getTitle());
        viewHolder.taskreward.setText(String.format(Locale.ENGLISH,"¥%.2f",order.getPrice()));
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
            mSize= UPublicTool.getScreenSize(mContext,0.03,0.03);
        }
        SpannableStringBuilder ssb=UPublicTool.addICONtoString(mContext,oc,"#LO",R.drawable.location,mSize.x,mSize.y);
        viewHolder.fromandto.setText(ssb);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isEmpty && position == 0 ? VIEW_TYPE_EMPTY : VIEW_TYPE_NORMAL;
    }

    static class MyDoneViewHolder extends RecyclerView.ViewHolder {
        TextView tasktype;
        TextView publishername;
        TextView publisherstars;
        TextView taskinfo;
        TextView taskreward;
        TextView timelimit;
        TextView fromandto;
        TextView getperson;

        public MyDoneViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof TextView) return;
            tasktype = (TextView) itemView.findViewById(R.id.task_type);
            publishername = (TextView) itemView.findViewById(R.id.publisher_name);
            publisherstars = (TextView) itemView.findViewById(R.id.publisher_stars);
            taskinfo = (TextView) itemView.findViewById(R.id.task_info);
            taskreward = (TextView) itemView.findViewById(R.id.task_reward);
            timelimit = (TextView) itemView.findViewById(R.id.time_limit);
            fromandto = (TextView) itemView.findViewById(R.id.from_and_to);
            getperson = (TextView) itemView.findViewById(R.id.getperson_name);
        }
    }

}
