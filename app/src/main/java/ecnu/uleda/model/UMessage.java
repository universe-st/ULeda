package ecnu.uleda.model;

/**
 * Created by TonyDanid on 2017/1/20.
 */

public class UMessage {
    private  String mName;
    private  int mImageId;
    private  String mTime;
    private  String mMessage;
    private  int mHint;


    public UMessage(String name, int imageId,String time,String message,int hint) {
        this.mName = name;
        this.mImageId = imageId;
        this.mTime=time;
        this.mMessage=message;
        this.mHint=hint;
    }


    public  String getName() {
        return mName;
    }

    public  int getImageId() {return mImageId;}

    public  String getTime(){return mTime;}

    public  String getMessage(){return mMessage;}

    public  int getHint(){return mHint;}

}
