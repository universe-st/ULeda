package ecnu.uleda.view_controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.model.UTask;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.function_module.ServerAccessApi;

public class TaskEditActivity extends AppCompatActivity {

    private IntentFilter mIntentFilter;
    private LocalReceiver mLocalReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    private Handler mClickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //Intent intent=new Intent();
                //intent.putExtra("Task", );
                //TaskEditActivity.this.setResult(1,intent);

                Toast.makeText(TaskEditActivity.this, "编辑任务成功～", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                UServerAccessException exception = (UServerAccessException) msg.obj;
                Toast.makeText(TaskEditActivity.this, "编辑任务失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Handler mClickHandlerDelete = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Intent intent=new Intent();
                //intent.putExtra("Task", );
                TaskEditActivity.this.setResult(2,intent);
                Toast.makeText(TaskEditActivity.this, "删除成功～", Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent("com.example.broadcasttest.Delete_BROADCAST");
                mLocalBroadcastManager.sendBroadcast(intent1);
                finish();
            } else {
                UServerAccessException exception = (UServerAccessException) msg.obj;
                Intent intent=new Intent();
                intent.putExtra("Task", exception);
                Toast.makeText(TaskEditActivity.this, "删除任务失败：" + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
        taskPostArray.add("运动锻炼");
        taskPostArray.add("项目招人");
        taskPostArray.add("招聘实习");
        taskPostArray.add("其他");
    }

    private UTask mTask;

    private EditText mEtTitle;
    private Spinner mSpinTag;
    private EditText mEtPrice;
    private EditText mEtActiveTime;
    private EditText mEtDescription;

    private Button mButtonTaskEdit;
    private Button mBUttonTaskDelete;
    private ArrayAdapter<String> taskPostAdapter;

    private String mId;
    private String mPassport;
    private String mPostId;
    private String mTitle;
    private String mTag = "跑腿代步";  //tag任务分类
    private String mDescription;
    private String mPrice;
    private String mPath;
    private String mActiveTime;
    private String mPosition;
    private String mStart;
    private String mDestination;

    private Button buttonStart;
    private Button buttonDestination;
    private Button buttonDeleteStart;
    private Button buttonDeleteDestination;
    private float latitude = 0;
    private float longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction("com.example.broadcasttest.Delete_BROADCAST");
        mLocalReceiver=new LocalReceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver,mIntentFilter);

        mTask=(UTask)getIntent().getSerializableExtra("Task");
        final UserOperatorController uoc=UserOperatorController.getInstance();
        if(!uoc.getIsLogined())
            return;

        init();
        SpinnerInit();
        SpinnerEvent();

        mEtPrice.addTextChangedListener(new TaskEditActivity.MyTextWatcher());
        mButtonTaskEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getTaskEdit();
                if (!judgeEditText()) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            ServerAccessApi.editTask(mId, mPassport, mPostId,mTitle, mTag, mDescription, mPrice, mPath, mActiveTime, mPosition);
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

        mBUttonTaskDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                final UserOperatorController uoc=UserOperatorController.getInstance();
                mId = uoc.getId();
                mPassport = uoc.getPassport();
                mPostId=mTask.getPostID();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            ServerAccessApi.cancelTask(mId,mPassport,mPostId);
                            Message message = new Message();
                            message.what = 0;
                            mClickHandlerDelete.sendMessage(message);
                        } catch (UServerAccessException e) {
                            e.printStackTrace();
                            Message message = new Message();
                            message.what = 1;
                            message.obj = e;
                            mClickHandlerDelete.sendMessage(message);
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

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        onBackPressed();
        return true;
    }

    protected void init() {
        final UserOperatorController uoc=UserOperatorController.getInstance();
        mId = uoc.getId();
        mPassport = uoc.getPassport();
        mPostId=mTask.getPostID();

        mButtonTaskEdit = (Button) findViewById(R.id.button_task_post_edit);
        mBUttonTaskDelete = (Button) findViewById(R.id.button_task_delete);

        mEtTitle = (EditText) findViewById(R.id.task_edit_title);
        mEtPrice = (EditText) findViewById(R.id.task_edit_payment);
        mEtActiveTime = (EditText) findViewById(R.id.task_edit_activeTime);
        mEtDescription = (EditText) findViewById(R.id.task_edit_description);

        mEtTitle.setText(mTask.getTitle());
        mEtActiveTime.setText(String.valueOf(mTask.getActiveTime()/60));
        mEtPrice.setText(String.valueOf(mTask.getPrice()));
        mEtDescription.setText(mTask.getDescription());


        String[] pos={mTask.getFromWhere(),mTask.getToWhere()};
        buttonStart = (Button) findViewById(R.id.button_task_edit_start);
        buttonStart.setText(pos[0].length()==0?"选择地址":pos[0]);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent=new Intent(TaskEditActivity.this,LocationListActivity.class);
                startActivityForResult(intent,100);
            }
        });
        buttonDestination=(Button)findViewById(R.id.button_task_edit_destination);
        buttonDestination.setText(pos[1].length()==0?"选择地址":pos[1]);
        buttonDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TaskEditActivity.this,LocationListActivity.class);
                startActivityForResult(intent,200);
            }
        });
        buttonDeleteStart=(Button)findViewById(R.id.button_delete_start_edit);
        buttonDeleteStart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                buttonStart.setText("选择地址");
            }
        });
        buttonDeleteDestination=(Button)findViewById(R.id.button_delete_destination_edit);
        buttonDeleteDestination.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                buttonDestination.setText("选择地址");
            }
        });
        latitude=(float)mTask.getPosition().getLatitude();
        longitude=(float)mTask.getPosition().getLongitude();

        //mTask.setActiveTime(Integer.valueOf(mActiveTime));
        //mTask.setDescription(mDescription);
        //mTask.setPath(mPath);
        //////////////////////

    }



    protected void onDestroy() {
            super.onDestroy();
            mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
        }


    private void SpinnerInit() {
        mSpinTag = (Spinner) findViewById(R.id.spinner_task_edit);
        //mSpinTag.setTag();
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

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Toast.makeText(context,"已收到广播",Toast.LENGTH_SHORT).show();
        }
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

    private void getTaskEdit() {
        //mUserOperatorController = UserOperatorController.getInstance();
        //mId = mUserOperatorController.getId();
        //mPpassport = mUserOperatorController.getPassport();
        final UserOperatorController uoc=UserOperatorController.getInstance();
        mId = uoc.getId();
        mPassport = uoc.getPassport();
        mPostId=mTask.getPostID();

        mTitle = mEtTitle.getText().toString();
        mDescription = mEtDescription.getText().toString();
        mPrice = mEtPrice.getText().toString();
        mStart = buttonStart.getText().toString();
        mDestination = buttonDestination.getText().toString();
        mPath = mStart+"|"+mDestination;
        String time=mEtActiveTime.getText().toString();
        if(time.length()==0)time="0";
        mActiveTime = String.valueOf(Integer.parseInt(time)*60);
        mPosition = latitude+","+longitude;
    }


    private boolean judgeEditText()
    {
        if(mTitle.length()==0)
        {
            Toast.makeText(TaskEditActivity.this, "标题不能为空哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(UPublicTool.byteCount(mTitle)<5)
        {
            Toast.makeText(TaskEditActivity.this, "标题不能少于5个字节哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(UPublicTool.byteCount(mTitle)>30){
            Toast.makeText(TaskEditActivity.this, "标题不能多于30个字节哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mPrice.length()==0)
        {
            Toast.makeText(TaskEditActivity.this, "价格不能为空哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(UPublicTool.byteCount(mDescription)>225){
            Toast.makeText(TaskEditActivity.this, "描述不能多于225个字节哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Float.parseFloat(mPrice)<0.5f)
        {
            Toast.makeText(TaskEditActivity.this, "价格不能低于0.5元哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mActiveTime.length()==0)
        {
            Toast.makeText(TaskEditActivity.this, "时效不能为空哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!mStart.equals("选择地址") && mDestination.equals("选择地址") )
        {
            Toast.makeText(TaskEditActivity.this,"请选择目的地哦～",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mStart.equals("选择地址") && !mDestination.equals("选择地址"))
        {
            mPath = ""+"|"+mDestination;
            return true;
        }
        if(mStart.equals("选择地址") && mDestination.equals("选择地址"))
        {
            mPosition="31.2267104411"+","+"121.4044582732";
            mPath = "|";
            return true;
        }

        return true;

    }

}
