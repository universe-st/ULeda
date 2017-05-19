package ecnu.uleda.view_controller.task.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ecnu.uleda.R;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.view_controller.task.activity.ActivityDetailsActivity;
import ecnu.uleda.view_controller.widgets.BrochureItemDecoration;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class ActivityListFragment extends Fragment {

    private static final int MESSAGE_LOAD_COMPLETE = 0x111;

    public static final int TYPE_ALL = 0;
    public static final int TYPE_SPORTS = 1;
    public static final int TYPE_CLUB = 2;
    public static final int TYPE_CHARITY = 3;

    private int mType;

    private Handler mLoadHandler;

    private XRecyclerView mActivityRv;
    private List<UActivity> mActivityList;
    private List<String> mBrochureUrls;

    private static ActivityListFragment mInstance;
    public static ActivityListFragment getInstance() {
        if (mInstance == null) {
            synchronized (ActivityListFragment.class) {
                if (mInstance == null) {
                    mInstance = new ActivityListFragment();
                }
            }
        }
        return mInstance;
    }


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
                            mLoadHandler.sendEmptyMessage(MESSAGE_LOAD_COMPLETE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
        mLoadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_LOAD_COMPLETE:
                        mActivityRv.loadMoreComplete();
                        break;
                }
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
        mActivityList = new ArrayList<>();
        mBrochureUrls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mActivityList.add(new UActivity(getResources().getString(R.string.activity_example),
                    31.2284700000,
                    121.4064000000,
                    "幽灵地点",
                    "校园",
                    1,
                    "小明",
                    "xiaohong.jpg",
                    null,
                    System.currentTimeMillis() / 1000 + 24 * 3600,
                    20,
                    new ArrayList<>(Arrays.asList(new String[]{String.valueOf(R.drawable.img1),
                            String.valueOf(R.drawable.img2),
                            String.valueOf(R.drawable.img3)}))));
            mBrochureUrls.add(R.drawable.img1 + "," + R.drawable.img2 + "," + R.drawable.img3);
        }
    }

    private class ActivityListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mInflater;
        private SimpleDateFormat df = new SimpleDateFormat("M月d日 HH:mm");

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
            if (uActivity.getAvatar().equals("xiaohong.jpg")) {
                holder.avatar.displayResourceImage(R.drawable.xiaohong);
            } else {
                holder.avatar.displayImage(uActivity.getAvatar());
            }
            holder.time.setText(df.format(new Date(uActivity.getHoldTime())));
            holder.username.setText(uActivity.getAuthorUsername());
            holder.tag.setText("#" + uActivity.getTag());
            holder.title.setText(uActivity.getTitle());
            holder.actTime.setText(df.format(new Date(uActivity.getHoldTime() * 1000)));
            holder.location.setText(uActivity.getLocation());
            holder.itemView.setTag(position);
            holder.brochure.setAdapter(new BrochureAdapter(new ArrayList<>(Arrays.asList(mBrochureUrls.get(position).split(",")))));
            holder.brochure.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            holder.brochure.addItemDecoration(new BrochureItemDecoration(getContext(), 3));
        }

        @Override
        public int getItemCount() {
            return mActivityList.size();
        }
    }

    private void onItemClick(View v, int pos) {
        ActivityDetailsActivity.startActivity(getActivity(), mActivityList.get(pos));
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
        TextView title;
        @BindView(R.id.activity_time)
        TextView time;
        @BindView(R.id.activity_brochure)
        RecyclerView brochure;
        @BindView(R.id.activity_location)
        TextView location;
        @BindView(R.id.activity_act_time)
        TextView actTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

    class BrochureHolder extends RecyclerView.ViewHolder {
        SketchImageView imageView;
        public BrochureHolder(View itemView) {
            super(itemView);
            imageView = (SketchImageView) itemView;
        }
    }
   class BrochureAdapter extends RecyclerView.Adapter<BrochureHolder> {
       private int mImageWidth;
       private List<String> mUrls;
       public BrochureAdapter(List<String> urls) {
           mImageWidth = mActivityRv.getMeasuredWidth() / 3;
           mUrls = urls;
       }

       @Override
       public BrochureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           SketchImageView sketchImageView = new SketchImageView(getContext());
           sketchImageView.setLayoutParams(new RecyclerView.LayoutParams(mImageWidth, mImageWidth));
           sketchImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           return new BrochureHolder(sketchImageView);
       }

       @Override
       public void onBindViewHolder(BrochureHolder holder, int position) {
            holder.imageView.setImageResource(Integer.parseInt(mUrls.get(position)));
       }

       @Override
       public int getItemCount() {
           return 4;
       }
   }
}
