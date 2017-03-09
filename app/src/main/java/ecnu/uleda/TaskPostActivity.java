package ecnu.uleda;


import android.content.Intent;
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
import com.tencent.map.geolocation.TencentLocationManager;

import java.util.ArrayList;

public class TaskPostActivity extends AppCompatActivity {

    private UserOperatorController mUserOperatorController;

    private Handler mClickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(TaskPostActivity.this, "提交成功～", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                UServerAccessException exception = (UServerAccessException) msg.obj;
                Toast.makeText(TaskPostActivity.this, "提交任务失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    final private static ArrayList<String> taskPostArray;

    static {
        taskPostArray = new ArrayList<>();
        taskPostArray.add("跑腿代步");
        taskPostArray.add("生活服务");
        taskPostArray.add("学习帮助");
        taskPostArray.add("技术难题");
        taskPostArray.add("寻物启示");
        taskPostArray.add("活动相关");
        taskPostArray.add("其他");
    }

    private EditText mEtTitle;
    private Spinner mSpinTag;
    private EditText mEtPrice;
    private EditText mEtActiveTime;
    private EditText mEtDescription;

    private Button mButtonBack;
    private Button mButtonTaskPost;
    private ArrayAdapter<String> taskPostAdapter;

    private String mId;
    private String mPpassport;
    private String mTitle;
    private String mTag = "跑腿代步";  //tag任务分类
    private String mDescription;
    private String mPrice;
    private String mPath;
    private String mActiveTime;
    private String mPosition;

    private Button buttonStart;
    private Button buttonDestination;
    private float latitude = 0;
    private float longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        init();
        SpinnerInit();
        SpinnerEvent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mEtPrice.addTextChangedListener(new MyTextWatcher());

        mButtonTaskPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getTaskPost();
                if (!judgeEditText()) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            ServerAccessApi.postTask(mId, mPpassport, mTitle, mTag, mDescription, mPrice, mPath, mActiveTime, mPosition);
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
    @Override
    public void onActivityResult(int request,int result,Intent data){
        if(data==null)return;
        if(request==100){
            buttonStart.setText(data.getStringExtra("title"));
        }else if(request==200){
            buttonDestination.setText(data.getStringExtra("title"));
            latitude=data.getFloatExtra("lat",0f);
            longitude=data.getFloatExtra("lng",0f);
        }
    }
    protected void init() {
        mButtonBack = (Button) findViewById(R.id.button_task_post_back);
        mButtonTaskPost = (Button) findViewById(R.id.button_task_post);
        mEtTitle = (EditText) findViewById(R.id.task_post_title);
        mEtPrice = (EditText) findViewById(R.id.task_post_payment);
        mEtActiveTime = (EditText) findViewById(R.id.task_post_activeTime);
        mEtDescription = (EditText) findViewById(R.id.task_post_description);


        buttonStart = (Button) findViewById(R.id.button_task_post_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent=new Intent(TaskPostActivity.this,LocationListActivity.class);
                startActivityForResult(intent,100);
            }
        });
        buttonDestination=(Button)findViewById(R.id.button_task_post_destination);
        buttonDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TaskPostActivity.this,LocationListActivity.class);
                startActivityForResult(intent,200);
            }
        });
    }


    private void SpinnerInit() {
        mSpinTag = (Spinner) findViewById(R.id.spinner_task_post);
        taskPostAdapter = new ArrayAdapter<>(this.getApplicationContext(),
                R.layout.task_post_spinner, taskPostArray);
        taskPostAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinTag.setAdapter(taskPostAdapter);
    }

    private void SpinnerEvent() {
        mSpinTag.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTag = mSpinTag.getSelectedItem().toString();
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
            String price = mEtPrice.getText().toString();
            int posDot = price.indexOf('.');
            if (posDot>0 &&price.length() - posDot - 1 > 2) {
                String string;
                string = price.substring(0, price.length() - 1);
                mEtPrice.setText(string);
                mEtPrice.setSelection(string.length());
            }
        }

        public void afterTextChanged(Editable s) {
        }
    }

    private void getTaskPost() {
        mUserOperatorController = UserOperatorController.getInstance();
        mId = mUserOperatorController.getId();
        mPpassport = mUserOperatorController.getPassport();
        mTitle = mEtTitle.getText().toString();
        mDescription = mEtDescription.getText().toString();
        mPrice = mEtPrice.getText().toString();
        mPath = buttonStart.getText()+"|"+buttonDestination.getText();
        String time=mEtActiveTime.getText().toString();
        if(time.length()==0)time="0";
        mActiveTime = String.valueOf(Integer.parseInt(time)*60);
        mPosition = latitude+","+longitude;
    }


    private boolean judgeEditText()
    {
        if(mTitle.length()==0)
        {
            Toast.makeText(TaskPostActivity.this, "标题不能为空哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(UPublicTool.byteCount(mTitle)<5)
        {
            Toast.makeText(TaskPostActivity.this, "标题不能少于5个字节哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(UPublicTool.byteCount(mTitle)>30){
            Toast.makeText(TaskPostActivity.this, "标题不能多于30个字节哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mPrice.length()==0)
        {
            Toast.makeText(TaskPostActivity.this, "价格不能为空哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(UPublicTool.byteCount(mDescription)>450){
            Toast.makeText(TaskPostActivity.this, "描述不能多于225个字哦～",Toast.LENGTH_SHORT).show();
        }
        if(Float.parseFloat(mPrice)<0.5f)
        {
            Toast.makeText(TaskPostActivity.this, "价格不能低于0.5元哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mActiveTime.length()==0)
        {
            Toast.makeText(TaskPostActivity.this, "时效不能为空哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

}


