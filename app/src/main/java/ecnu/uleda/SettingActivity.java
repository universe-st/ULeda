package ecnu.uleda;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SettingActivity extends AppCompatActivity
implements View.OnClickListener{
    ImageButton back;
    LinearLayout user;
    LinearLayout general;
    LinearLayout feedback;
    LinearLayout clean;
    LinearLayout about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        back=(ImageButton)findViewById(R.id.back);
        user =(LinearLayout)findViewById(R.id.user) ;
        general=(LinearLayout)findViewById(R.id.general) ;
        feedback=(LinearLayout)findViewById(R.id.feedback) ;
        about=(LinearLayout)findViewById(R.id.about);
        about.setOnClickListener(this);
        user.setOnClickListener(this);
        back.setOnClickListener(this);
        general.setOnClickListener(this);
        feedback.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(R.id.back == v.getId()){
          finish();
        }
        if(v.getId()==R.id.user){
            Intent it = new Intent(this, SettingUserActivity.class);
            startActivity(it);
        }
        if(v.getId()==R.id.general){
            Intent it = new Intent(this, SettingGeneralActivity.class);
            startActivity(it);
        }
        if(v.getId()==R.id.feedback){
            Intent it = new Intent(this, SettingFeedbackActivity.class);
            startActivity(it);
        }
        if(v.getId()==R.id.about){
            Intent it = new Intent(this, SettingAboutActivity.class);
            startActivity(it);
        }

    }
}
