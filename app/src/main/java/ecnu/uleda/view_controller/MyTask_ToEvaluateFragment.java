package ecnu.uleda.view_controller;

import android.os.Bundle;
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
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.model.MyOrder;
import ecnu.uleda.tool.RecyclerViewTouchListener;
import ecnu.uleda.view_controller.task.activity.TaskDetailsActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MyTask_ToEvaluateFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<MyOrder> doinglist = new ArrayList<>();
    private My_toevaluateAdapter mAdapter;
    private int itemCount = 0;
    private int index = 0;
    private boolean isOnce = false;
    private boolean isEmpty = false;
    private boolean hasMoreTask = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_my_task__to_evaluate, parent, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.to_evaluate_item);
        mRecyclerView.setAdapter(mAdapter = new My_toevaluateAdapter(this.getActivity(),doinglist));
        if (isOnce && isEmpty) mAdapter.setEmpty();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1) && hasMoreTask) {
                        index++;
                        loadOrders();
                    }
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder viewHolder) {
                TaskDetailsActivity.startActivityFromMyTask(getActivity(), position, ServerAccessApi.USER_TASK_FLAG_TO_EVAL);
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder viewHolder) {

            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isOnce) {
            loadOrders();
            isOnce = true;
        }
    }

    private void loadOrders() {
        if (!hasMoreTask) return;
        hasMoreTask = false;
        Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                e.onNext(ServerAccessApi.getUserTask(index, ServerAccessApi.USER_TASK_FLAG_TO_EVAL));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<JSONArray>() {
                    @Override
                    public void accept(JSONArray jsonArray) throws Exception {
                        int length = jsonArray.length();
                        if (index == 0) doinglist.clear();
                        if (index == 0 && length == 0) {
                            mAdapter.setEmpty();
                            isEmpty = true;
                            return;
                        }
                        for (int i = 0; i < length; i++) {
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
                                doinglist.add(new MyOrder()
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
                        TransitionManager.beginDelayedTransition(mRecyclerView,
                                TransitionInflater.from(getContext()).inflateTransition(R.transition.slide_in));
                        mAdapter.notifyItemRangeInserted(itemCount, length);
                        itemCount += length;
                        if (length > 0) hasMoreTask = true; // 如果加载到了数据，说明可能还有更多
                    }
                });
    }

    public void notifyItemRemoved(int taskPos) {
        if (taskPos >= 0 && taskPos < doinglist.size()) {
            doinglist.remove(taskPos);
            mAdapter.notifyItemRemoved(taskPos);
        }
    }
}
