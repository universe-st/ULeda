package ecnu.uleda;

/**
 * Created by TonyDanid on 2017/2/9.
 */

public class AddOptions {
    private  String mName;
    private  int mImageId;

    public AddOptions(String name,int imageid){
        this.mName=name;
        this.mImageId=imageid;
    }

    public int getmImageId() {
        return mImageId;
    }


    public String getmName() {
        return mName;
    }

}
