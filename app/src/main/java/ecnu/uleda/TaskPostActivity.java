package ecnu.uleda;

//import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText editText;
    private Button buttonBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        buttonBack=(Button) findViewById(R.id.button_task_post_back);
        editText=(EditText)findViewById(R.id.task_post_payment);

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

            editText.addTextChangedListener(new MyTextWatcher());

    }
        //taskPostSpinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.DST);

      /*  private void initListener() {
            editText.addTextChangedListener(new MyTextWatcher());
        }
*/
        private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String temp=editText.getText().toString();
                int posDot=temp.indexOf('.');
                if(posDot==0)
                    return;
                else if(temp.length()-posDot-1>2)
                {
                    String string;
                    //editText.delete(posDot+3,posDot+4);
                    string = temp.substring(0,temp.length() - 1);
                    editText.setText(string);
                    editText.setSelection(editText.length());
                }

            }
        public void afterTextChanged(Editable s) {
        }
    }

}


