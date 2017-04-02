package ecnu.uleda.view_controller;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UTask;
import ecnu.uleda.function_module.UTaskManager;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class TaskListFragment extends Fragment {

    private static final String[] SORT_BY = {UTaskManager.TIME_LAST, UTaskManager.PRICE_DES,
            UTaskManager.PRICE_ASC, UTaskManager.DISTANCE};
    public static final String[] TITLES = {"学习", "生活", "娱乐"};
    //任务列表

    private ArrayAdapter<String> mMainAdapter;
    private ArrayAdapter<String> mSortAdapter;
    private List<String> mTitles;

    //Widgets go here.
    @BindView(R.id.spinner0)
    Spinner mMainSpinner;

    @BindView(R.id.spinner1)
    Spinner mSortSpinner;

    @BindView(R.id.task_list_view)
    RefreshListView mListView;

    @BindView(R.id.titles)
    SelectableTitleView mTitleView;

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
                mUTaskManager.loadMoreTaskInList(5);
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
                    mListView.completeRefresh();
                    mUTaskManager.setListView(mListView, TaskListFragment.this.getActivity());
                    mTaskListAdapter.notifyDataSetChanged();
                    Toast.makeText(TaskListFragment.this.getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                    break;
                case LOAD_MORE:
                    mListView.completeRefresh();
                    mTaskListAdapter.notifyDataSetChanged();
                    Toast.makeText(TaskListFragment.this.getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    UServerAccessException e = (UServerAccessException) msg.obj;
                    String error = e.getMessage();
                    mListView.completeRefresh();
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

    @OnItemClick(R.id.task_list_view)
    void onListItemClick(ListView v, int pos) {
        UTask task = (UTask) v.getItemAtPosition(pos);
        Intent intent = new Intent(getActivity().getApplicationContext(), TaskDetailsActivity.class);
        intent.putExtra("UTask", task);
        startActivity(intent);
    }

//    private void setListViewClick() {
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //Thanks to MicroDog.
//
//            }
//        });
//    }

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
        TaskPostActivity.startActivity(getContext());
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
        mTaskListAdapter = (TaskListAdapter) mUTaskManager
                .setListView(mListView, this.getActivity().getApplicationContext());
        mListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                new RefreshThread().start();
            }

            @Override
            public void onLoadingMore() {
                new LoadMoreThread().start();
            }
        });
        mTitles = new ArrayList<>(Arrays.asList(TITLES));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView.setTitles(mTitles);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUTaskManager.setListView(mListView, this.getActivity().getApplicationContext());
        mTaskListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
