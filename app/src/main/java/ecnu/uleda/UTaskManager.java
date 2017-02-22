package ecnu.uleda;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Shensheng on 2017/1/19.
 * 任务管理者
 * 管理列表和地图上的任务
 */

public class UTaskManager {
    /*
    * 该类遵循单例模式。
    * 持有两个列表，一个表示任务列表中显示的任务
    * 一个表示地图上显示的任务
    * */
    private static UTaskManager sInstance=null;

    private ArrayList<UTask> mTasksInList;
    private ArrayList<UTask> mTasksInMap;

    private int mNumber;
    public static UTaskManager getInstance(){
        if(sInstance==null){
            sInstance=new UTaskManager();
        }
        return sInstance;
    }

    public ArrayList<UTask> getTasksInList(){
        return mTasksInList;
    }

    private UTaskManager(){
        mTasksInList=new ArrayList<>();
        mTasksInMap=new ArrayList<>();
        mTasksInList.add(new UTask()
                .setActiveTime(99999)
                .setAuthorAvatar("")
                .setAuthorCredit(5)
                .setAuthorUserName("张三")
                .setPosition(null)
                .setPrice(new BigDecimal("5.00"))
                .setPostDate(new Date().getTime())
                .setStatus(UTask.UNRECEIVE)
                .setTitle("测试任务")
                .setDescription("测试列表")
                .setAuthorID(0)
                .setTag("跑腿代步")
                .setPath("甲地|乙地")
        );
        mTasksInList.add(new UTask()
                .setActiveTime(99999)
                .setAuthorAvatar("")
                .setAuthorCredit(5)
                .setAuthorUserName("李四")
                .setPosition(null)
                .setPrice(new BigDecimal("5.00"))
                .setPostDate(new Date().getTime())
                .setStatus(UTask.UNRECEIVE)
                .setTitle("测试任务")
                .setDescription("测试列表")
                .setAuthorID(0)
                .setTag("跑腿代步")
                .setPath("甲地|乙地")
        );
    }
    public ListAdapter setListView(ListView listView, Context context){
        //将一个ListView的内容设置为我们的任务
        ListAdapter la=new TaskListAdapter(context,mTasksInList);
        listView.setAdapter(la);
        return la;
    }
    public void refreshTaskInList()throws UServerAccessException{
        /*
        * TODO:访问服务器，更新任务列表。
        * */
    }
    public void loadMoreTaskInList(int n)throws UServerAccessException{
        //TODO：从目前任务列表的最后一项开始向后从服务器获取n个任务项
    }
    public void init()throws UServerAccessException{
        //TODO:初始化任务列表
        refreshTaskInList();
        refreshTasksInMap();
    }

    public void refreshTasksInMap()throws UServerAccessException{
        //TODO:重新从服务器获取显示在地图上的任务

    }
    public ArrayList<UTask> getTasksInMap(){
        return mTasksInMap;
    }
}
