package ecnu.uleda.view_controller;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ecnu.uleda.R;
import ecnu.uleda.tool.SPUtil;

public class ConversationActivity extends FragmentActivity {

    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment_conversation);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        //单聊是targetId就是userId
        String targetId = getIntent().getData().getQueryParameter("targetId");
        //需要设置用户者信息，才能获取到title
        String title = getIntent().getData().getQueryParameter("title");
        tvTitle.setText(title);
        String userId = SPUtil.getUserId("userId");
        if(targetId.equals("10086")){
            tvTitle.setText("孙悟天");
        }else{
            tvTitle.setText("特兰克斯");
        }
    }
}
