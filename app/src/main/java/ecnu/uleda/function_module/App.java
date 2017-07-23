package ecnu.uleda.function_module;

/**
 * Created by zhaoning on 2017/4/17.
 */

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static final String TAG = "App";
    private static Context context;

    public App() {
    }

    public void onCreate() {
        super.onCreate();
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
