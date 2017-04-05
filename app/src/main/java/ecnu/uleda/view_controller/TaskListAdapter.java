package ecnu.uleda.view_controller;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.model.UTask;

/**
 * Created by Shensheng on 2017/1/15.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    //记录标识地点的徽标的长度和宽度
    private Point mSize = null;
    private List<UTask> mDatas = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mListener;

    public TaskListAdapter(Context context, List<UTask> datas) {
        mDatas = datas;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //    @Override
//    @NonNull
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
////        UTask task = getItem(position);
////        //Log.d("TaskListAdapter",position+task.getInformation());
////        if (convertView == null) {
////            convertView = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.task_list_item, parent, false);
////        }
////        if (task == null)
////            return LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.task_list_item, parent, false);
////        TextView tv = (TextView) convertView.findViewById(R.id.task_type);
////        tv.setText(task.getTag().substring(0, 2));
//        tv = (TextView) convertView.findViewById(R.id.publisher_name);
//        tv.setText(task.getAuthorUserName());
//        tv = (TextView) convertView.findViewById(R.id.publisher_stars);
//        tv.setText(task.getStarString());
//        tv = (TextView) convertView.findViewById(R.id.task_info);
//        tv.setText(task.getTitle());
//        tv = (TextView) convertView.findViewById(R.id.task_reward);
//        tv.setText(String.format(Locale.ENGLISH, "¥%.2f", task.getPrice()));
//        long time = task.getLeftTime();
//        String timeDescription = UPublicTool.timeLeft(time);
//        tv = (TextView) convertView.findViewById(R.id.time_limit);
//        tv.setText(timeDescription);
//        String f = task.getFromWhere();
//        String t = task.getToWhere();
//        tv = (TextView) convertView.findViewById(R.id.from_and_to);
//        String oc = "";
//        if (f.length() == 0 && t.length() == 0) {
//            tv.setText("");
//        } else if (f.length() == 0) {
//            oc = "到 #LO " + t;
//        } else if (t.length() == 0) {
//            oc = "从 #LO " + t;
//        } else {
//            oc = "从 #LO " + f + "到 #LO " + t;
//        }
//        if (mSize == null) {
//            mSize = UPublicTool.getScreenSize(, 0.03, 0.03);
//        }
//        SpannableStringBuilder ssb = UPublicTool.addICONtoString(getContext(), oc, "#LO", R.drawable.location, mSize.x, mSize.y);
//        tv.setText(ssb);
//        return convertView;
//    }


    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.task_list_item, parent, false);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(v, (UTask) v.getTag());
                }
            }
        });
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(TaskListAdapter.ViewHolder holder, int position) {
        UTask task = mDatas.get(position);
        holder.mTvType.setText(task.getTag().substring(0, 2));
        holder.mTvPublisherName.setText(task.getAuthorUserName());
        holder.mTvStars.setText(task.getStarString());
        holder.mTvInfo.setText(task.getTitle());
        holder.mTvTaskReward.setText(String.format(Locale.ENGLISH, "¥%.2f", task.getPrice()));
        holder.mTvTimeLimit.setText(UPublicTool.parseTime(task.getLeftTime()));
        holder.mTvFromAndTo.setText(getFromTo(task));
        holder.itemView.setTag(task);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void updateDataSource(List<UTask> newDatas) {
        if (newDatas != null) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new TaskDiffCallback(mDatas, newDatas), true);
            result.dispatchUpdatesTo(this);
        }
        mDatas = newDatas;
    }

    private SpannableStringBuilder getFromTo(UTask task) {
        String f = task.getFromWhere();
        String t = task.getToWhere();
        String oc;
        if (f.length() == 0 && t.length() == 0) {
            oc = "";
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
        return UPublicTool.addICONtoString(mContext, oc, "#LO",
                R.drawable.location, mSize.x, mSize.y);
    }

    public interface OnItemClickListener {
        void onItemClicked(View v, UTask task);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        @BindView(R.id.task_type)
        TextView mTvType;
        @BindView(R.id.publisher_name)
        TextView mTvPublisherName;
        @BindView(R.id.publisher_stars)
        TextView mTvStars;
        @BindView(R.id.task_info)
        TextView mTvInfo;
        @BindView(R.id.task_reward)
        TextView mTvTaskReward;
        @BindView(R.id.time_limit)
        TextView mTvTimeLimit;
        @BindView(R.id.from_and_to)
        TextView mTvFromAndTo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

    class TaskDiffCallback extends DiffUtil.Callback {

        private List<UTask> mOldDatas;
        private List<UTask> mNewDatas;

        TaskDiffCallback(List<UTask> oldDatas, List<UTask> newDatas) {
            mOldDatas = oldDatas;
            mNewDatas = newDatas;
        }

        @Override
        public int getOldListSize() {
            return mOldDatas.size();
        }

        @Override
        public int getNewListSize() {
            return mNewDatas.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            UTask old = mOldDatas.get(oldItemPosition);
            UTask nw = mNewDatas.get(newItemPosition);
            return old.getAuthorID() == nw.getAuthorID()
                    && old.getPostDate() == nw.getPostDate();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            UTask old = mOldDatas.get(oldItemPosition);
            UTask nw = mNewDatas.get(newItemPosition);
            return old.getTitle().equals(nw.getTitle());
        }
    }

}
