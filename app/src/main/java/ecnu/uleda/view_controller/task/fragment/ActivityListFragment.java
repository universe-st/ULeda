package ecnu.uleda.view_controller.task.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import ecnu.uleda.R;
import ecnu.uleda.function_module.UActivityManager;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.view_controller.task.activity.ActivityDetailsActivity;
import ecnu.uleda.view_controller.widgets.BrochureItemDecoration;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class ActivityListFragment extends Fragment {

    private static final int MESSAGE_LOAD_COMPLETE = 0x111;
    private static final int MESSAGE_REFRESH_COMPLETE = 0x112;

    private static final String[] TYPES = new String[]{"", "运动", "社团", "公益"};

    private String mType = TYPES[0];

    private Handler mLoadHandler;
    private ExecutorService mThreadPool;
    private OnRefreshListener mListener;

    private RecyclerView mActivityRv;
    private ActivityListAdapter mAdapter;
    private List<UActivity> mActivityList;

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

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivityRv = (RecyclerView) inflater.inflate(R.layout.task_activity_innerview, container, false);
//        mActivityRv.setPullRefreshEnabled(false);
//        mActivityRv.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
//        mActivityRv.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//            }
//
//            @Override
//            public void onLoadMore() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(1000);
//                            mLoadHandler.sendEmptyMessage(MESSAGE_LOAD_COMPLETE);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
//            }
//        });
        mLoadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_LOAD_COMPLETE:
                        int size = mActivityList.size();
                        mActivityList = UActivityManager.INSTANCE.getActivityList();
                        int newSize = mActivityList.size();
                        mAdapter.notifyItemRangeInserted(size, newSize - size);
                        mActivityRv.smoothScrollBy(0, -1);
                        break;
                    case MESSAGE_REFRESH_COMPLETE:
                        mActivityList = UActivityManager.INSTANCE.getActivityList();
                        mAdapter.notifyDataSetChanged();
                        mActivityRv.smoothScrollBy(0, -1);
                        if (mListener != null) {
                            mListener.onRefreshComplete();
                        }
                        break;
                }
            }
        };
        return mActivityRv;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mThreadPool == null || mThreadPool.isShutdown()) {
            mThreadPool = Executors.newCachedThreadPool();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mThreadPool.shutdownNow();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityList = UActivityManager.INSTANCE.getActivityList();
        mThreadPool = Executors.newCachedThreadPool();
        mActivityRv.setAdapter(mAdapter = new ActivityListAdapter(getContext()));
        mActivityRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mActivityRv.addItemDecoration(new TaskListItemDecoration(getContext(), 8, true));
        mActivityRv.setHasFixedSize(true);
        mActivityRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !ViewCompat.canScrollVertically(recyclerView, 1)) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mThreadPool.submit(new RefreshRunnable());
    }

    private void loadMore() {
        final int oldSize = mActivityList.size();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(UActivityManager.INSTANCE.loadMoreActivityInList());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer loadedCount) throws Exception {
                        if (loadedCount < 0) {
                            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                        } else if (loadedCount > 0) {
                            mActivityList = UActivityManager.INSTANCE.getActivityList();
                            int newSize = mActivityList.size();
                            mAdapter.notifyItemRangeInserted(oldSize, newSize - oldSize);
                            mActivityRv.smoothScrollBy(0, -1);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void refresh() {
        mThreadPool.submit(new RefreshRunnable());
    }

    public void setTag(CharSequence text) {
        mType = text.toString();
        if (mType.equals("全部")) {
            mType = "";
        }
        mThreadPool.submit(new RefreshRunnable());
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
            if (uActivity.getAvatar().equals("no")) {
                Glide.with(getContext()).load(R.drawable.xiaohong)
                        .bitmapTransform(new CropCircleTransformation(getContext()))
                        .into(holder.avatar);
            } else {
                Glide.with(getContext()).load(uActivity.getAvatar())
                        .bitmapTransform(new CropCircleTransformation(getContext()))
                        .into(holder.avatar);
            }
            holder.time.setText(df.format(new Date(uActivity.getPostDate() * 1000)));
            holder.username.setText(uActivity.getAuthorUsername());
            holder.tag.setText("#" + uActivity.getTag());
            holder.title.setText(uActivity.getTitle());
            holder.actTime.setText(df.format(new Date(uActivity.getHoldTime())));
            holder.location.setText(uActivity.getLocation());
            holder.itemView.setTag(position);
            if (uActivity.getImgUrls() == null || uActivity.getImgUrls().size() <= 0) {
                holder.brochure.setVisibility(View.GONE);
            } else {
                holder.brochure.setVisibility(View.VISIBLE);
                holder.brochure.setAdapter(new BrochureAdapter(uActivity.getImgUrls()));
                holder.brochure.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                holder.brochure.addItemDecoration(new BrochureItemDecoration(getContext(), 3));
            }
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
        ImageView avatar;
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
        ImageView imageView;
        public BrochureHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
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
           ImageView imageView = new ImageView(getContext());
           imageView.setLayoutParams(new RecyclerView.LayoutParams(mImageWidth, mImageWidth));
           imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           return new BrochureHolder(imageView);
       }

       @Override
       public void onBindViewHolder(BrochureHolder holder, int position) {
           String url = mUrls.get(position);
           if (url.startsWith("http")) {
               Glide.with(getContext()).load(url).into(holder.imageView);
           } else {
               holder.imageView.setImageResource(Integer.parseInt(mUrls.get(position)));
           }
       }

       @Override
       public int getItemCount() {
           return mUrls.size();
       }
   }

   class RefreshRunnable implements Runnable {
       @Override
       public void run() {
           UActivityManager.INSTANCE.refreshActivityInList(mType);
           mLoadHandler.sendEmptyMessage(MESSAGE_REFRESH_COMPLETE);
       }
   }

    interface OnRefreshListener {
        void onRefreshComplete();
    }
}
