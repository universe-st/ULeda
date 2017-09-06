package ecnu.uleda.view_controller.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ecnu.uleda.R;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UserInfo;

/**
 * Created by zhaoning on 2017/8/16.
 */

public class SendMessageActivity extends AppCompatActivity {

    private UserOperatorController uoc;
    private String mUFriendId ;
    private String tag = "SendMessageActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        uoc = UserOperatorController.getInstance();
        Intent intent=getIntent();
        if(intent.getBooleanExtra("isGet",false)){
            mUFriendId = intent.getStringExtra("userId");
        }


        Log.e(tag, "SendMessageActivity");

    }

}


