package ecnu.uleda.function_module;

/**
 * Created by zhaoning on 2017/4/17.
 */

import android.app.Application;
import io.rong.imkit.RongIM;

public class App extends Application {
    public App() {
    }

    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
    }
}
