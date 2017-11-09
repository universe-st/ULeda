package ecnu.uleda.view_controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import ecnu.uleda.R;
import ecnu.uleda.function_module.UserOperatorController;

public class WelcomeActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserOperatorController.getInstance().getIsLogined()) {
            startActivity(new Intent(getApplication(), UMainActivity.class));
            WelcomeActivity.this.finish();
        }
        setContentView(R.layout.activity_welcome);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果用户已经登陆成功，则不用展示欢迎界面
        Handler h = new Handler();
        h.postDelayed(new WaitThread(), 1500);

    }

    class WaitThread implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            WelcomeActivity.this.finish();
        }
    }
}
