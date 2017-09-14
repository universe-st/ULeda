package ecnu.uleda.view_controller.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ecnu.uleda.R;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UserInfo;

/**
 * Created by zhaoning on 2017/8/16.
 */

public class SendMessageActivity extends AppCompatActivity {

    private UserOperatorController uoc;
    private String mFriendId ;
    private String mFriendName;
    private static final String TAG = "SendMessageActivity";
    private View view;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment_chat_fragment);

        uoc = UserOperatorController.getInstance();
        Intent intent=getIntent();
        mFriendId = intent.getStringExtra("userId");
        mFriendName = intent.getStringExtra("userName");

        Log.e(TAG, "SendMessageActivity");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_right_fragment,container,false);
        title = (TextView)view.findViewById(R.id.chat_title);
        title.setText(mFriendName);


        return view;
    }

}


