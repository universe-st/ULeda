package ecnu.uleda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;


public class MyTask_ReleasedFragment extends Fragment {

    private ListView mlistView;
    private List<MyOrder> releasedList;
    private UserOperatorController mUOC;
    private List<MyOrder> mTasksInList;
    private int index = 0;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                {
                    releasedList = new ArrayList<>();
                    releasedList = (List<MyOrder>)msg.obj;
                    break;
                }
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

        releasedList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                mUOC = UserOperatorController.getInstance();
                mTasksInList = new ArrayList<>();
                try
                {
                    JSONArray jsonArray = ServerAccessApi.getUserTasks(
                            mUOC.getId(),
                            mUOC.getPassport(),
                            index,
                            0
                    );
                    mTasksInList.clear();
                    int length = jsonArray.length();
                    for(int i = 0;i < length;i++)
                    {
                        JSONObject j = jsonArray.getJSONObject(i);
                        MyOrder order = new MyOrder()
                                .setTitle(j.getString("title"))
                                .setAuthorUserName(j.getString("authorUsername"))
                                .setAuthorCredit(j.getInt("authorCredit"))
                                .setDescription(j.getString("description"))
                                .setActiveTime(j.getLong("activetime"))
                                .setPostID(j.getString("postID"))
                                .setAuthorID(j.getInt("author"))
                                .setPath( j.getString("path") )
                                .setPrice(new BigDecimal(j.getString("price")))
                                .setPostDate(j.getLong("postdate"))
                                .setTag( j.getString("tag") );
                        mTasksInList.add(order);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = mTasksInList;
                    mHandler.sendMessage(msg);
                }catch (UServerAccessException e)
                {
                    e.printStackTrace();
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        mlistView.setAdapter(new MyOrderAdapter(this.getActivity(),releasedList));
        return v;
    }
}
