package ecnu.uleda;

import android.util.Log;

import com.tencent.mapsdk.raster.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Shensheng on 2017/1/15.
 */

public class UTask {

    public static final String TYPE="type";
    public static final String PUBLISHER_NAME="publisher_name";
    public static final String FROM_WHERE="from";
    public static final String TO_WHERE="to";
    public static final String PUBLISH_TIME="publish_time";
    public static final String END_TIME="end_time";
    public static final String STAR_COUNT="star_count";
    public static final String REWARD="reward";
    public static final String INFO="info";
    public static final String FROM_LOCATION_X="from_location_x";
    public static final String FROM_LOCATION_Y="from_location_y";
    public static final String TO_LOCATION_X="to_location_x";
    public static final String TO_LOCATION_Y="to_location_y";

    private JSONObject mJSON;
    public UTask(JSONObject json){
        mJSON=json;
    }
    public String getInformation(){
        String t=get(INFO);
        if(t==null)t="";
        return t;
    }

    public String getShortInfo(int n){
        String str=getInformation();
        if(str.length()<=n){
            return str;
        }
        str=str.substring(0,n)+"...";
        return str;
    }
    public String getPublisherName(){
        return get(PUBLISHER_NAME);
    }
    public String getType(){
        return get(TYPE);
    }
    public JSONObject toJSON(){
        return mJSON;
    }

    public LatLng getFromLocation(){
        String str_x=get(FROM_LOCATION_X);
        String str_y=get(FROM_LOCATION_Y);
        if(str_x==null || str_y==null)return null;
        try {
            double x = Double.parseDouble(str_x);
            double y = Double.parseDouble(str_y);
            return new LatLng(x, y);
        }catch (NumberFormatException e){
            return null;
        }
    }

    public LatLng getToLocation(){
        String str_x=get(TO_LOCATION_X);
        String str_y=get(TO_LOCATION_Y);
        if(str_x==null || str_y==null)return null;
        try {
            double x=Double.parseDouble(str_x);
            double y=Double.parseDouble(str_y);
            return new LatLng(x,y);
        }catch (NumberFormatException e){
            return null;
        }
    }
    public String getShortType(){
        String r=get(TYPE);
        if(r==null)return null;
        return r.substring(0,2);
    }

    public String getFromWhere(){
        return get(FROM_WHERE);
    }

    public String getToWhere(){
        return get(TO_WHERE);
    }

    public int getStarCount(){
        return Integer.valueOf(get(STAR_COUNT));
    }

    public Date getPublishTime(){
        long t=Long.valueOf(get(PUBLISH_TIME));
        return new Date(t);
    }

    public Date getEndTime(){
        long t=Long.valueOf(get(END_TIME));
        return new Date(t);
    }

    public int getLeftTime(){
        Date a=new Date();
        Date b=getEndTime();
        long l=(b.getTime()-a.getTime())/60000;
        return (int)l;
    }

    public BigDecimal getReward(){
        String r=get(REWARD);
        return new BigDecimal(r);
    }

    public String getStarString(){
        int c=getStarCount();
        StringBuilder s=new StringBuilder();
        for(int i=0;i<5;i++){
            if(i<c){
                s.append("★");
            }else{
                s.append("☆");
            }
        }
        return s.toString();
    }

    private String get(String paramName){
        String ret=null;
        try{
            ret=mJSON.getString(paramName);
        }catch (JSONException e){
            Log.d("UTask",e.toString());
        }
        return ret;
    }

    public static void UTaskArrayListSort(ArrayList<UTask> tasks){
        //这个方法用来排序
    }

    private static void sortByRewardDEC(ArrayList<UTask> tasks){
        Collections.sort(tasks, new Comparator<UTask>() {
            @Override
            public int compare(UTask t1, UTask t2) {
                return -t1.getReward().compareTo(t2.getReward());
            }
        });
    }

    private static void sortByRewardINC(ArrayList<UTask> tasks){
        Collections.sort(tasks, new Comparator<UTask>() {
            @Override
            public int compare(UTask t1, UTask t2) {
                return t1.getReward().compareTo(t2.getReward());
            }
        });
    }
}
