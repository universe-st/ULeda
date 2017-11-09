package ecnu.uleda.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zhaoning on 2017/4/15.
 */


public class SPUtil {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    public static void init(Context context){
        if(preferences == null){
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            editor = preferences.edit();
        }
    }

    public static void saveUserId(String key,String userId){
        editor.putString(key,userId).apply();
    }
    public static String getUserId(String key){
        String userId = preferences.getString(key, "");
        return userId;
    }
}
