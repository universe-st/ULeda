package ecnu.uleda.function_module;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.OutputStreamWriter;


/**
 * Created by Shensheng on 2017/2/9.
 * 用户配置信息类
 */

public class UserConfig {
    //TODO:该类遵循单例模式。用于在各大场合获取用户的配置信息，例如声音是否开启等。
    private static UserConfig sUserConfig = null;
    private boolean mSoundIsOn;
    private String mUsername;
    private String mUserPassword;
    private static final String USER_SETTINGS = "ueserSettings.json";
    private Context context;

    private UserConfig(Context context){
        this.context = context.getApplicationContext();
        String jsonData = Read();
        try{
            JSONObject j = new JSONObject(jsonData);
            mSoundIsOn = j.has("soundIsOn") && j.getBoolean("soundIsOn");
            if(j.has("username") && j.has("userpassword")) {
                mUsername = j.getString("username");
                mUserPassword = j.getString("userpassword");
            }else{
                mUsername="";
                mUserPassword="";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getSavedPassword()
    {
        //TODO:读取用户密码
        return mUserPassword;
    }
    public String getSavedUsername()
    {
       return mUsername;
    }
    public void setSavedUsernamePassword(String username,String password)
    {
        //TODO:保存用户名和密码
        mUsername = username;
        mUserPassword = password;
        saveConfigToFile();
    }

    public boolean soundIsOn(){
        //TODO:加入判断声音是否开启的代码

         return mSoundIsOn;
    }

    public void setSoundOn(boolean set){
        //TODO:设置声音是否开启
        mSoundIsOn = set;
        saveConfigToFile();
    }
    private String Read()
    {
        FileInputStream in;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = context.openFileInput(USER_SETTINGS);
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null)
            {
                content.append(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new JSONObject().toString();
        }
        finally {
            if(reader != null)
            {
                try{
                    reader.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
    private JSONObject toJSON() throws JSONException
    {
        JSONObject j = new JSONObject();
        j.put("soundIsOn",mSoundIsOn);
        j.put("username",mUsername);
        j.put("userpassword",mUserPassword);
        return j;
    }
    private void saveConfigToFile(){
        //TODO:将配置保存在文件中
        BufferedWriter w = null;
        try {
            FileOutputStream output = context.openFileOutput(USER_SETTINGS, Context.MODE_PRIVATE);
            w = new BufferedWriter(new OutputStreamWriter(output));
            JSONObject j = toJSON();
            w.write(j.toString());
        }
        catch (Exception e)
        {
        e.printStackTrace();
        }
        finally {
            if(w != null)
            {
                try{
                    w.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    public static UserConfig getInstance(Context context){
        if(sUserConfig == null){
            sUserConfig  = new UserConfig(context);
        }
        return sUserConfig;
    }

}
