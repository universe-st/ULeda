package ecnu.uleda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MyTask_ReleasedFragment extends Fragment {

    private ListView mlistView;
    private List<MyOrder> releasedList=new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {

        View v=inflater.inflate(R.layout.fragment_my_task__released,parent,false);
        mlistView = (ListView) v.findViewById(R.id.list_view);

        releasedList.add(new MyOrder()
                .setTitle("捉拿胡楠")
                .setDescription("捉拿胡楠捉拿胡楠捉拿胡楠")
                .setPrice(BigDecimal.valueOf(15))
                .setActiveTime(15)
                .setAuthorCredit(5)
                .setAuthorID(110)
                .setAuthorUserName("赵铁柱")
                .setPath("从5舍到7舍")
                .setTag("生活任务")
        );






        mlistView.setAdapter(new MyOrderAdapter(this.getActivity(),releasedList));
        return v;
    }



}
