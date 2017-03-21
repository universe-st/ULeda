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
                .setTitle("帮忙重装操作系统")
                .setDescription("")
                .setPrice(BigDecimal.valueOf(15))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("张三")
                .setPath("从5舍到7舍")
                .setTag("学习帮助")
                .setGetperson("李四")

        );
        mListView.setAdapter(new MydoneAdapter(this.getActivity(),doinglist));

        return v;
    }

}
