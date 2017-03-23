package ecnu.uleda;

import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by VinnyHu on 2017/3/22.
 */

public class UMyTaskManager {

    private static UMyTaskManager sInstance=null;
    private UserOperatorController mUOC;
    private ArrayList<MyOrder> mTasksInList;
    private String mLocation="31.2296,121.403";
    public static UMyTaskManager getInstance(){
        if(sInstance==null){
            sInstance=new UMyTaskManager();
        }
        return sInstance;
    }
    public void setLocation(String loc){
        mLocation=loc;
    }
    public ArrayList<MyOrder> getTasksInList()
    {
        return mTasksInList;
    }
    private UMyTaskManager()
    {
        mTasksInList = new ArrayList<>();
    }
    public ListAdapter setListView(ListView listView, Context context){
        //将一个ListView的内容设置为我们的任务
        ListAdapter la=new MyOrderAdapter(context,mTasksInList);
        listView.setAdapter(la);
        return la;
    }
    public void RefreshTaskInList(int n)throws UServerAccessException
    {
        mUOC=UserOperatorController.getInstance();
        if(!mUOC.getIsLogined())
        {
            throw new UServerAccessException(UServerAccessException.UN_LOGIN);
        }
        else
        {
            try{
                JSONArray jsonArray = ServerAccessApi.getUserTasks(
                        mUOC.getId(),
                        mUOC.getPassport(),
                        0,
                        n
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

            }catch (JSONException e)
            {
                e.printStackTrace();
                System.exit(1);
            }catch (UServerAccessException e){
                if(e.getStatus()==416){
                    e.printStackTrace();
                    mTasksInList.clear();
                }else{
                    throw e;
                }
            }
        }


    }





}
