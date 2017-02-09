package ecnu.uleda;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class UserLoginController {

    public static class NoLoginControllerInstanceException extends RuntimeException{
        public NoLoginControllerInstanceException(){
            super("There is no UserLoginController instance. \nPlease call method \"getInstance(String,String) first.\"");
        }
    }
    private static UserLoginController sInstance;


    public static UserLoginController getInstance(String username,String password){
        sInstance=new UserLoginController(username,password);
        return sInstance;
    }
    public static UserLoginController getInstance(){
        if(sInstance==null){
            throw new NoLoginControllerInstanceException();
        }
        return sInstance;
    }

    public static final int NOT_LOGIN=0;
    public static final int SUCCESS_LOGIN=1;
    public static final int WRONG_PASSWORD_OR_USERNAME=2;
    public static final int UNKNOWN_WRONG=3;
    public static final int INTERNET_ERROR=4;

    private static final String sUrl="http://cftuan.top/uleda/api_user.php";
    private volatile String mKey=null;
    private volatile String mToken=null;
    private String mUsername;
    private String mPassword;
    private String mUserId;
    private int mSuccessNumber=NOT_LOGIN;
    public UserInfo getUserInfo(){
        try {
            if (mSuccessNumber != SUCCESS_LOGIN) return null;
            String p ="action=gi&at="+URLEncoder.encode(SecretTool.encode(mKey, mToken),"UTF-8")+"&id="+mUserId;
        }catch (Exception e){
            Log.d("Debug","getUserInfo "+e.toString());
        }
        return null;
    }
    public int getSuccessNumber() {
        return mSuccessNumber;
    }
    private UserLoginController(String username,String password){
        mUsername=username;
        mPassword=password;
    }

    public void login(){
        getKey();
        if(mSuccessNumber!=NOT_LOGIN)return;
        try {
            Log.d("Debug","What");
            JSONObject json = new JSONObject();
            json.put("username", mUsername);
            json.put("password",mPassword);
            String p= URLEncoder.encode(SecretTool.encode(mKey,json.toString()),"UTF-8");
            String pr="action=lg&data="+p;
            Log.d("Debug",pr);
            URL url=new URL(sUrl);
            HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.connect();
            Log.d("Debug","Connect");
            DataOutputStream out=new DataOutputStream(urlConn.getOutputStream());
            out.writeBytes(pr);
            out.flush();
            out.close();
            Log.d("Debug","Output");
            InputStreamReader in=new InputStreamReader(urlConn.getInputStream());
            BufferedReader reader=new BufferedReader(in);
            StringBuffer result=new StringBuffer();
            String readLine=null;
            while((readLine=reader.readLine())!=null){
                result.append(readLine);
            }
            in.close();
            urlConn.disconnect();
            if(result.toString().contains("Error!")){
                if(result.toString().contains("2002") || result.toString().contains("2003") || result.toString().contains("2004")){
                    mSuccessNumber=WRONG_PASSWORD_OR_USERNAME;
                }else{
                    mSuccessNumber=UNKNOWN_WRONG;
                }
                return;
            }
            String tokenJson=SecretTool.decodeValue(mKey,result.toString());
            JSONObject a=new JSONObject(tokenJson);
            mToken=a.getString("accesstoken");
            mUserId=a.getString("id");
            if(mToken!=null && mUserId!=null){
                mSuccessNumber=SUCCESS_LOGIN;
            }
            Log.d("NetWork",mToken);
        }catch (Exception e){
            if(e.getClass()==java.net.UnknownHostException.class){
                mSuccessNumber=INTERNET_ERROR;
            }
            mSuccessNumber=UNKNOWN_WRONG;
            Log.d("Debug",e.toString());
        }
    }
    private void getKey(){
        if(mKey==null) {
            URL url=null;
            try {
                url = new URL(sUrl+"?action=gk");
                HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
                InputStreamReader in=new InputStreamReader(urlConn.getInputStream());
                BufferedReader reader=new BufferedReader(in);
                StringBuffer result=new StringBuffer();
                String readLine=null;
                while((readLine=reader.readLine())!=null){
                    result.append(readLine);
                }
                in.close();
                urlConn.disconnect();
                mKey=result.toString();
                mKey=(new String(Base64.decode(mKey,Base64.DEFAULT))).substring(8,16);
                Log.d("Debug",mKey);
            }catch (Exception e){
                if(e.getClass()==java.net.UnknownHostException.class) {
                    mSuccessNumber = INTERNET_ERROR;
                }else{
                    mSuccessNumber = UNKNOWN_WRONG;
                }
            }
        }
    }
}
