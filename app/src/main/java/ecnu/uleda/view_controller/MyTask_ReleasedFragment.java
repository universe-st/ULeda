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
import ecnu.uleda.view_controller.MyOrderAdapter;


public class MyTask_ReleasedFragment extends Fragment {

    private ListView mlistView;
    private List<MyOrder> releasedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {
        View v=inflater.inflate(R.layout.fragment_my_task__released,parent,false);
        mlistView = (ListView) v.findViewById(R.id.list_view);

        releasedList = new ArrayList<>();
        releasedList.add(new MyOrder()
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

        mlistView.setAdapter(new MyOrderAdapter(this.getActivity(),releasedList));
        return v;
    }
}
