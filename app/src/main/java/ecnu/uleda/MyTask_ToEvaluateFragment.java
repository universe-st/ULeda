package ecnu.uleda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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
                .setTitle("捉拿胡楠")
                .setDescription("捉拿胡楠捉拿胡楠捉拿胡楠")
                .setPrice(BigDecimal.valueOf(15))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("赵铁柱")
                .setPath("从5舍到7舍")
                .setTag("生活任务")
                .setGetperson("张无忌")

        );
        mListView.setAdapter(new My_toevaluateAdapter(this.getActivity(),doinglist));

    return v;
    }



}
