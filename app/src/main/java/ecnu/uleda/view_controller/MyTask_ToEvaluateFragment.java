package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.MyOrder;
import ecnu.uleda.view_controller.My_toevaluateAdapter;


public class MyTask_ToEvaluateFragment extends Fragment {
    private ListView mListView;
    private List<MyOrder> doinglist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_my_task__to_evaluate, parent, false);
        mListView = (ListView)v.findViewById(R.id.to_evaluate_item);
        doinglist = new ArrayList<>();

        doinglist.add(new MyOrder()
                .setTitle("寻教书院遗失U盘")
                .setDescription("")
                .setPrice(BigDecimal.valueOf(3))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("克贡")
                .setPath("到5舍426")
                .setTag("寻物启事")
                .setGetperson("胡楠")

        );
        mListView.setAdapter(new My_toevaluateAdapter(this.getActivity(),doinglist));

    return v;
    }



}
