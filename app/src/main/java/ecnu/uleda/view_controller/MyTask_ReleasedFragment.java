package ecnu.uleda.view_controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ecnu.uleda.view_controller.MyOrderAdapter;


public class MyTask_ReleasedFragment extends Fragment {

    private ListView mlistView;
    private List<MyOrder> releasedList;
  /*  private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                        releasedList = new ArrayList<>();
                        JSONArray jsonArray = (JSONArray)msg.obj;
                        for(int i = 0;i < jsonArray.length();i++)
                        {
                            try
                            {
                                JSONObject json = jsonArray.getJSONObject(i);
                                releasedList.add(new MyOrder()
                                        .setTitle(json.getString()));
                            }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }


                        }
                        break;

                default:
                        break;
            }
        }
    };*/

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
        UserOperatorController us = UserOperatorController.getInstance();
        String id = us.getId();
        String passport = us.getPassport();
        Toast.makeText(getActivity().getApplicationContext(),id+"*"+passport,Toast.LENGTH_LONG).show();
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray jsonArray = ServerAccessApi.getUserTask(0,0);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = jsonArray;
                    handler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }


            }
        }).start();*/



        mlistView.setAdapter(new MyOrderAdapter(this.getActivity(),releasedList));
        return v;
    }
}
