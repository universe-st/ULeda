package ecnu.uleda.view_controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.model.MyOrder;
import ecnu.uleda.model.UTask;
import ecnu.uleda.tool.RecyclerViewTouchListener;
import ecnu.uleda.view_controller.task.activity.TaskDetailsActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MyTask_ReleasedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<MyOrder> releasedList = new ArrayList<>();
    private MyOrderAdapter mMyOrderAdapter;
    private int index = 0;
    private int itemCount = 0;
    private boolean hasMoreTask = true;
    private boolean isOnce = false;
    private boolean isEmpty = false;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 2:
                    Intent intent = new Intent(getActivity().getApplication(),TaskDetailsActivity.class);
                    UTask utask = (UTask)msg.obj;
                    intent.putExtra("UTask",utask);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {
        View v = inflater.inflate(R.layout.fragment_my_task__released, parent, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_view);
        mMyOrderAdapter = new MyOrderAdapter(this.getActivity().getApplicationContext(), releasedList);
        if (isOnce && isEmpty) mMyOrderAdapter.setEmpty();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mMyOrderAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder viewHolder) {
                TaskDetailsActivity.startActivityFromMyTask(getActivity(), position, ServerAccessApi.USER_TASK_FLAG_RELEASED);
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder viewHolder) {

            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1) && hasMoreTask) {
                        index++;
                        getReleasedUserTask();
                    }
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isOnce) {
            getReleasedUserTask();
            isOnce = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getReleasedUserTask() {
        hasMoreTask = false; // 避免正在网络请求时再次触发"加载更多"
        Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                e.onNext(ServerAccessApi.getUserTask(index, 0));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<JSONArray>() {
                    @Override
                    public void accept(JSONArray jsonArray) throws Exception {
                        int length = jsonArray.length();
                        if (index == 0) releasedList.clear();
                        if (index == 0 && length == 0) {
                            mMyOrderAdapter.setEmpty();
                            isEmpty = true;
                            return;
                        }
                        for (int i = 0; i < length; i++) {
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
                                releasedList.add(new MyOrder()
                                        .setTag(json.getString("tag"))
                                        .setAuthorUserName(json.getString("authorUsername"))
                                        .setActiveTime(Long.parseLong(json.getString("activetime")))
                                        .setAuthorCredit(Integer.parseInt(json.getString("authorCredit")))
                                        .setDescription(json.getString("description"))
                                        .setTitle(json.getString("title"))
                                        .setPrice(BigDecimal.valueOf(Double.parseDouble(json.getString("price"))))
                                        .setPath(json.getString("path"))
                                );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        TransitionManager.beginDelayedTransition(mRecyclerView, TransitionInflater.from(getContext()).inflateTransition(R.transition.slide_in));
                        mMyOrderAdapter.notifyItemRangeInserted(itemCount, length);
                        itemCount += length;
                        if (length > 0) hasMoreTask = true; // 如果加载到了数据，说明可能还有更多
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMyOrderAdapter = null;
    }

    public void notifyItemRemoved(int taskPos) {
        if (taskPos >= 0 && taskPos < releasedList.size()) {
            releasedList.remove(taskPos);
            mMyOrderAdapter.notifyItemRemoved(taskPos);
        }
    }
}
