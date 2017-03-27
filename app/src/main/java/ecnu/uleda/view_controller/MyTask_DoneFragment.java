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
import ecnu.uleda.view_controller.MydoneAdapter;


public class MyTask_DoneFragment extends Fragment {
    private ListView mListView;
    private List<MyOrder> doinglist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_my_task__done, parent, false);
        mListView = (ListView)v.findViewById(R.id.done_item);
        doinglist = new ArrayList<>();

        doinglist.add(new MyOrder()
                .setTitle("一起打篮球")
                .setDescription("")
                .setPrice(BigDecimal.valueOf(30))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("胡楠")
                .setPath("篮球场")
                .setTag("运动锻炼")
                .setGetperson("匡神")

        );
        mListView.setAdapter(new MydoneAdapter(this.getActivity(),doinglist));

        return v;
    }

}
