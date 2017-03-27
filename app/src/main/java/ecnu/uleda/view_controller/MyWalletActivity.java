package ecnu.uleda.view_controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import ecnu.uleda.R;

public class MyWalletActivity extends AppCompatActivity
implements View.OnClickListener{

    ImageButton back;
    Button recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        back=(ImageButton)findViewById(R.id.back);
        recharge=(Button)findViewById(R.id.recharge);

        recharge.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(R.id.back == v.getId()){
            finish();
        }
        if(v.getId()==R.id.recharge){
            Intent it = new Intent(this, Mywallet_RechargeActivity.class);
            startActivity(it);
        }
    }
}
