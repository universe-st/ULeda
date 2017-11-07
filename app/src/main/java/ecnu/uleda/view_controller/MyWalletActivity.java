package ecnu.uleda.view_controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ecnu.uleda.R;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UserInfo;

public class MyWalletActivity extends AppCompatActivity implements View.OnClickListener{

    private Button recharge;
    private UserOperatorController mUOC;
    private String money;
    private TextView mTextViewMoney;
    private static final String TAG = "MyWalletActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        recharge=(Button)findViewById(R.id.recharge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextViewMoney = (TextView) findViewById(R.id.textView34);
        setTitle("");

        mUOC = UserOperatorController.getInstance();
        new Thread(new MyRunnable()).start();

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

    class MyRunnable implements Runnable{

        @Override
        public void run() {
            String mId = mUOC.getId();

            try {
                JSONObject json = ServerAccessApi.getBasicInfo(mId, mUOC.getPassport(),mId);
                UserInfo userInfo = new UserInfo();
                money = json.getString("balance");
                MyWalletActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=money)
                        {
                            mTextViewMoney.setText(money);
                        }
                    }
                });
                Log.e(TAG, "money: "+money);
            } catch (UServerAccessException e){
                e.printStackTrace();
                System.exit(1);
            }
            catch (JSONException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
