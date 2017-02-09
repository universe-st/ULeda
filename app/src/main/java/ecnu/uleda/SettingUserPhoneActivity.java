package ecnu.uleda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SettingUserPhoneActivity extends AppCompatActivity
implements View.OnClickListener{

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user_phone);

        back=(ImageButton)findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(R.id.back == v.getId()){
            finish();
        }
    }
}
