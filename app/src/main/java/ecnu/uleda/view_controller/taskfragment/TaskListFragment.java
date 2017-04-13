package ecnu.uleda.view_controller.taskfragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import ecnu.uleda.view_controller.TaskDetailsActivity;
import ecnu.uleda.view_controller.TaskPostActivity;
import ecnu.uleda.view_controller.widgets.DrawableLeftCenterTextView;
import ecnu.uleda.view_controller.widgets.TaskListFilterWindow;
import ecnu.uleda.view_controller.widgets.TaskListItemDecoration;
import ecnu.uleda.view_controller.widgets.SelectableTitleView;
import ecnu.uleda.view_controller.widgets.XRecyclerView;


/**
 * Created by Shensheng on 2016/11/11.
 */

public class TaskListFragment extends Fragment implements SelectableTitleView.OnTitleSelectedListener {



    @BindArray(R.array.task_type)
    String[] mTitleArray;

//    private ArrayAdapter<String> mMainAdapter;
//    private ArrayAdapter<String> mSortAdapter;
    private List<String> mTitles;

    @BindView(R.id.titles)
    SelectableTitleView mTitleView;

    @BindView(R.id.task_post)
    TextView mPostView;

    @BindView(R.id.shader_full)
    View mShaderAll;

    private Unbinder mUnbinder;

    @OnClick(R.id.task_post)
    void postTask() {
        PostTaskWindow popUpWindow = new PostTaskWindow(getContext());
        popUpWindow.showAsDropDown(mPostView);
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



    private void init() {
        mTitles = new ArrayList<>(Arrays.asList(mTitleArray));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView.setTitles(mTitles);
        mTitleView.setOnTitleSelectedListner(this);
        getFragmentManager().beginTransaction()
                .add(R.id.post_container, TaskMissionFragment.getInstance())
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private int mPos = 0;

    @Override
    public void onItemSelected(int pos, String title) {
        //TODO 三大类的切换
        if (mPos == pos) return;
        switch (pos) {
            case 0:
                getFragmentManager().beginTransaction()
                        .replace(R.id.post_container, TaskMissionFragment.getInstance())
                        .commit();
                mPos = 0;
                break;
            case 1:
                mPos = 1;
                break;
            case 2:
                getFragmentManager().beginTransaction()
                        .replace(R.id.post_container, TaskActivityFragment.getInstance())
                        .commit();
                mPos = 2;
                break;

        }
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
