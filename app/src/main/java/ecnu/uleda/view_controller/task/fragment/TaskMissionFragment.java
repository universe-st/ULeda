package ecnu.uleda.view_controller.task.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

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
import ecnu.uleda.function_module.UTaskManager;
import ecnu.uleda.model.UTask;
import ecnu.uleda.view_controller.task.activity.TaskDetailsActivity;
import ecnu.uleda.view_controller.task.adapter.TaskListAdapter;
import ecnu.uleda.view_controller.widgets.DrawableLeftCenterTextView;
import ecnu.uleda.view_controller.widgets.TaskListFilterWindow;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;

/**
 * Created by jimmyhsu on 2017/4/10.
 */

public class TaskMissionFragment extends Fragment {

    private static final String[] SORT_BY = {UTaskManager.TIME_LAST, UTaskManager.PRICE_DES,
            UTaskManager.PRICE_ASC, UTaskManager.DISTANCE};
    public static final String ACTION_REFRESH = "ecnu.uleda.view_controller.TaskMissionFragment.refresh";

    private List<UTask> mTasksInList = new ArrayList<>();

    private Unbinder mUnbinder;
    private ExecutorService mThreadPool;
    private BroadcastReceiver mReceiver;

    //Widgets go here.
    @BindView(R.id.spinner0)
    DrawableLeftCenterTextView mMainSpinner;

    @BindView(R.id.spinner1)
    DrawableLeftCenterTextView mSortSpinner;

    @BindView(R.id.task_list_view)
    XRecyclerView mTaskListView;

    @BindView(R.id.shader_part)
    View mShaderPart;

    private TaskListFilterWindow mMainDropDownWindow;
    private TaskListFilterWindow mSortDropDownWindow;

    private volatile boolean hasMoreItems = true;
    private volatile boolean isLoadedFromServer = false;

    private UTaskManager mUTaskManager = UTaskManager.getInstance();
    private TaskListAdapter mTaskListAdapter;
    private static final int LOAD_MORE = 0;
    private static final int REFRESH = 1;
    private static final int ERROR = 2;
    private Handler mRefreshHandler;
    private final static ArrayList<String> mMainArrayTask;
    //    private final static ArrayList<String> mMainArrayProject;
//    private final static ArrayList<String> mMainArrayActivity;
    private final static ArrayList<String> mSortArray;

    static {
        mMainArrayTask = new ArrayList<>();
//        mMainArrayProject = new ArrayList<>();
//        mMainArrayActivity = new ArrayList<>();
        mSortArray = new ArrayList<>();
        mMainArrayTask.add("全部");
        mMainArrayTask.add("跑腿代步");
        mMainArrayTask.add("生活服务");
        mMainArrayTask.add("学习帮助");
        mMainArrayTask.add("技术难题");
        mMainArrayTask.add("寻物启示");
//        mMainArrayActivity.add("活动相关");
//        mMainArray.add("运动锻炼");
//        mMainArray.add("项目招人");
//        mMainArray.add("招聘实习");

//        mMainArrayProject.add("全部");
        mMainArrayTask.add("其他");
        mSortArray.add("最新");
        mSortArray.add("报酬从高到低");
        mSortArray.add("报酬从低到高");
        mSortArray.add("距离从近到远");
    }

    private static TaskMissionFragment mInstance;

    public static TaskMissionFragment getInstance() {
        if (mInstance == null) {
            synchronized (TaskMissionFragment.class) {
                if (mInstance == null) {
                    mInstance = new TaskMissionFragment();
                }
            }
        }
        return mInstance;
    }

