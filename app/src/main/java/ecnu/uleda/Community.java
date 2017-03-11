package ecnu.uleda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class Community extends AppCompatActivity {
    private List<Msg> messageList = new ArrayList<Msg>();
    private Button button_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        button_back = (Button)findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMessage();
        Msg_Adapter msg_adpter = new Msg_Adapter(Community.this, R.layout.message_item,messageList);
        ListView listView = (ListView) findViewById(R.id.message_list_view);
        listView.setAdapter(msg_adpter);
    }
    private void initMessage()
    {
        Msg m1 = new Msg("欢迎使用U乐达，您在这里可以发布任务，接手任务并获得一定的酬劳，感谢您的使用");
        messageList.add(m1);
        Msg m2 = new Msg("您所接手的任务将在2016年12月15日15：00逾期，请尽快完成");
        messageList.add(m2);
    }
}
