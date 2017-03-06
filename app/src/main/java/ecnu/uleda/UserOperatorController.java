package ecnu.uleda;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Shensheng on 2017/2/21.
 * 控制用户的操作
 */

public class UserOperatorController {
    private static UserOperatorController sUOC=null;
    public static UserOperatorController getInstance(){
        if(sUOC==null){
            sUOC=new UserOperatorController();
        }
        return sUOC;
    }

    private String mUserName;
    private String mPassword;
    private String mToken;
    private String mMainKey;
    private boolean mIsLogined=false;
    private int mStatus;
    private String mMessage="undefined";
    private String mId;
    public String getMessage(){
        return mMessage;
    }
    public String getId(){
        return mId;
    }
    public boolean getIsLogined(){
        return mIsLogined;
    }
    public void login(String userName,String password){
        if(mIsLogined){
            return;
        }
        try {
            mUserName=userName;
            mPassword=password;
            String passport=getLoginPassport();
            Log.d("login",passport);
            JSONObject json=ServerAccessApi.login(mUserName,passport);
            mId=json.getString("id");
            mToken=json.getString("accessToken");
            Log.d("login","Token: "+mToken);
            mIsLogined=true;
            mMessage="successful login";
        }catch (UServerAccessException e){
            e.printStackTrace();
            mIsLogined=false;
            mMessage=e.getMessage();
        }catch (Exception e){
            e.printStackTrace();
            mIsLogined=false;
            System.exit(1);
        }
    }
    private void login(){
        login(mUserName,mPassword);
    }
    private String getLoginPassport()throws UServerAccessException{
        String loginToken=ServerAccessApi.getLoginToken(mUserName);
        String aes_key=MD5Utils.MD5(getMainKey()).substring(0,16);
        String aes=AESUtils.encrypt(mPassword,aes_key);
        String md5=MD5Utils.MD5(aes);
        String ret = loginToken + md5;
        Log.d("LoginToken",loginToken);
        Log.d("AES",md5);
        return MD5Utils.MD5(ret);
    }

    private String getMainKey()throws UServerAccessException{
        if(mMainKey==null) {
            mMainKey = ServerAccessApi.getMainKey();
        }
        Log.d("MainKey",mMainKey);
        return mMainKey;
    }
    public String getPassport(){
        long timeStamp=System.currentTimeMillis()/100000;
        return MD5Utils.MD5(mUserName + mToken + timeStamp);
    }

    public UserInfo getMyInfo() throws UServerAccessException {
        return getUserBaseInfo(mId);
    }
    public UserInfo getUserBaseInfo(String id) throws UServerAccessException {
        try {
            JSONObject json = ServerAccessApi.getBasicInfo(mId, getPassport(), id);
            UserInfo userInfo=new UserInfo();
            userInfo.setAvatar(json.getString("avatar"))
                    .setBirthday(json.getString("birthday"))
                    .setPhone(json.getString("phone"))
                    .setSex(json.getInt("sex"))
                    .setRealName(json.getString("realname"))
                    .setSchool(json.getString("school"))
                    .setSchoolClass(json.getString("class"))
                    .setStudentId(json.getString("studentid"))
                    .setUserName(json.getString("username"))
                    .setId(id);
            return userInfo;
        }catch (JSONException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public String getUserName(){
        return mUserName;
    }
    UserOperatorController(){
        //单例模式
    }
}
