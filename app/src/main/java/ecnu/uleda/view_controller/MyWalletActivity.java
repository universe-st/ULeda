package ecnu.uleda.view_controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import ecnu.uleda.R;

public class MyWalletActivity extends AppCompatActivity
implements View.OnClickListener{

    Button recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        recharge=(Button)findViewById(R.id.recharge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        recharge.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v.getId()==R.id.recharge){
            Intent it = new Intent(this, Mywallet_RechargeActivity.class);
            startActivity(it);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
