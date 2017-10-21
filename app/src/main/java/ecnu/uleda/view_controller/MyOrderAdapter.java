package ecnu.uleda.view_controller;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.model.MyOrder;

/**
 * Created by TonyDanid on 2017/3/18.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private LayoutInflater mInflater;
    private List<MyOrder> objects;
    private Context mContext;
    private boolean isEmpty = false;
    public MyOrderAdapter(Context context, List<MyOrder> objects) {
        mInflater = LayoutInflater.from(context);
        this.objects = objects;
        mContext = context;
    }

    public void setEmpty() {
        isEmpty = true;
        objects.add(new MyOrder());
        notifyItemInserted(0);
    }

    private Point mSize = null;

    @Override
    public MyOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            TextView emptyView = new TextView(mContext);
            emptyView.setText("未找到符合条件的订单");
            emptyView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextSize(16);
            return new MyOrderViewHolder((emptyView));
        }
        return new MyOrderViewHolder(mInflater.inflate(R.layout.my_order_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return isEmpty && position == 0 ? VIEW_TYPE_EMPTY : VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(MyOrderViewHolder holder, int position) {
        if (position == 0 && isEmpty) return;
        MyOrder order = objects.get(position);
        holder.tasktype.setText(order.getTag().substring(0, 2));

        holder.publishername.setText(order.getAuthorUserName());

        holder.publisherstars.setText(order.getStarString());

        holder.taskinfo.setText(order.getTitle());
        holder.taskreward.setText(String.format(Locale.ENGLISH, "¥%.2f", order.getPrice()));
        long hour;
        long min;
        long sec;
        long time = order.getLeftTime();
        hour = time / 3600;
        min = (time - hour * 3600) / 60;
        sec = time % 60;
        String s;
        if (time <= 0) {
            s = "已失效";
        } else if (min == 0 && hour == 0) {
            s = sec + "秒";
        } else if (hour == 0) {
            s = min + "分钟";
        } else {
            s = hour + "小时" + min + "分钟";
        }

        holder.timelimit.setText(s);
        String f = order.getFromWhere();
        String t = order.getToWhere();

        String oc = "";
        if (f.length() == 0 && t.length() == 0) {
            holder.fromandto.setText("");
        } else if (f.length() == 0) {
            oc = "到 #LO " + t;
        } else if (t.length() == 0) {
            oc = "从 #LO " + t;
        } else {
            oc = "从 #LO " + f + "到 #LO " + t;
        }
        if (mSize == null) {
            mSize = UPublicTool.getScreenSize(mContext, 0.03, 0.03);
        }
        SpannableStringBuilder ssb = UPublicTool.addICONtoString(mContext, oc, "#LO", R.drawable.location, mSize.x, mSize.y);
        holder.fromandto.setText(ssb);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    static class MyOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tasktype;
        TextView publishername;
        TextView publisherstars;
        TextView taskinfo;
        TextView taskreward;
        TextView timelimit;
        TextView fromandto;

        MyOrderViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof TextView) return;
            tasktype = (TextView) itemView.findViewById(R.id.task_type);
            publishername = (TextView) itemView.findViewById(R.id.publisher_name);
            publisherstars = (TextView) itemView.findViewById(R.id.publisher_stars);
            taskinfo = (TextView) itemView.findViewById(R.id.task_info);
            taskreward = (TextView) itemView.findViewById(R.id.task_reward);
            timelimit = (TextView) itemView.findViewById(R.id.time_limit);
            fromandto = (TextView) itemView.findViewById(R.id.from_and_to);
        }
    }


}
