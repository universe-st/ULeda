package ecnu.uleda.view_controller.taskfragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ecnu.uleda.R;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class ActivityListFragment extends Fragment {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_SPORTS = 1;
    public static final int TYPE_CLUB = 2;
    public static final int TYPE_CHARITY = 3;

    private int mType;

    private Handler mRefreshHandler;

    private XRecyclerView mActivityRv;
    private List<UActivity> mActivityList;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setType(TabLayout.Tab tab) {
        mType = tab.getPosition();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivityRv = (XRecyclerView) inflater.inflate(R.layout.task_activity_innerview, container, false);
        mActivityRv.setPullRefreshEnabled(false);
        mActivityRv.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mActivityRv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        mRefreshHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mActivityRv.loadMoreComplete();
            }
        };
        return mActivityRv;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mActivityList.add(new UActivity("xiaohong.jpg", "小蓝", System.currentTimeMillis() / 1000 - 24 * 3600,
                    "校园", getResources().getString(R.string.activity_example)));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityRv.setAdapter(new ActivityListAdapter(getContext()));
        mActivityRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mActivityRv.addItemDecoration(new TaskListItemDecoration(getContext(), 8, false));
    }

    private class ActivityListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mInflater;

        public ActivityListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.activity_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v, (int) v.getTag());
                }
            });
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UActivity uActivity = mActivityList.get(position);
            DisplayOptions options = new DisplayOptions();
            options.setImageShaper(new CircleImageShaper());
            holder.avatar.setOptions(options);
            if (uActivity.getAvatarUrl().equals("xiaohong.jpg")) {
                holder.avatar.displayResourceImage(R.drawable.xiaohong);
            } else {
                holder.avatar.displayImage(uActivity.getAvatarUrl());
            }
            holder.time.setText(UPublicTool.parseTime(uActivity.getReleaseTime()));
            holder.username.setText(uActivity.getUsername());
            holder.tag.setText("#" + uActivity.getTag());
            holder.desc.setText(uActivity.getContent());
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mActivityList.size();
        }
    }

    private void onItemClick(View v, int pos) {
        // TODO 点击事件
        Toast.makeText(getContext(), "点击了 item" + pos, Toast.LENGTH_SHORT).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        @BindView(R.id.activity_avatar)
        SketchImageView avatar;
        @BindView(R.id.activity_username)
        TextView username;
        @BindView(R.id.activity_tag)
        TextView tag;
        @BindView(R.id.activity_description)
        TextView desc;
        @BindView(R.id.activity_time)
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
