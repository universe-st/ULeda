package ecnu.uleda;

//import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class TaskPostActivity extends AppCompatActivity {

    private UserOperatorController mUserOperatorController;

    private Handler mClickHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                finish();
            }else{
                UServerAccessException exception=(UServerAccessException)msg.obj;
                Toast.makeText(TaskPostActivity.this,"提交任务失败："+exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    };

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
    private EditText mEtPrice;
    private Button mButtonBack;
    private Button mButtonTaskPost;
    private Spinner spinnerTag;
    private ArrayAdapter<String> taskPostAdapter;

    private String mId;
    private String mPpassport;
    private String mTitle;
    private String mTag;  //tag任务分类
    private String mDescription;
    private String mPrice;
    private String mPath;
    private String mActiveTime;
    private String mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        mButtonBack=(Button) findViewById(R.id.button_task_post_back);
        mButtonTaskPost=(Button)findViewById(R.id.button_task_post);
        mEtPrice=(EditText)findViewById(R.id.task_post_payment);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        SpinnerInit();

        mButtonBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        mEtPrice.addTextChangedListener(new MyTextWatcher());

        mButtonTaskPost.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                SpinnerEvent();
                getTaskPost();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            mUserOperatorController=UserOperatorController.getInstance();
                            ServerAccessApi.postTask(mId,mPpassport,mTitle,mTag,mDescription,mPrice,mPath,mActiveTime,mPosition);
                            Message message = new Message();
                            message.what = 0;
                            mClickHandler.sendMessage(message);
                        } catch (UServerAccessException e) {
                            e.printStackTrace();
                            Message message = new Message();
                            message.what = 1;
                            message.obj = e;
                            mClickHandler.sendMessage(message);
                        }
                    }
                }.start();

            }
        });
    }

    private void SpinnerInit()
    {
        spinnerTag=(Spinner) findViewById(R.id.spinner_task_post);
        taskPostAdapter=new ArrayAdapter<>(this.getApplicationContext(),
                R.layout.task_post_spinner,taskPostArray);
        taskPostAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerTag.setAdapter(taskPostAdapter);
    }

    private void SpinnerEvent()
    {
        spinnerTag.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTag=spinnerTag.getSelectedItem().toString();
                adapterView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String price=mEtPrice.getText().toString();
            int posDot=price.indexOf('.');
            if(posDot==-1)
                return;
            else if(price.length()-posDot-1>2)
            {
                String string;
                string = price.substring(0,price.length() - 1);
                mEtPrice.setText(string);
                mEtPrice.setSelection(string.length());
                //mEtPrice.setSelection(price.length());      //智障
            }
        }
        public void afterTextChanged(Editable s) {
        }
    }

    private void getTaskPost()
    {
        mTitle=((EditText) findViewById(R.id.task_post_title)).getText().toString();
        mPrice=mEtPrice.getText().toString();
        mDescription = ((EditText)findViewById(R.id.task_post_description)).getText().toString();
        mActiveTime = ((EditText)findViewById(R.id.task_post_activeTime)).getText().toString();
        mPath = ((EditText)findViewById(R.id.task_post_start)).getText().toString()+"|"+mPosition;
        mPosition=((EditText)findViewById(R.id.task_post_description)).getText().toString();
        //TODO:
        mUserOperatorController=UserOperatorController.getInstance();
        mId = mUserOperatorController.getId();
        mPpassport = mUserOperatorController.getPassport();
    }

}


