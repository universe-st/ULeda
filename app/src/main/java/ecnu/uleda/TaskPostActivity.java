package ecnu.uleda;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class TaskPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        //Button buttonBack=(Button) findViewById(R.id.button_task_post_back);
        //buttonBack.setOnClickListener(new View.OnClickListener(){
        //    public void onClick(View v){
          //      finish();
            //}
        //});

        //if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
          //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //}
    }
}
