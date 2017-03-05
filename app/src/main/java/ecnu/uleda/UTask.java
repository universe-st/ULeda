package ecnu.uleda;
import com.tencent.mapsdk.raster.model.LatLng;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by Shensheng on 2017/1/15.
 * 任务类
 */

public class UTask implements Serializable{

    //Task status code.
    public static final int UNRECEIVE=0;
    public static final int IS_RECEIVED=1;
    public static final int WAIT_REWARD=2;
    public static final int IS_DONE=3;
    public static final int INVAILDATION=4;
    public static final int IN_DISPUTE=5;



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

    public String getPostID() {
        return mPostID;
    }

    public UTask setPostID(String postID) {
        mPostID = postID;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }
    public BigDecimal getPrice() {
        return mPrice;
    }

    public UTask setPrice(BigDecimal price) {
        mPrice = price;
        return this;
    }
    public UTask setTitle(String title) {
        mTitle = title;
        return this;
    }

    public int getStatus() {
        return mStatus;
    }

    public UTask setStatus(int status) {
        mStatus = status;
        return this;
    }

    public int getAuthorID() {
        return mAuthorID;
    }

    public UTask setAuthorID(int authorID) {
        mAuthorID = authorID;
        return this;
    }

    public String getAuthorAvatar() {
        return mAuthorAvatar;
    }

    public UTask setAuthorAvatar(String authorAvatar) {
        mAuthorAvatar = authorAvatar;
        return this;
    }

    public String getAuthorUserName() {
        return mAuthorUserName;
    }

    public UTask setAuthorUserName(String authorUserName) {
        mAuthorUserName = authorUserName;
        return this;
    }

    public int getAuthorCredit() {
        return mAuthorCredit;
    }

    public UTask setAuthorCredit(int authorCredit) {
        mAuthorCredit = authorCredit;
        return this;
    }

    public String getTag() {
        return mTag;
    }

    public UTask setTag(String tag) {
        mTag = tag;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public UTask setDescription(String description) {
        mDescription = description;
        return this;
    }

    public long getPostDate() {
        return mPostDate;
    }

    public UTask setPostDate(long postDate) {
        mPostDate = postDate;
        return this;
    }

    public long getActiveTime() {
        return mActiveTime;
    }

    public UTask setActiveTime(long activeTime) {
        mActiveTime = activeTime;
        return this;
    }

    public String getPath() {
        return mPath;
    }

    public UTask setPath(String path) {
        mPath = path;
        return this;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public UTask setPosition(LatLng position) {
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
        return (mPostDate+mActiveTime-new Date().getTime()/1000);
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
