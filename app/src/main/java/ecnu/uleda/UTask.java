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

public class UTask implements Serializable{

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
    //Task status code.
    public static final int UNRECEIVE=0;
    public static final int IS_RECEIVED=1;
    public static final int WAIT_REWARD=2;
    public static final int IS_DONE=3;
    public static final int INVAILDATION=4;
    public static final int IN_DISPUTE=5;

    private JSONObject mJSON;


    private String mTitle;
    private int mStatus;
    private int mAuthorID;
    private String mAuthorAvatar;
    private String mAuthorUserName;
    private int mAuthorCredit;
    private String mTag;
    private String mDescription;
    private long mPostDate;
    private long mActiveTime;
    private String mPath;
    private String mPrice;
    private LatLng mPosition;


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getAuthorID() {
        return mAuthorID;
    }

    public void setAuthorID(int AuthorID) {
        mAuthorID = AuthorID;
    }

    public String getAuthorAvatar() {
        return mAuthorAvatar;
    }

    public void setAuthorAvatar(String AuthorAvatar) {
        mAuthorAvatar = AuthorAvatar;
    }

    public String getAuthorUserName() {
        return mAuthorUserName;
    }

    public void setAuthorUserName(String authorUserName) {
        mAuthorUserName = authorUserName;
    }

    public int getAuthorCredit() {
        return mAuthorCredit;
    }

    public void setAuthorCredit(int authorCredit) {
        mAuthorCredit = authorCredit;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getPostDate() {
        return mPostDate;
    }

    public void setPostDate(long postDate) {
        mPostDate = postDate;
    }

    public long getActiveTime() {
        return mActiveTime;
    }

    public void setActiveTime(long activeTime) {
        mActiveTime = activeTime;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public BigDecimal getPrice() {
        return new BigDecimal(mPrice);
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng position) {
        mPosition = position;
    }

    public String getFromWhere(){
        String[] ret=mPath.split("\\|");
        if(ret.length>1) {
            return ret[0];
        }else {
            return "";
        }
    }

    public String getToWhere(){
        String[] ret=mPath.split("\\|");
        if(ret.length>1) {
            return ret[1];
        }
        else{
            return ret[0];
        }
    }
    public long getLeftTime(){
        return mPostDate+mActiveTime-new Date().getTime()/1000;
    }
    public String getStarString(){
        int c=mAuthorCredit;
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
    /*
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
    }*/
}
