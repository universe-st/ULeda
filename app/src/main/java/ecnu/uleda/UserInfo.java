package ecnu.uleda;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shensheng on 2016/10/28.
 */

public class UserInfo {
    public static final String NAME="name";
    public static final String AVATAR="avatar";
    //测试代码
    public int mResource;
    //传入头像资源ID
    //测试代码
    private JSONObject mJSON;
    public UserInfo(JSONObject json){
        mJSON=json;
    }
    public String getName(){
        return get(NAME);
    }

    public String getAvatar(){
        return get(AVATAR);
    }

    private String get(String key){
        String ret="#undefined";
        try{
            ret=mJSON.getString(key);
        }catch (JSONException e){
            Log.d("UserInfo",e.toString());
        }
        return ret;
    }
}
