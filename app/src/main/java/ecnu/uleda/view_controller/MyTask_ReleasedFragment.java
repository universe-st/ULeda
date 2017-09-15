package ecnu.uleda.view_controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.MyOrder;
import ecnu.uleda.model.UTask;
import ecnu.uleda.view_controller.MyOrderAdapter;
import ecnu.uleda.view_controller.task.activity.TaskDetailsActivity;
import ecnu.uleda.view_controller.task.adapter.TaskListAdapter;


public class MyTask_ReleasedFragment extends Fragment {

    private ListView mlistView;
    private List<MyOrder> releasedList = new ArrayList<>();
    private MyOrderAdapter mMyOrderAdapter;
    private int Index = 0;
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
                                releasedList.add(new MyOrder()
                                        .setTag(json.getString("tag"))
                                        .setAuthorUserName(json.getString("authorUsername"))
                                        .setActiveTime(Long.parseLong(json.getString("activetime")))
                                        .setAuthorCredit(Integer.parseInt(json.getString("authorCredit")))
                                        .setDescription(json.getString("description"))
                                        .setTitle(json.getString("title"))
                                        .setPrice(BigDecimal.valueOf(Double.parseDouble(json.getString("price"))))
                                        .setPath(json.getString("path"))
                                );
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    mMyOrderAdapter.notifyDataSetChanged();
                    mlistView.setAdapter(mMyOrderAdapter);
                        break;
                case 2:
                    Intent intent = new Intent(getActivity().getApplication(),TaskDetailsActivity.class);
                    UTask utask = (UTask)msg.obj;
                    intent.putExtra("UTask",utask);
                    startActivity(intent);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {
        View v=inflater.inflate(R.layout.fragment_my_task__released,parent,false);
        mlistView = (ListView) v.findViewById(R.id.list_view);
        mMyOrderAdapter = new MyOrderAdapter(this.getActivity(),releasedList);
        loadUserData();
        mlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mlistView.getLastVisiblePosition() == mlistView.getCount() - 1)
                {
                    Index++;
                    loadUserData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getTask(position);
            }
        });
        return v;
    }
    public void loadUserData()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray jsonArray = ServerAccessApi.getUserTask(Index,0);
                    if(jsonArray.length() > 0)
                    {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = jsonArray;
                        handler.sendMessage(msg);
                    }
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }


            }
        }).start();
    }
    public void  getTask(final int position)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UTask uTask = new UTask();
                try {
                    int index = 0;
                    int temp = 0;
                    if(position == 0)
                    {
                        index = 0;
                    }
                    else
                    {
                        if(position % 10 == 0)
                        {
                            index = position / 10 - 1;
                        }
                        else
                        {
                            index = position / 10;
                        }
                    }
                    temp = position - 10 * index;
                    JSONArray jsonArray = ServerAccessApi.getUserTask(index,0);
                    JSONObject json = jsonArray.getJSONObject(temp);
                    uTask.setTitle(json.getString("title"))
                            .setStatus(Integer.parseInt(json.getString("status")))
                            .setAuthorID(Integer.parseInt(json.getString("author")))
                            .setAuthorAvatar(json.getString("authorAvatar"))
                            .setAuthorUserName(json.getString("authorUsername"))
                            .setAuthorCredit(Integer.parseInt(json.getString("authorCredit")))
                            .setTag(json.getString("tag"))
                            .setDescription(json.getString("description"))
                            .setPostDate(Long.parseLong(json.getString("postdate")))
                            .setActiveTime(Long.parseLong(json.getString("activetime")))
                            .setPath(json.getString("path"))
                            .setPrice(BigDecimal.valueOf(Double.parseDouble(json.getString("price"))))
                            .setPostID(json.getString("postID"))
                            .setTakersCount(Integer.parseInt(json.getString("taker")));
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = uTask;
                    handler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
