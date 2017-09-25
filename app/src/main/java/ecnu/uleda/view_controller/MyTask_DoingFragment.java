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

public class MyTask_DoingFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<MyOrder> doinglist = new ArrayList<>();
    private MydoingAdapter mMydoingAdapter;
    private int index = 0;
    private int itemCount = 0;
    private boolean isOnce = false;
    private boolean isEmpty = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_task__doing, parent, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.doing_item);
        mMydoingAdapter = new MydoingAdapter(getActivity().getApplicationContext(), doinglist);
        if (isOnce && isEmpty) mMydoingAdapter.setEmpty();
        mRecyclerView.setAdapter(mMydoingAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && ViewCompat.canScrollVertically(recyclerView, 1)) {
                    index++;
                    loadUserData();
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder viewHolder) {
                TaskDetailsActivity.startActivityFromMyTask(getActivity(), position, ServerAccessApi.USER_TASK_FLAG_DOING);
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder viewHolder) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isOnce) { // 避免视图被回收时数据源跟着重建
            doinglist.clear();
            loadUserData();
            isOnce = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMydoingAdapter = null;
    }

    public void loadUserData() {
        Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                e.onNext(ServerAccessApi.getUserTask(index, 1));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<JSONArray>() {
                    @Override
                    public void accept(JSONArray jsonArray) throws Exception {
                        int length = jsonArray.length();
                        if (index == 0 && length == 0) {
                            mMydoingAdapter.setEmpty();
                            isEmpty = true;
                            return;
                        }
                        try {
                            for (int i = 0; i < length; i++) {
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
                                        .setGetperson(json.getString("taker"))

                                );

                            }
                            TransitionManager.beginDelayedTransition(mRecyclerView,
                                    TransitionInflater.from(getContext()).inflateTransition(R.transition.slide_in));
                            mMydoingAdapter.notifyItemRangeInserted(itemCount, length);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void notifyItemRemoved(int taskPos) {
        if (taskPos >= 0 && taskPos < doinglist.size()) {
            doinglist.remove(taskPos);
            mMydoingAdapter.notifyItemRemoved(taskPos);
        }
    }

}
