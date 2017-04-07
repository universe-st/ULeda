package ecnu.uleda.view_controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UTask;
import ecnu.uleda.function_module.UTaskManager;
import ecnu.uleda.view_controller.widgets.HorizontalItemDecoration;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class TaskListFragment extends Fragment implements SelectableTitleView.OnTitleSelectedListener {

    private static final String[] SORT_BY = {UTaskManager.TIME_LAST, UTaskManager.PRICE_DES,
            UTaskManager.PRICE_ASC, UTaskManager.DISTANCE};

    @BindArray(R.array.task_type)
    String[] mTitleArray;

    private ArrayAdapter<String> mMainAdapter;
    private ArrayAdapter<String> mSortAdapter;
    private List<String> mTitles;
    private List<UTask> mTasksInList = new ArrayList<>();

    //Widgets go here.
    @BindView(R.id.spinner0)
    Spinner mMainSpinner;

    @BindView(R.id.spinner1)
    Spinner mSortSpinner;

    @BindView(R.id.titles)
    SelectableTitleView mTitleView;

    @BindView(R.id.task_list_view)
    XRecyclerView mTaskListView;

    @BindView(R.id.task_post)
    TextView mPostView;

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
//                    mUTaskManager.setListView(mListView, TaskListFragment.this.getActivity());
                    mTaskListAdapter.updateDataSource(mTasksInList);
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
    private final static ArrayList<String> mMainArray;
    private final static ArrayList<String> mSortArray;

    static {
        mMainArray = new ArrayList<>();
        mSortArray = new ArrayList<>();
        mMainArray.add("全部");
        mMainArray.add("跑腿代步");
        mMainArray.add("生活服务");
        mMainArray.add("学习帮助");
        mMainArray.add("技术难题");
        mMainArray.add("寻物启示");
        mMainArray.add("活动相关");
        mMainArray.add("运动锻炼");
        mMainArray.add("项目招人");
        mMainArray.add("招聘实习");
        mMainArray.add("其他");
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


    @OnItemSelected(R.id.spinner0)
    void onMainItemSelected(int pos) {
        mUTaskManager.setTag(mMainArray.get(pos));
        new RefreshThread().start();
    }

    @OnItemSelected(R.id.spinner1)
    void onSortItemSelected(int pos) {
        mUTaskManager.setSortBy(SORT_BY[pos]);
        new RefreshThread().start();
    }

    @OnClick(R.id.task_post)
    void postTask() {
        PostTaskWindow popUpWindow = new PostTaskWindow(getContext());
        popUpWindow.showAsDropDown(mPostView);
    }

    private void init() {
        mMainSpinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.DST);
        mSortSpinner.getBackground().setColorFilter(0XFFFFFF, PorterDuff.Mode.DST);

        mMainAdapter = new ArrayAdapter<>(this.getActivity().getApplicationContext(),
                R.layout.u_spiner_text_item,
                mMainArray);
        mMainAdapter.setDropDownViewResource(R.layout.u_spiner_dropdown_item);
        mMainSpinner.setAdapter(mMainAdapter);
        mSortAdapter = new ArrayAdapter<>(this.getActivity().getApplicationContext(),
                R.layout.u_spiner_text_item,
                mSortArray);
        mSortAdapter.setDropDownViewResource(R.layout.u_spiner_dropdown_item);
        mSortSpinner.setAdapter(mSortAdapter);
        initRecyclerView();
        mTitles = new ArrayList<>(Arrays.asList(mTitleArray));
    }

    private void initRecyclerView() {
        mTasksInList = mUTaskManager.getTasksInList();
        mTaskListAdapter = new TaskListAdapter(getActivity(), mTasksInList);
        mTaskListView.setAdapter(mTaskListAdapter);
        mTaskListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mTaskListView.setRefreshProgressStyle(ProgressStyle.Pacman);
        mTaskListView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mTaskListView.setArrowImageView(R.drawable.pull_to_refresh_arrow);
        mTaskListView.addItemDecoration(new HorizontalItemDecoration(getContext(), 8));
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

    class PostTaskWindow extends PopupWindow {

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
            setBackgroundDrawable(new BitmapDrawable());
            setOutsideTouchable(true);
            setAnimationStyle(R.style.post_window_anim);
        }

        public void showAsDropDown(View v) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_pop_up);
            mCvTask.startAnimation(animation);
            mCvProject.startAnimation(animation);
            mCvActivity.startAnimation(animation);
            super.showAsDropDown(v);
        }

        private void post(int type) {
            dismiss();
            TaskPostActivity.startActivity(getActivity(), type);
        }

    }
}
