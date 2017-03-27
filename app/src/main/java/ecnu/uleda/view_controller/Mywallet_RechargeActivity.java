package ecnu.uleda.view_controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import ecnu.uleda.R;

public class Mywallet_RechargeActivity extends AppCompatActivity
implements View.OnClickListener{

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet_recharge);
        back=(ImageButton)findViewById(R.id.back);

        back.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(R.id.back == v.getId()){
            finish();
        }
    }
}
