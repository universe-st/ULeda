package ecnu.uleda;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Shensheng on 2017/1/19.
 */

public class UTaskManager {
    /*
    * 该类遵循单例模式。
    *
    * */
    private static UTaskManager sInstance=null;

    private ArrayList<UTask> mTaskList;

    public static UTaskManager getInstance(){
        if(sInstance==null){
            sInstance=new UTaskManager();
        }
        return sInstance;
    }

    public ArrayList<UTask> getTasks(){
        return mTaskList;
    }

    private UTaskManager(){
        init();
    }
    public ListAdapter setListView(ListView listView, Context context){
        ListAdapter la=new TaskListAdapter(context,mTaskList);
        listView.setAdapter(la);
        return la;
    }
    public void refresh(){
        //mTaskList.clear();
        //↓测试代码
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            Log.d("UTaskManager",e.toString());
        }
        JSONObject jo=new JSONObject();
        try {
            jo.put(UTask.PUBLISHER_NAME, "路人");
            jo.put(UTask.STAR_COUNT, "2");
            jo.put(UTask.FROM_WHERE, "侠客岛");
            jo.put(UTask.TO_WHERE, "牛家村");
            jo.put(UTask.TYPE, "生活服务");
            jo.put(UTask.PUBLISH_TIME, (new Date()).getTime() - 1000 * 3600 * 5);
            jo.put(UTask.END_TIME, (new Date()).getTime() + 1000 * 3600 * 4);
            jo.put(UTask.INFO, new Date().toString());
            jo.put(UTask.REWARD, "20");
            mTaskList.add(0,new UTask(jo));
        }catch (Exception e){
            Log.d("UTM",e.toString());
        }
        //↑测试代码
    }
    public void loadMore(int n){
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            Log.d("UTaskManager",e.toString());
        }
        for(int i=0;i<n;i++) {
            JSONObject jo=new JSONObject();
            try {
                jo.put(UTask.PUBLISHER_NAME, "路人"+(i+1));
                jo.put(UTask.STAR_COUNT,"2");
                jo.put(UTask.FROM_WHERE,"侠客岛");
                jo.put(UTask.TO_WHERE,"牛家村");
                jo.put(UTask.TYPE,"生活服务");
                jo.put(UTask.PUBLISH_TIME,(new Date()).getTime()-1000*3600*5);
                jo.put(UTask.END_TIME,(new Date()).getTime()+1000*3600*4);
                jo.put(UTask.INFO,"测试");
                jo.put(UTask.REWARD,"9.99");
            }catch (Exception e){
                Log.d("UTaskManager",e.toString());
            }
            mTaskList.add(new UTask(jo));
        }
    }
    private void init(){
        mTaskList=new ArrayList<>();
        JSONObject[] jos=new JSONObject[5];
        for(int i=0;i<5;i++){
            jos[i]=new JSONObject();
        }
        try {
            jos[0].put(UTask.PUBLISHER_NAME,"赵铁柱");
            jos[0].put(UTask.TO_WHERE,"五舍");
            jos[0].put(UTask.FROM_WHERE,"邮局");
            jos[0].put(UTask.REWARD,"2");
            jos[0].put(UTask.END_TIME,String.valueOf(new Date().getTime()+1000*3600*5));
            jos[0].put(UTask.INFO,"有谁顺路经过邮局的话，能不能帮我拿一封信？信封是白色的，上面有我的名字。谢谢了。");
            jos[0].put(UTask.PUBLISH_TIME,String.valueOf(new Date().getTime()-1000*3600*5));
            jos[0].put(UTask.STAR_COUNT,"5");
            jos[0].put(UTask.TYPE,"跑腿代步");
            jos[0].put(UTask.TO_LOCATION_X,"31.2295615001");
            jos[0].put(UTask.TO_LOCATION_Y,"121.4032196544");
            jos[0].put(UTask.FROM_LOCATION_X,"31.2287689789");
            jos[0].put(UTask.FROM_LOCATION_Y,"121.4098710299");

            jos[1].put(UTask.PUBLISHER_NAME,"钱金棒");
            jos[1].put(UTask.TO_WHERE,"四舍");
            jos[1].put(UTask.FROM_WHERE,"食堂");
            jos[1].put(UTask.REWARD,"5.5");
            jos[1].put(UTask.END_TIME,String.valueOf(new Date().getTime()+1000*3600*2/3));
            jos[1].put(UTask.INFO,"饿死宝宝了，哪位大侠帮忙去食堂二楼给我带份扬州炒饭？重谢");
            jos[1].put(UTask.PUBLISH_TIME,String.valueOf(new Date().getTime()-1000*3600*5));
            jos[1].put(UTask.STAR_COUNT,"3");
            jos[1].put(UTask.TYPE,"跑腿代步");
            jos[1].put(UTask.TO_LOCATION_X,"31.2290284411");
            jos[1].put(UTask.TO_LOCATION_Y,"121.4030772732");
            jos[1].put(UTask.FROM_LOCATION_X,"31.2302915001");
            jos[1].put(UTask.FROM_LOCATION_Y,"121.4031336544");

            jos[2].put(UTask.PUBLISHER_NAME,"孙银花");
            jos[2].put(UTask.TO_WHERE,"六舍");
            jos[2].put(UTask.FROM_WHERE,"");
            jos[2].put(UTask.REWARD,"3.5");
            jos[2].put(UTask.END_TIME,String.valueOf(new Date().getTime()+1000*3600*2/3));
            jos[2].put(UTask.INFO,"搬这么重东西要累死了ヽ(*。>Д<)o゜，求帮忙。");
            jos[2].put(UTask.PUBLISH_TIME,String.valueOf(new Date().getTime()-1000*3600*5));
            jos[2].put(UTask.STAR_COUNT,"5");
            jos[2].put(UTask.TYPE,"生活服务");
            jos[2].put(UTask.TO_LOCATION_X,"31.2287689789");
            jos[2].put(UTask.TO_LOCATION_Y,"121.4037562732");

            jos[3].put(UTask.PUBLISHER_NAME,"李铜条");
            jos[3].put(UTask.TO_WHERE,"五舍");
            jos[3].put(UTask.FROM_WHERE,"");
            jos[3].put(UTask.REWARD,"10");
            jos[3].put(UTask.END_TIME,String.valueOf(new Date().getTime()+1000*3600*4/3));
            jos[3].put(UTask.INFO,"要疯了，电脑一直自动重启，感觉不会再好了，有大神帮帮我吗？");
            jos[3].put(UTask.PUBLISH_TIME,String.valueOf(new Date().getTime()-1000*3600*5));
            jos[3].put(UTask.STAR_COUNT,"5");
            jos[3].put(UTask.TYPE,"技术难题");
            jos[3].put(UTask.TO_LOCATION_X,"31.2284184411");
            jos[3].put(UTask.TO_LOCATION_Y,"121.4098710299");

            jos[4].put(UTask.PUBLISHER_NAME,"周锡牛");
            jos[4].put(UTask.TO_WHERE,"图书馆");
            jos[4].put(UTask.FROM_WHERE,"");
            jos[4].put(UTask.REWARD,"5");
            jos[4].put(UTask.END_TIME,String.valueOf(new Date().getTime()+1000*3600*4/3));
            jos[4].put(UTask.INFO,"完了，英语要挂了，求大腿！求大腿！陪我三天速成！");
            jos[4].put(UTask.PUBLISH_TIME,String.valueOf(new Date().getTime()-1000*3600*5));
            jos[4].put(UTask.STAR_COUNT,"3");
            jos[4].put(UTask.TYPE,"学习帮助");
            jos[4].put(UTask.TO_LOCATION_X,"31.2284364411");
            jos[4].put(UTask.TO_LOCATION_Y,"121.4067312732");
        }catch (JSONException e){
            Log.d("UTaskManager",e.toString());
        }
        for(JSONObject jo : jos){
            mTaskList.add(new UTask(jo));
        }
    }

}
