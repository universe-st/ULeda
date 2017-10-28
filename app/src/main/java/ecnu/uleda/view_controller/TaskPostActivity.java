package ecnu.uleda.view_controller;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;
import ecnu.uleda.exception.UServerAccessException;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.view_controller.task.adapter.ImageChooseAdapter;
import ecnu.uleda.view_controller.task.fragment.TaskMissionFragment;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;


public class TaskPostActivity extends AppCompatActivity {

    private static final String EXTRA_POST_TYPE = "extra_post_type";
    private static final int REQUEST_IMAGE = 0;
    public static final int TYPE_TASK = 1;
    public static final int TYPE_PROJECT = 2;
    public static final int TYPE_ACTIVITY = 3;

    private UserOperatorController mUserOperatorController;

    private Handler mClickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(TaskPostActivity.this, "提交成功～", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(TaskMissionFragment.ACTION_REFRESH));
                finish();
            } else {
                String exception = (String) msg.obj;
                Toast.makeText(TaskPostActivity.this, "提交任务失败：" + exception, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ArrayList<String> taskPostArray;

    private TextView mButtonBack;
    private Button mButtonTaskPost;
    private EditText mEtTitle;
    private EditText mEtPrice;
    private EditText mEtActiveTime;
    private TextView mTvMainTitle;
    private TextView mTvMajorName;
    private EditText mEtProDescription;
    private TextView mTvLocationName;
    private TextView mTvDetailName;
    private EditText mEtDescription;
    private TextView mTvTitleName;
    private TextView mTvActivityTime;
    private TextView mTvCategory;
    private TextView mTvLocation;
    private EditText mEtPeopleCount;

    private LinearLayout mLlTitle;
    private LinearLayout mLlMajor;
    private LinearLayout mLlActivityTime;
    private LinearLayout mLlLocation;
    private LinearLayout mLlCategory;
    private LinearLayout mLlFee;
    private LinearLayout mLlActiveTime;
    private LinearLayout mLlStart;
    private LinearLayout mLlDestination;
    private LinearLayout mLlDetails;
    private LinearLayout mLlAddPhoto;
    private LinearLayout mLlProDescription;
    private LinearLayout mLlPeopleCount;

    private GridView mImgChooserGrid;

    private String mId;
    private String mPpassport;
    private String mTitle;
    private String mTag;  //tag任务分类
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

    // 活动相关
    private Calendar mActivityTime;
    private List<String> mImagePaths = new ArrayList<>();
    private ImageChooseAdapter mImgAdapter;

    private ExecutorService mThreadPool;

    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        init();

        Intent intent=getIntent();
        mType=intent.getIntExtra(EXTRA_POST_TYPE,1);
        switch (mType){
            case TaskPostActivity.TYPE_TASK:{
                mTvMainTitle.setText("发布任务");
                mLlProDescription.setVisibility(View.GONE);
                mLlMajor.setVisibility(View.GONE);
                mLlActivityTime.setVisibility(View.GONE);
                mLlLocation.setVisibility(View.GONE);
                mLlCategory.setVisibility(View.VISIBLE);
                break;
            }
            case TaskPostActivity.TYPE_PROJECT:{
                mTvTitleName.setText("主题");
                mTvMainTitle.setText("项目招人");
                mTvLocationName.setText("实验室地点");
                mTvDetailName.setText("招人要求");
                mEtDescription.setHint("您对招募同学能力、人数等方面的要求，限225字节内。");
                mLlFee.setVisibility(View.GONE);
                mLlStart.setVisibility(View.GONE);
                mLlDestination.setVisibility(View.GONE);
                mLlActivityTime.setVisibility(View.GONE);
                mLlActiveTime.setVisibility(View.GONE);
                mLlCategory.setVisibility(View.GONE);
                break;
            }
            case TaskPostActivity.TYPE_ACTIVITY:{
                mLlProDescription.setVisibility(View.GONE);
                mTvTitleName.setText("活动名称");
                mEtTitle.setHint("5~30字");
                mTvMainTitle.setText("活动宣传");
                mLlMajor.setVisibility(View.GONE);
                mLlFee.setVisibility(View.GONE);
                mLlActiveTime.setVisibility(View.GONE);
                mLlStart.setVisibility(View.GONE);
                mLlDestination.setVisibility(View.GONE);
                mEtDescription.setHint("简述活动具体内容，限450字内。");
                mLlPeopleCount.setVisibility(View.VISIBLE);
                break;

            }
        }
        if (mType != TYPE_PROJECT) {
            initCategory(mType);
            spinnerInit();
            timePickerInit();
        }
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mEtPrice.addTextChangedListener(new MyTextWatcher());

        mButtonTaskPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mType == TYPE_TASK) {
                    getTaskPost();
                    if (!judgeEditText()) {
                        return;
                    }
                    showAlertDialog();
                } else if (mType == TYPE_ACTIVITY) {
                    releaseActivity();
                }
            }
        });
        initGridView();
    }

    private void releaseActivity() {
        final String title = mEtTitle.getText().toString();
        final String category = mTvCategory.getText().toString();
        final String detail = mEtDescription.getText().toString();
        final String maxPeopleCountStr = mEtPeopleCount.getText().toString();
        final String location = mTvLocation.getText().toString();
        if (checkActivityInfoComplete(title, category, detail, maxPeopleCountStr, location, mImagePaths)) {
            mUserOperatorController = UserOperatorController.getInstance();
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                    e.onNext(ServerAccessApi.postActivity(mUserOperatorController.getId(),
                            mUserOperatorController.getPassport(),
                            title,
                            mTag,
                            detail,
                            String.valueOf(mActivityTime.getTimeInMillis() - System.currentTimeMillis()),
                            latitude,
                            longitude,
                            maxPeopleCountStr,
                            location,
                            mImagePaths));
                    e.onComplete();
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer response) throws Exception {
                            if (response == 200) {
                                Toast.makeText(TaskPostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(TaskPostActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (throwable instanceof IOException) {
                                Toast.makeText(TaskPostActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThreadPool = Executors.newCachedThreadPool();
    }

    private boolean checkActivityInfoComplete(String title, String category,
                                              String detail, String maxPeopleCountStr, String location, List<String> imagePaths) {
        boolean result = true;


        if (UPublicTool.isTextLegal(title, 5, 30)) {
            Toast.makeText(this, "标题无效", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (UPublicTool.isTextEmpty(category)) {
            Toast.makeText(this, "分类为空", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (mActivityTime == null) {
            Toast.makeText(this, "时间为空", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (latitude == 0 || longitude == 0) {
            Toast.makeText(this, "地点为空", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (UPublicTool.isTextLegal(detail, 0, 450)) {
            Toast.makeText(this, "详情为空", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (UPublicTool.isTextEmpty(maxPeopleCountStr) || Integer.parseInt(maxPeopleCountStr) <= 0) {
            Toast.makeText(this, "人数上限需为大于0的数字", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (UPublicTool.isTextEmpty(location)) {
            Toast.makeText(this, "地点为空", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (imagePaths == null || imagePaths.size() <= 0) {
            Toast.makeText(this, "请选择至少一张图片", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    private void initGridView() {
        mImgChooserGrid.setAdapter(mImgAdapter = new ImageChooseAdapter(this, mImagePaths, 60, 7));
        mImgChooserGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mImgAdapter.getCount() - 1 && mImagePaths.size() < 7) {
                    selectImage();
                } else {
                    removeImage(position);
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void removeImage(final int pos) {
        new AlertDialog.Builder(this)
                .setMessage("确认删除")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mImagePaths.remove(pos);
                        mImgAdapter.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
    }


    public void showAlertDialog()
    {
        new AlertDialog.Builder(TaskPostActivity.this)
                .setIcon(R.drawable.main_icon)
                .setTitle("确认发布")
                .setMessage("本次发布任务将花费"+mEtPrice.getText().toString()+"元")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mThreadPool.submit(new Runnable() {
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
                            });
                        } catch (RejectedExecutionException e){}
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }
    @Override
    public void onActivityResult(int request,int result,Intent data){
        if(data==null)return;
        switch (request) {
            case 100:
                buttonStart.setText(data.getStringExtra("title"));
                break;
            case 200:
                if (mType == TYPE_TASK) {
                    buttonDestination.setText(data.getStringExtra("title"));
                } else {
                    mTvLocation.setText(data.getStringExtra("title"));
                }
                latitude=data.getFloatExtra("lat",0f);
                longitude=data.getFloatExtra("lng",0f);
                break;
            case REQUEST_IMAGE:
                onImageChosen(data);
                break;
        }
    }

    private void onImageChosen(Intent data) {
        Uri uri = data.getData();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            mImagePaths.add(path);
            mImgAdapter.notifyDataSetChanged();
            mImgChooserGrid.requestLayout();
            if (mImagePaths.size() == 7) {
                Toast.makeText(this, "已到达最大图片数", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    protected void init() {
        mButtonBack = (TextView) findViewById(R.id.button_task_post_back);
        mButtonTaskPost = (Button) findViewById(R.id.button_task_post);
        mEtTitle = (EditText) findViewById(R.id.task_post_title);
        mEtPrice = (EditText) findViewById(R.id.task_post_payment);
        mEtActiveTime = (EditText) findViewById(R.id.task_post_activeTime);
        mTvMainTitle=(TextView) findViewById(R.id.task_post_main_title);
        mTvMajorName=(TextView)findViewById(R.id.task_post_major_name);
        mEtProDescription=(EditText) findViewById(R.id.task_post_project_description);
        mTvLocationName=(TextView)findViewById(R.id.task_post_activity_location_name);
        mTvDetailName=(TextView)findViewById(R.id.task_post_details_name);
        mEtDescription=(EditText)findViewById(R.id.task_post_description);
        mTvTitleName=(TextView)findViewById(R.id.task_post_title_name);
        mTvActivityTime = (TextView) findViewById(R.id.task_post_activity_time);
        mTvCategory = (TextView) findViewById(R.id.category);
        mImgChooserGrid = (GridView) findViewById(R.id.img_choose_grid);
        mTvLocation = (TextView) findViewById(R.id.task_post_location);
        mEtPeopleCount = (EditText) findViewById(R.id.max_people_count);


        mLlTitle=(LinearLayout)findViewById(R.id.task_post_title_option);
        mLlMajor=(LinearLayout)findViewById(R.id.task_post_major_option);
        mLlActivityTime=(LinearLayout)findViewById(R.id.task_post_activity_time_option);
        mLlLocation=(LinearLayout)findViewById(R.id.task_post_activity_location_option);
        mLlCategory=(LinearLayout)findViewById(R.id.task_post_category_option);
        mLlFee=(LinearLayout)findViewById(R.id.task_post_fee_option);
        mLlActiveTime=(LinearLayout)findViewById(R.id.task_post_active_time_option);
        mLlStart=(LinearLayout)findViewById(R.id.task_post_start_option);
        mLlDestination=(LinearLayout)findViewById(R.id.task_post_destination_option);
        mLlDetails=(LinearLayout)findViewById(R.id.task_post_details_option);
        mLlAddPhoto=(LinearLayout)findViewById(R.id.task_post_add_photo_option);
        mLlProDescription=(LinearLayout)findViewById( R.id.task_post_project_details_option);
        mLlPeopleCount = (LinearLayout) findViewById(R.id.task_post_people_count);

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
                chooseLocation();
            }
        });
        buttonDeleteStart=(Button)findViewById(R.id.button_delete_start);
        buttonDeleteStart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                buttonStart.setText("选择地址");
            }
        });
        buttonDeleteDestination=(Button)findViewById(R.id.button_delete_destination);
        buttonDeleteDestination.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                buttonDestination.setText("选择地址");
            }
        });
        mTvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLocation();
            }
        });
    }

    private void chooseLocation() {
        Intent intent=new Intent(TaskPostActivity.this,LocationListActivity.class);
        startActivityForResult(intent,200);
    }

    private void initCategory(int type) {
        taskPostArray = new ArrayList<>();
        if (type == TYPE_TASK) {
            taskPostArray.add("跑腿代步");
            taskPostArray.add("生活服务");
            taskPostArray.add("学习帮助");
            taskPostArray.add("技术难题");
            taskPostArray.add("寻物启示");
            taskPostArray.add("运动锻炼");
            taskPostArray.add("其他");
        } else {
            taskPostArray.add("运动");
            taskPostArray.add("社团");
            taskPostArray.add("公益");
        }
    }

    private void spinnerInit() {
        final OptionsPickerView catOptions = new  OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int i, int i1, int i2, View view) {
                mTag = taskPostArray.get(i);
                mTvCategory.setText(mTag);
            }
        }).setCancelText("取消")
                .setCancelColor(Color.GRAY)
                .setContentTextSize(24)
                .setTitleText("类别")
                .setTitleColor(Color.GRAY)
                .setOutSideCancelable(true)
                .setSubmitText("确定")
                .setSubmitColor(ContextCompat.getColor(this, R.color.colorUMain))
                .build();
        catOptions.setPicker(taskPostArray);
        mTvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catOptions.show();
            }
        });
    }


    private void timePickerInit() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE) + 1);
        endDate.set(endDate.get(Calendar.YEAR) + 1, startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE));

        final TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            private SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
            @Override
            public void onTimeSelect(Date date, View v) {
                Calendar cal = Calendar.getInstance();
                if (date.getTime() - cal.getTime().getTime() < 24 * 3600 * 1000) {
                    Toast.makeText(TaskPostActivity.this, "活动时间至少为24小时以后!", Toast.LENGTH_SHORT).show();
                } else {
                    cal.setTime(date);
                    mActivityTime = cal;
                    mTvActivityTime.setText(df.format(date));
                }
            }
        }).setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("选择活动时间")
                .setContentSize(17)
                .setOutSideCancelable(true)
                .isCyclic(false)
                .setTitleColor(Color.GRAY)
                .setSubmitColor(ContextCompat.getColor(this, R.color.colorUMain))
                .setCancelColor(Color.GRAY)
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(true)
                .setRangDate(startDate, endDate)
                .build();
        mActivityTime = Calendar.getInstance();
        mLlActivityTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.setDate(mActivityTime);
                pvTime.show();
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
        if(UPublicTool.byteCount(mDescription)>225){
            Toast.makeText(TaskPostActivity.this, "描述不能多于225个字节哦～",Toast.LENGTH_SHORT).show();
            return false;
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
        if(!mStart.equals("选择地址") && mDestination.equals("选择地址") )
        {
            Toast.makeText(TaskPostActivity.this,"请选择目的地哦～",Toast.LENGTH_SHORT).show();
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



    public static void startActivity(Context context, int type) {
        Intent intent = new Intent(context, TaskPostActivity.class);
        intent.putExtra(EXTRA_POST_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mThreadPool.shutdownNow();
    }
}


