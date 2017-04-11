package ecnu.uleda.view_controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UTask;
import ecnu.uleda.function_module.UTaskManager;
import ecnu.uleda.view_controller.widgets.DrawableLeftCenterTextView;
import ecnu.uleda.view_controller.widgets.TaskListFilterWindow;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;
import ecnu.uleda.view_controller.widgets.SelectableTitleView;
import ecnu.uleda.view_controller.widgets.XRecyclerView;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class TaskListFragment extends Fragment implements SelectableTitleView.OnTitleSelectedListener {

    private static final String[] SORT_BY = {UTaskManager.TIME_LAST, UTaskManager.PRICE_DES,
            UTaskManager.PRICE_ASC, UTaskManager.DISTANCE};

    @BindArray(R.array.task_type)
    String[] mTitleArray;

//    private ArrayAdapter<String> mMainAdapter;
//    private ArrayAdapter<String> mSortAdapter;
    private List<String> mTitles;
    private List<UTask> mTasksInList = new ArrayList<>();

    //Widgets go here.
    @BindView(R.id.spinner0)
    DrawableLeftCenterTextView mMainSpinner;

    @BindView(R.id.spinner1)
    DrawableLeftCenterTextView mSortSpinner;

    @BindView(R.id.titles)
    SelectableTitleView mTitleView;

    @BindView(R.id.task_list_view)
    XRecyclerView mTaskListView;

    @BindView(R.id.task_post)
    TextView mPostView;

    @BindView(R.id.shader_full)
    View mShaderAll;

    @BindView(R.id.shader_part)
    View mShaderPart;

    private TaskListFilterWindow mMainDropDownWindow;
    private TaskListFilterWindow mSortDropDownWindow;

    private volatile boolean hasMoreItems = true;
    private Unbinder mUnbinder;

    private class RefreshThread extends Thread {
        @Override
        public void run() {
            try {
                mUTaskManager.refreshTaskInList();
                Message message = new Message();
                message.what = REFRESH;
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


    private UTaskManager mUTaskManager = UTaskManager.getInstance();
    private TaskListAdapter mTaskListAdapter;
    private static final int LOAD_MORE = 0;
    private static final int REFRESH = 1;
    private static final int ERROR = 2;
    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    mTaskListView.refreshComplete();
                    if (!hasMoreItems) {
                        hasMoreItems = true;
                        mTaskListView.setIsnomore(false);
                    }
                    mTasksInList = mUTaskManager.getTasksInList();
                    mTaskListAdapter.updateDataSource(mTasksInList);
                    if (mTasksInList.size() > 0) {
                        mTaskListView.scrollToPosition(0);
                    }
//                    mUTaskManager.setListView(mListView, TaskListFragment.this.getActivity());
//                    Toast.makeText(TaskListFragment.this.getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                    break;
                case LOAD_MORE:
                    mTaskListView.loadMoreComplete();
                    if (!hasMoreItems) {
                        mTaskListView.setIsnomore(true);
                    }
                    mTaskListAdapter.updateDataSource(mTasksInList);
//                    Toast.makeText(TaskListFragment.this.getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    UServerAccessException e = (UServerAccessException) msg.obj;
                    String error = e.getMessage();
                    mTaskListView.refreshComplete();
                    mTaskListView.loadMoreComplete();
                    if (e.getStatus() != UServerAccessException.DATABASE_ERROR)
                        Toast.makeText(TaskListFragment.this.getActivity(), "网络异常：" + error, Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
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

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
    }

    //初始化Spinner
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_fragment, parent, false);
        mUnbinder = ButterKnife.bind(this, v);
        init();
        return v;
    }

    @OnClick(R.id.task_post)
    void postTask() {
        PostTaskWindow popUpWindow = new PostTaskWindow(getContext());
        popUpWindow.showAsDropDown(mPostView);
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
//                    mTaskListView.setRefreshing(true);
                    new RefreshThread().start();
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
//                    mTaskListView.setRefreshing(true);
                    new RefreshThread().start();
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

    private void showShaderAll() {
        mShaderAll.setVisibility(View.VISIBLE);
    }

    private void hideShaderAll() {
        mShaderAll.setVisibility(View.GONE);
    }

    private void hideShaderPart() {
        mShaderPart.setVisibility(View.GONE);
    }

    private void init() {
        mMainSpinner.setText(mMainArrayTask.get(0));
        mSortSpinner.setText(mSortArray.get(0));
        initRecyclerView();
        mTitles = new ArrayList<>(Arrays.asList(mTitleArray));
    }

    private void initRecyclerView() {
        mTasksInList = mUTaskManager.getTasksInList();
        mTaskListAdapter = new TaskListAdapter(getActivity(), mTasksInList);
        mTaskListAdapter.setHasStableIds(true);
        mTaskListView.setAdapter(mTaskListAdapter);
        mTaskListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mTaskListView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mTaskListView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mTaskListView.setArrowImageView(R.drawable.pull_to_refresh_arrow);
        mTaskListView.addItemDecoration(new TaskListItemDecoration(getContext(), 8));
        mTaskListAdapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, UTask task) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TaskDetailsActivity.class);
                intent.putExtra("UTask", task);
                startActivity(intent);
            }
        });
        mTaskListView.setLoadingListener(new ecnu.uleda.view_controller.widgets.XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new RefreshThread().start();
            }

            @Override
            public void onLoadMore() {
                new LoadMoreThread().start();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView.setTitles(mTitles);
        mTitleView.setOnTitleSelectedListner(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mUTaskManager.setListView(mListView, this.getActivity().getApplicationContext());
//        mTaskListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemSelected(int pos, String title) {
        //TODO 三大类的切换
    }

    class PostTaskWindow extends PopupWindow implements PopupWindow.OnDismissListener {

        @BindView(R.id.post_task)
        CardView mCvTask;

        @BindView(R.id.post_project)
        CardView mCvProject;

        @BindView(R.id.post_activity)
        CardView mCvActivity;

        @OnClick(R.id.post_task)
        void postTask() {

            post(TaskPostActivity.TYPE_TASK);
        }
        @OnClick(R.id.post_project)
        void postProject() {
            post(TaskPostActivity.TYPE_PROJECT);
        }
        @OnClick(R.id.post_activity)
        void postActivity() {
            post(TaskPostActivity.TYPE_ACTIVITY);
        }

        public PostTaskWindow(Context context) {
            super(context);
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            View contentView = LayoutInflater.from(context).inflate(R.layout.popup_post_task, null);
            ButterKnife.bind(this, contentView);
            setContentView(contentView);
            setFocusable(true);
            setTouchable(true);
            setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
            setOutsideTouchable(true);
            setAnimationStyle(R.style.post_window_anim);
            setOnDismissListener(this);
        }

        public void showAsDropDown(View v) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_pop_up);
            mCvTask.startAnimation(animation);
            mCvProject.startAnimation(animation);
            mCvActivity.startAnimation(animation);
            mShaderAll.setVisibility(View.VISIBLE);
            super.showAsDropDown(v);
        }

        private void post(int type) {
            dismiss();
            TaskPostActivity.startActivity(getActivity(), type);
        }

        @Override
        public void onDismiss() {
            mShaderAll.setVisibility(View.GONE);
        }
    }
}