    @OnClick(R.id.spinner0)
    void typeSelect() {
        if (mMainDropDownWindow == null) {
            mMainDropDownWindow = new TaskListFilterWindow(getContext(), mMainArrayTask);
            mMainDropDownWindow.setOnItemSelectedListener(new TaskListFilterWindow.OnItemSelectedListener() {
                @Override
                public void OnItemSelected(View v, int pos) {
                    mMainSpinner.setText(mMainArrayTask.get(pos));
                    mUTaskManager.setTag(mMainArrayTask.get(pos));
                    mTaskListView.refresh();
                }
            });
            mMainDropDownWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    hideShaderPart();
                }
            });
        }
        showShaderPart();

        mMainDropDownWindow.showAsDropDown(mMainSpinner);
    }

    @OnClick(R.id.spinner1)
    void sortSelect() {
        if (mSortDropDownWindow == null) {
            mSortDropDownWindow = new TaskListFilterWindow(getContext(), mSortArray);
            mSortDropDownWindow.setOnItemSelectedListener(new TaskListFilterWindow.OnItemSelectedListener() {
                @Override
                public void OnItemSelected(View v, int pos) {
                    mSortSpinner.setText(mSortArray.get(pos));
                    mUTaskManager.setSortBy(SORT_BY[pos]);
                    mTaskListView.refresh();
                }
            });
            mSortDropDownWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    hideShaderPart();
                }
            });
        }
        showShaderPart();
        mSortDropDownWindow.showAsDropDown(mSortSpinner);
    }

    private void showShaderPart() {
        mShaderPart.setVisibility(View.VISIBLE);
    }


    private void hideShaderPart() {
        mShaderPart.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mThreadPool.shutdownNow();
        mUnbinder.unbind();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void init() {
        mMainSpinner.setText(mMainArrayTask.get(0));
        mSortSpinner.setText(mSortArray.get(0));
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
                    mThreadPool.submit(new RefreshThread());
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
                        if (isLoadedFromServer) {
//                            if (!hasMoreItems) {
//                                hasMoreItems = true;
//                                mTaskListView.setNoMore(true);
//                            } else {
//                                mTaskListView.setNoMore(false);
//                            }
                            mTaskListView.setNoMore(false);
                        } else {
                            mTaskListView.setNoMore(true);
                        }
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
                startActivity(intent);
            }
        });
        mTaskListView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mThreadPool.submit(new RefreshThread());
            }

            @Override
            public void onLoadMore() {
                mThreadPool.submit(new LoadMoreThread());
            }
        });
        if (mUTaskManager.getTasksInList() == null || mUTaskManager.getTasksInList().size() == 0) {
//            mThreadPool.submit(new RefreshFromFileThread());
            refreshFromFile();
            mThreadPool.submit(new RefreshThread());
        } else {
            mTaskListAdapter.updateDataSource(mTasksInList = mUTaskManager.getTasksInList());
            isLoadedFromServer = true;
            mTaskListView.setLoadingMoreEnabled(true);
        }
    }

    private class RefreshThread extends Thread {
        @Override
        public void run() {
            try {
                mUTaskManager.refreshTaskInList(getContext());
                Message message = new Message();
                message.what = REFRESH;
                isLoadedFromServer = true;
                mRefreshHandler.sendMessage(message);
            } catch (UServerAccessException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = ERROR;
                message.obj = e;
                mRefreshHandler.sendMessage(message);
            }
        }
    }

    private void refreshFromFile() {
        mUTaskManager.refreshTaskInListFromFile(getContext());
        List<UTask> taskList = mUTaskManager.getTasksInList();
        if (taskList != null && taskList.size() > 0) {
            mTasksInList = taskList;
            mTaskListAdapter.updateDataSource(mTasksInList);
            mTaskListView.scrollToPosition(0);
        }
    }

    private class RefreshFromFileThread extends Thread {
        @Override
        public void run() {
            mUTaskManager.refreshTaskInListFromFile(getContext());
            Message message = new Message();
            message.what = REFRESH;
            mRefreshHandler.sendMessage(message);
        }
    }

    private class LoadMoreThread extends Thread {
        @Override
        public void run() {
            try {
                hasMoreItems = mUTaskManager.loadMoreTaskInList(5);
            } catch (UServerAccessException e) {
                //TODO:根据异常的状态决定向主线程的handle发送哪些信息
                e.printStackTrace();
                Message message = new Message();
                message.what = ERROR;
                message.obj = e;
                mRefreshHandler.sendMessage(message);
            }
            Message message = new Message();
            message.what = LOAD_MORE;
            mRefreshHandler.sendMessage(message);
        }
    }

}
