package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SettingUserActivity extends AppCompatActivity
implements View.OnClickListener{
    ImageButton back;
    LinearLayout phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);

        back=(ImageButton)findViewById(R.id.back);
        phone=(LinearLayout)findViewById(R.id.phone);

        phone.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(R.id.back == v.getId()){
            finish();
        }
        if(R.id.phone == v.getId()){
            Intent it = new Intent(this, SettingUserPhoneActivity.class);
            startActivity(it);
        }



    }
}
