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


public class MyTask_MyEvaluationFragment extends Fragment {
    private ListView mListView;
    private List<MyOrder> doinglist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_my_task__my_evaluation, parent, false);
        mListView = (ListView)v.findViewById(R.id.mytask_item);
        doinglist = new ArrayList<>();

        doinglist.add(new MyOrder()
                .setTitle("带饭到宿舍425")
                .setDescription("")
                .setPrice(BigDecimal.valueOf(45))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("")
                .setPath("到五舍")
                .setTag("跑腿代步")
                .setGetperson("李四")

        );
        mListView.setAdapter(new MytaskAdapter(this.getActivity(),doinglist));

        return v;
    }

}
