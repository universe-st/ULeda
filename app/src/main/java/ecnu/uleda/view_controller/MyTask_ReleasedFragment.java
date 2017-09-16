package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MyTask_ReleasedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<MyOrder> releasedList = new ArrayList<>();
    private MyOrderAdapter mMyOrderAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {
        View v = inflater.inflate(R.layout.fragment_my_task__released, parent, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_view);
        mMyOrderAdapter = new MyOrderAdapter(this.getActivity().getApplicationContext(), releasedList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mMyOrderAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder viewHolder) {

            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder viewHolder) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                e.onNext(ServerAccessApi.getUserTask(0, 0));
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<JSONArray>() {
                    @Override
                    public void accept(JSONArray jsonArray) throws Exception {
                        int length = jsonArray.length();
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
                        mMyOrderAdapter.notifyDataSetChanged();
                    }
                });
    }
}
