package ecnu.uleda;

import android.content.Context;

/**
 * Created by Shensheng on 2017/2/9.
 */

public class UserConfig {
    //TODO:该类遵循单例模式。用于在各大场合获取用户的配置信息，例如声音是否开启等。
    private static UserConfig sUserConfig=null;

    public boolean soundIsOn(){
        //TODO:加入判断声音是否开启的代码
        return true;
    }

    public void setSoundOn(boolean set){
        //TODO:设置声音是否开启
    }

    public void saveConfigToFile(Context context){
        //TODO:将配置保存在文件中
    }

    public static UserConfig getInstance(Context context){
        if(sUserConfig==null){
            sUserConfig=new UserConfig(context);
        }
        return sUserConfig;
    }
    private UserConfig(Context context){

    }
}
