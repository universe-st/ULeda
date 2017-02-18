package ecnu.uleda;

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class TaskPostActivity extends AppCompatActivity {

    final private static ArrayList<String> taskPostArray;
    static {
        taskPostArray=new ArrayList<>();
        taskPostArray.add("全部");
        taskPostArray.add("跑腿代步");
        taskPostArray.add("生活服务");
        taskPostArray.add("学习帮助");
        taskPostArray.add("技术难题");
        taskPostArray.add("寻物启示");
        taskPostArray.add("活动相关");
        taskPostArray.add("其他");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        Button buttonBack=(Button) findViewById(R.id.button_task_post_back);
        buttonBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Spinner taskPostSpinner=(Spinner) findViewById(R.id.spinner_task_post);
        ArrayAdapter<String> taskPostAdapter=new ArrayAdapter<>(this.getApplicationContext(),
                R.layout.task_post_spinner,taskPostArray);
        taskPostAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        taskPostSpinner.setAdapter(taskPostAdapter);
        //taskPostSpinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.DST);

    }
}
