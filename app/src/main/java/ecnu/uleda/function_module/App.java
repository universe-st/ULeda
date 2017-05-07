package ecnu.uleda.function_module;

/**
 * Created by zhaoning on 2017/4/17.
 */

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import io.rong.imkit.RongIM;

import static java.security.AccessController.getContext;

public class App extends Application {
    public App() {
    }

    public void onCreate() {
        super.onCreate();
        if (this.getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);

            }
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
}
