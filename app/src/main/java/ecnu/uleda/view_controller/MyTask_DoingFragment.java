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
import ecnu.uleda.view_controller.MydoingAdapter;

public class MyTask_DoingFragment extends Fragment {
    private ListView mListView;
    private List<MyOrder> doinglist;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup  parent,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_my_task__doing, parent, false);
        mListView = (ListView)v.findViewById(R.id.doing_item);
        doinglist = new ArrayList<>();

        doinglist.add(new MyOrder()
                .setTitle("帮忙重装系统")
                .setDescription("")
                .setPrice(BigDecimal.valueOf(10))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("TonyDanid")
                .setPath("到理科大楼")
                .setTag("学习帮助")
                .setGetperson("徐洪义")
                .setActiveTime(1260)


        );
        mListView.setAdapter(new MydoingAdapter(this.getActivity(),doinglist));


        return v;
    }


}
