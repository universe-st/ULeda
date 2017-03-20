package ecnu.uleda;

import com.tencent.mapsdk.raster.model.LatLng;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by VinnyHu on 2017/3/13.
 */

public class MyOrder {





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

    private BigDecimal mPrice;
    private LatLng mPosition;
    private String mPostID;
    private static String mGetperson;

    public String  getGetperson() {
        return mGetperson;
    }

    public MyOrder setGetperson(String getperson) {
        mGetperson = getperson;
        return this;
    }

    public String getPostID() {
        return mPostID;
    }

    public MyOrder setPostID(String postID) {
        mPostID = postID;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }
    public BigDecimal getPrice() {
        return mPrice;
    }

    public MyOrder setPrice(BigDecimal price) {
        mPrice = price;
        return this;
    }
    public MyOrder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public int getStatus() {
        return mStatus;
    }

    public MyOrder setStatus(int status) {
        mStatus = status;
        return this;
    }

    public int getAuthorID() {
        return mAuthorID;
    }

    public MyOrder setAuthorID(int authorID) {
        mAuthorID = authorID;
        return this;
    }

    public String getAuthorAvatar() {
        return mAuthorAvatar;
    }

    public MyOrder setAuthorAvatar(String authorAvatar) {
        mAuthorAvatar = authorAvatar;
        return this;
    }

    public String getAuthorUserName() {
        return mAuthorUserName;
    }

    public MyOrder setAuthorUserName(String authorUserName) {
        mAuthorUserName = authorUserName;
        return this;
    }

    public int getAuthorCredit() {
        return mAuthorCredit;
    }

    public MyOrder setAuthorCredit(int authorCredit) {
        mAuthorCredit = authorCredit;
        return this;
    }

    public String getTag() {
        return mTag;
    }

    public MyOrder setTag(String tag) {
        mTag = tag;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public MyOrder setDescription(String description) {
        mDescription = description;
        return this;
    }

    public long getPostDate() {
        return mPostDate;
    }

    public MyOrder setPostDate(long postDate) {
        mPostDate = postDate;
        return this;
    }

    public long getActiveTime() {
        return mActiveTime;
    }

    public MyOrder setActiveTime(long activeTime) {
        mActiveTime = activeTime;
        return this;
    }

    public String getPath() {
        return mPath;
    }

    public MyOrder setPath(String path) {
        mPath = path;
        return this;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public MyOrder setPosition(LatLng position) {
        mPosition = position;
        return this;
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
        else if(ret.length>0){
            return ret[0];
        }
        else{
            return "";
        }
    }
    public long getLeftTime(){
        return (mPostDate+mActiveTime-System.currentTimeMillis()/1000);
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




}
