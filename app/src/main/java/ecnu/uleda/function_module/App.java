package ecnu.uleda.function_module;

/**
 * Created by zhaoning on 2017/4/17.
 */

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;


import com.mob.MobApplication;
import com.mob.MobSDK;

import cn.smssdk.SMSSDK;

public class App extends Application {
    private static final String TAG = "App";
    private static Context context;
    /*
    * Something about Mob.
    * */
    public static final String MOB_APP_KEY="1f19cb4168a4e";
    public static final String MOB_APP_SECRET="ad4ea8c6dfec607ffbaffec324b75131";

    public App() {
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        MobSDK.init(context,MOB_APP_KEY,MOB_APP_SECRET);

    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static Context getContext() {
        return context;
    }
}
