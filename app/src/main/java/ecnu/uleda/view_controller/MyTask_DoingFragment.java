package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.model.MyOrder;
import ecnu.uleda.view_controller.MydoingAdapter;

public class MyTask_DoingFragment extends Fragment {
    private ListView mListView;
    private List<MyOrder> doinglist = new ArrayList<>();;
    private MydoingAdapter mMydoingAdapter;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:

                    JSONArray jsonArray = (JSONArray)msg.obj;
                    for(int i = 0;i < jsonArray.length();i++)
                    {
                        try
                        {
                            JSONObject json = jsonArray.getJSONObject(i);
                            doinglist.add(new MyOrder()
                                    .setTag(json.getString("tag"))
                                    .setAuthorUserName(json.getString("authorUsername"))
                                    .setActiveTime(Long.parseLong(json.getString("activetime")))
                                    .setAuthorCredit(Integer.parseInt(json.getString("authorCredit")))
                                    .setDescription(json.getString("description"))
                                    .setTitle(json.getString("title"))
                                    .setPrice(BigDecimal.valueOf(Double.parseDouble(json.getString("price"))))
                                    .setPath(json.getString("path"))
                                    .setGetperson(json.getString("taker"))

                            );
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                   mMydoingAdapter.notifyDataSetChanged();
                    mListView.setAdapter(mMydoingAdapter);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup  parent,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_my_task__doing, parent, false);
        mListView = (ListView)v.findViewById(R.id.doing_item);
       mMydoingAdapter = new MydoingAdapter(this.getActivity(),doinglist);

       /* doinglist.add(new MyOrder()
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


        );*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray jsonArray = ServerAccessApi.getUserTask(0,1);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = jsonArray;
                    handler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }


            }
        }).start();
        return v;
    }


}
