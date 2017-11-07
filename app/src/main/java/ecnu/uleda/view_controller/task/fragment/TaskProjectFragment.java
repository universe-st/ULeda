package ecnu.uleda.view_controller.task.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import net.phalapi.sdk.PhalApiClientResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UTaskManager;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UTask;
import ecnu.uleda.view_controller.task.activity.TaskDetailsActivity;
import ecnu.uleda.view_controller.task.adapter.TaskListAdapter;
import ecnu.uleda.view_controller.widgets.DrawableLeftCenterTextView;
import ecnu.uleda.view_controller.widgets.TaskListFilterWindow;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by jimmyhsu on 2017/4/10.
 */

public class TaskProjectFragment extends Fragment {

    public static final String ACTION_REFRESH = "ecnu.uleda.view_controller.TaskMissionFragment.refresh";
    private static final int PAGE_SIZE = 5;
    private static final int REQUEST_DETAIL = 1000;

    private List<UTask> mTasksInList = new ArrayList<>();

    private Unbinder mUnbinder;
    private ExecutorService mThreadPool;
    private BroadcastReceiver mReceiver;

    private String mLastItem = null;

    //Widgets go here.
    @BindView(R.id.spinner0)
    DrawableLeftCenterTextView mMainSpinner;

    @BindView(R.id.spinner1)
    DrawableLeftCenterTextView mSortSpinner;
    @BindView(R.id.task_list_view)
    XRecyclerView mTaskListView;

    private volatile boolean hasMoreItems = true;

    private UTaskManager mUTaskManager = UTaskManager.getInstance();
    private TaskListAdapter mTaskListAdapter;
    private static final int LOAD_MORE = 0;
    private static final int REFRESH = 1;
    private static final int ERROR = 2;
    private Handler mRefreshHandler;

    private static TaskProjectFragment mInstance;

    public static TaskProjectFragment getInstance() {
        if (mInstance == null) {
            synchronized (TaskProjectFragment.class) {
                if (mInstance == null) {
                    mInstance = new TaskProjectFragment();
                }
            }
        }
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_mission_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        init();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMainSpinner.setVisibility(View.GONE);
        mSortSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mThreadPool.shutdownNow();
        mUnbinder.unbind();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void init() {
        mThreadPool = Executors.newCachedThreadPool();
        initHandler();
        initRecyclerView();
        initReceiver();
    }

    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!mThreadPool.isShutdown()) {
                    refreshProjects();
                }
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_REFRESH);
        getActivity().registerReceiver(mReceiver, filter);
    }


    private void initHandler() {
        mRefreshHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case REFRESH:
                        mTaskListView.refreshComplete();
                        mTasksInList = mUTaskManager.getTasksInList();
                        mTaskListAdapter.updateDataSource(mTasksInList);
                        if (mTasksInList.size() > 0) {
                            mTaskListView.scrollToPosition(0);
                        }
                        break;
                    case LOAD_MORE:
                        mTaskListView.loadMoreComplete();
                        if (!hasMoreItems) {
                            mTaskListView.setNoMore(true);
                        }
                        mTaskListAdapter.addDataSource(mTasksInList);
                        break;
                    case ERROR:
                        UServerAccessException e = (UServerAccessException) msg.obj;
                        String error = e.getMessage();
                        // 一个只出现过一次后来没法复现的诡异空指针bug,在此加个判断
                        if (mTaskListView != null) {
                            mTaskListView.refreshComplete();
                            mTaskListView.loadMoreComplete();
                        }
                        if (e.getStatus() != UServerAccessException.DATABASE_ERROR && getContext() != null)
                            Toast.makeText(getContext(), "网络异常：" + error, Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        };
    }


    private void initRecyclerView() {
        mTasksInList = new ArrayList<>();
        mTaskListAdapter = new TaskListAdapter(getActivity(), mTasksInList);
        mTaskListAdapter.setHasStableIds(true);
        mTaskListView.setAdapter(mTaskListAdapter);
        mTaskListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mTaskListView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mTaskListView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mTaskListView.setArrowImageView(R.drawable.pull_to_refresh_arrow);
        mTaskListView.addItemDecoration(new TaskListItemDecoration(getContext(), 8, true));
        mTaskListAdapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicked(View v, UTask task) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), TaskDetailsActivity.class);
                        intent.putExtra("UTask", task);
                        startActivityForResult(intent, REQUEST_DETAIL);
            }
        });
        mTaskListView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshProjects();
            }

            @Override
            public void onLoadMore() {
                loadMoreProjects();
            }
        });
        refreshProjects();
    }

    private void loadMoreProjects() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                UserOperatorController uoc = UserOperatorController.getInstance();
                e.onNext(ServerAccessApi.getProjectList(uoc.getId(),
                        uoc.getPassport(),
                        String.valueOf(mLastItem),
                        String.valueOf(PAGE_SIZE),
                        "31.2296,121.403"));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            parseApiResponse(response);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            mTaskListAdapter.addDataSource(mTasksInList);
                        } else {
                            Toast.makeText(getContext(), "获取项目失败：" + response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        mTaskListView.loadMoreComplete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void refreshProjects() {
        Observable.create(new ObservableOnSubscribe<PhalApiClientResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PhalApiClientResponse> e) throws Exception {
                UserOperatorController uoc = UserOperatorController.getInstance();
                e.onNext(ServerAccessApi.getProjectList(uoc.getId(),
                        uoc.getPassport(),
                        "0",
                        String.valueOf(PAGE_SIZE),
                        "31.2296,121.403"));
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            mTasksInList.clear();
                            mLastItem = null;
                            parseApiResponse(response);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        if (response.getRet() == 200) {
                            mTaskListAdapter.updateDataSource(mTasksInList);
                        } else {
                            Toast.makeText(getContext(), "获取项目失败：" + response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        mTaskListView.refreshComplete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void parseApiResponse(PhalApiClientResponse response) throws JSONException {
        JSONArray array = new JSONArray(response.getData());
        int length = array.length();
        hasMoreItems = length >= PAGE_SIZE;
        for (int i = 0; i < length; i++) {
            JSONObject j = array.getJSONObject(i);
            UTask task = new UTask()
                    .setPath(j.getString("path"))
                    .setTitle(j.getString("title"))
                    .setTag(j.getString("tag"))
                    .setPostDate(j.getLong("postdate"))
                    .setPrice(new BigDecimal(j.getString("price")))
                    .setAuthorID(j.getInt("author"))
                    .setAuthorUserName(j.getString("authorUsername"))
                    .setAuthorCredit(j.getInt("authorCredit"))
                    .setPostID(j.getString("postID"))
                    .setActiveTime(j.getLong("activetime"))
                    .setTakersCount(j.getInt("taker_count"));
            try {
                task.setAvatar(j.getString("avatar"));
            } catch (JSONException e) {
                task.setAvatar("xiaohong.jpg");
            }
            mTasksInList.add(task);
        }
        int lastItemIndex = mTasksInList.size() - 1;
        if (lastItemIndex >= 0) {
            mLastItem = mTasksInList.get(lastItemIndex).getPostID();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "项目已失效，不能访问", Toast.LENGTH_SHORT).show();
        }
    }
}
