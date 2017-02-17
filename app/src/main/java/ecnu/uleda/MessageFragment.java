package ecnu.uleda;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shensheng on 2016/11/11.
 */

public class MessageFragment extends Fragment {

    private List<UMessage> MessageList ;
    private MessageListAdapter adapter;
    private ListView MessageListview;

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        MessageList=createUser();//初始化消息列表
        adapter=new MessageListAdapter(this.getActivity().getApplicationContext()
                ,R.layout.message_list_item,MessageList);
        View view=inflater.inflate(R.layout.message_fragment,container,false);
        MessageListview=(ListView)view.findViewById(R.id.message_list_view);
        MessageListview.setAdapter(adapter);
        return view;
    }

    private List<UMessage>createUser(){
        List<UMessage> MList= new ArrayList<>();

        UMessage m1=new UMessage("您的跑腿任务已完成",R.drawable.user2,
                "刚刚","点击对对方进行评价",R.drawable.white);
        MList.add(m1);
        UMessage m2=new UMessage("社区消息",R.drawable.user3,"昨天","[管理员@了你]",
                R.drawable.white);
        MList.add(m2);
        UMessage m3=new UMessage("致幻Trance",R.drawable.user1,"刚刚","在吗?",
                R.drawable.oneo);
        MList.add(m3);
        UMessage m4=new UMessage("赵铁柱",R.drawable.user4,"星期三",
                "我没看到 我不是和你们一起去的吗", R.drawable.white);
        MList.add(m4);
        UMessage m5=new UMessage("恭喜你发现一枚美少女",R.drawable.user5,"08-21","好吧",
                R.drawable.white);
        MList.add(m5);



        return MList;

    }


}
