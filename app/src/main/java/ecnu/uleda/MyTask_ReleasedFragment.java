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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MyTask_ReleasedFragment extends Fragment {

    private ListView mlistView;
    private List<MyOrder> releasedList;
    private List<MyOrder> l;
    private UserOperatorController mUOC;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle b) {

        View v=inflater.inflate(R.layout.fragment_my_task__released,parent,false);
        mlistView = (ListView) v.findViewById(R.id.list_view);
        releasedList = new ArrayList<>();
        try{
            releasedList = getlist();
        }catch (UServerAccessException e)
        {
            e.printStackTrace();
        }

        mlistView.setAdapter(new MyOrderAdapter(this.getActivity(),releasedList));
        return v;
    }

    public List<MyOrder> getlist()throws UServerAccessException
    {
        l = new ArrayList<>();
        mUOC=UserOperatorController.getInstance();
        if(!mUOC.getIsLogined())
        {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        }
        else
        {
            try
            {
                JSONArray jsonArray = ServerAccessApi.getUserTasks(mUOC.getId(),mUOC.getPassport(),0,0);
                l.clear();
                int length = jsonArray.length();
                JSONObject j = jsonArray.getJSONObject(0);
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
                l.add(order);
            }catch (JSONException e)
            {
                e.printStackTrace();
                System.exit(1);
            }catch (UServerAccessException e){
                if(e.getStatus()==416){
                    e.printStackTrace();
                    l.clear();
                }else{
                    throw e;
                }
            }
        }
        return l;
    }

}
