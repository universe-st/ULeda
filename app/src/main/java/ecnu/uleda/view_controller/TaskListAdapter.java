package ecnu.uleda.view_controller;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
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
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

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
        DisplayOptions options = new DisplayOptions();
        options.setImageShaper(new CircleImageShaper());
        holder.mAvatar.setOptions(options);
        if (task.getAvatar().equals("xiaohong.jpg")) {
            holder.mAvatar.displayResourceImage(R.drawable.xiaohong);
        } else {
            holder.mAvatar.displayImage(task.getAvatar());
        }
//        holder.mTvType.setText(task.getTag().substring(0, 2));
        holder.mTvPublisherName.setText(task.getAuthorUserName());
        holder.mTvStars.setText(task.getStarString());
        holder.mTvInfo.setText(task.getTitle());
        holder.mTvTaskReward.setText(String.format(Locale.ENGLISH, "¥ %.2f", task.getPrice()));
        holder.mTvTimeLimit.setText("截止至 " + UPublicTool.parseTime(task.getLeftTime()));
        holder.mTvFromAndTo.setText(getFromTo(task));
        holder.mTvTakesCount.setText(task.getTakersCount() + "人接单");
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
            oc = "到" + t;
        } else if (t.length() == 0) {
            oc = "从" + t;
        } else {
            oc = "从" + f + "到" + t;
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
        @Nullable
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
        @BindView(R.id.avatar)
        SketchImageView mAvatar;
        @BindView(R.id.takers_count)
        TextView mTvTakesCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
            Typeface roboto = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
            mTvPublisherName.setTypeface(roboto);
            mTvTimeLimit.setTypeface(roboto);
            mTvFromAndTo.setTypeface(roboto);
            mTvTakesCount.setTypeface(roboto);
            mTvTaskReward.setTypeface(roboto);
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
