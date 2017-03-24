package ecnu.uleda;

import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity
implements View.OnClickListener{
    ImageButton back;
    LinearLayout user;
    LinearLayout general;
    LinearLayout feedback;
    LinearLayout clean;
    LinearLayout about;
    Button exit;

    private UserOperatorController mUserOperatorController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (!UserOperatorController.getInstance().getIsLogined()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        back=(ImageButton)findViewById(R.id.back);
        user =(LinearLayout)findViewById(R.id.user) ;
        general=(LinearLayout)findViewById(R.id.general) ;
        feedback=(LinearLayout)findViewById(R.id.feedback) ;
        about=(LinearLayout)findViewById(R.id.about);
        exit=(Button)findViewById(R.id.exit);
        about.setOnClickListener(this);
        user.setOnClickListener(this);
        back.setOnClickListener(this);
        general.setOnClickListener(this);
        feedback.setOnClickListener(this);
        exit.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
            case R.id.user: {
                Intent it = new Intent(this, SettingUserActivity.class);
                startActivity(it);
                break;
            }
            case R.id.general: {
                Intent it = new Intent(this, SettingGeneralActivity.class);
                startActivity(it);
                break;
            }
            case R.id.feedback: {
                Intent it = new Intent(this, SettingFeedbackActivity.class);
                startActivity(it);
                break;
            }
            case R.id.about: {
                Intent it = new Intent(this, SettingAboutActivity.class);
                startActivity(it);
                break;
            }
            case R.id.exit: {
                mUserOperatorController = UserOperatorController.getInstance();
                mUserOperatorController.setIsLogined(false);
//                Intent intent = new Intent(this,UMainActivity.class);
//                intent.putExtra(UMainActivity.TAG_EXIT, true);
//                startActivity(intent);
                
                Toast.makeText(SettingActivity.this, "成功退出登录", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(this, LoginActivity.class);
                startActivity(it);

            }

        }
    }
}
