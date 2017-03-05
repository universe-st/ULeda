package ecnu.uleda;

//import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import java.util.ArrayList;
import java.util.List;

public class TaskPostActivity extends AppCompatActivity {

    private UserOperatorController mUserOperatorController;

    private Handler mClickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
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
        taskPostArray.add("全部");
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
    private EditText mEtStart;
    private EditText mEtdestination;
    private EditText mEtDescription;

    private Button mButtonBack;
    private Button mButtonTaskPost;
    //private Spinner spinnerTag;
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

    private Button buttonTest;
    private TencentLocationManager mLocationManager;
    private float latitude = 0;
    private float longitude = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_post_activity);
        init();
        SpinnerInit();

        mLocationManager = TencentLocationManager.getInstance(this.getApplication());

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
                SpinnerEvent();
                if (!getTaskPost()) {
                    Toast.makeText(TaskPostActivity.this, "请输入有效数据～", Toast.LENGTH_SHORT).show();
                    return;
                }
                //judgeEdittext();
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void init() {
        mButtonBack = (Button) findViewById(R.id.button_task_post_back);
        mButtonTaskPost = (Button) findViewById(R.id.button_task_post);
        mEtTitle = (EditText) findViewById(R.id.task_post_title);
        mEtPrice = (EditText) findViewById(R.id.task_post_payment);
        mEtdestination = (EditText) findViewById(R.id.task_post_destination);
        mEtActiveTime = (EditText) findViewById(R.id.task_post_activeTime);
        mEtStart = (EditText) findViewById(R.id.task_post_start);
        mEtdestination = (EditText) findViewById(R.id.task_post_description);
        mEtDescription = (EditText) findViewById(R.id.task_post_description);


        buttonTest = (Button) findViewById(R.id.button_test);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationManager.requestLocationUpdates(TencentLocationRequest.create()
                        .setInterval(5000)
                        .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA), new TencentLocationListener() {
                    @Override
                    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
                        mLocationManager.removeUpdates(this);
                        if (i != TencentLocation.ERROR_OK) {
                            Toast.makeText(TaskPostActivity.this, "定位错误，请检查GPS状态", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        latitude = (float) tencentLocation.getLatitude();
                        longitude = (float) tencentLocation.getLongitude();
                    }

                    @Override
                    public void onStatusUpdate(String s, int i, String s1) {
                    }
                });

                //Toast.makeText(TaskPostActivity.this,""+longitude ,Toast.LENGTH_SHORT).show();
                getNearAdd();

               /* Geo2AddressParam param = new Geo2AddressParam().location(new Location().lat(latitude).lng(longitude));
                param.get_poi(true);

                Geo2AddressResultObject oj = (Geo2AddressResultObject) object;
                String result = "坐标转地址：lat:" + String.valueOf(latitude) + "  lng:" +
                        String.valueOf(longitude) + "\n\n";
                if (oj.result != null) {
                    Log.v("demo", "address:" + oj.result.address);
                    result += oj.result.address;
                }
                Toast.makeText(TaskPostActivity.this, result, Toast.LENGTH_SHORT).show();*/
            }

        });
    }

    private void getNearAdd() {

        TencentSearch mtencentSearch = new TencentSearch(
                getApplicationContext());

        //通过前面获取的经纬度在这里会用到，因为查找周围是需要根据Location对象来进行查找
        Geo2AddressParam param = new Geo2AddressParam().location(new Location().lat(latitude).lng(longitude));

        //设置为true才能获取到周围的建筑，默认为false
        param.get_poi(true);

        /** 因为是获取周围建筑，所以也会有监听，需要实现他的2个方法onSuccess(int arg0, Header[] arg1, BaseObject arg2)                    和onFailure(int arg0, Header[] arg1, String arg2,Throwable arg3) */

        mtencentSearch.geo2address(param, new HttpResponseListener() {

            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                if (baseObject != null) {


                    Geo2AddressParam param = new Geo2AddressParam().location(new Location().lat(latitude).lng(longitude));
                    param.get_poi(true);

                    Geo2AddressResultObject oj = (Geo2AddressResultObject) baseObject;
                    String result = oj.result.address;//
                    // = "坐标转地址：lat:" + String.valueOf(latitude) + "  lng:" +String.valueOf(longitude) + "\n\n";
                    /*if (oj.result != null) {
                        Log.v("demo", "address:" + oj.result.address);
                        result += oj.result.address;
                    }*/
                    Toast.makeText(TaskPostActivity.this, result, Toast.LENGTH_SHORT).show();
                    // result += oj.result.address;
                }
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }



           /* @Override
            public void onSuccess(int arg0, PreferenceActivity.Header[] arg1, BaseObject object) {
                // TODO Auto-generated method stub
                if (object != null) {


                    Geo2AddressParam param = new Geo2AddressParam().location(new Location().lat(latitude).lng(longitude));
                    param.get_poi(true);

                    Geo2AddressResultObject oj = (Geo2AddressResultObject) object;
                    String result = "坐标转地址：lat:" + String.valueOf(latitude) + "  lng:" +
                            String.valueOf(longitude) + "\n\n";
                    if (oj.result != null) {
                        Log.v("demo", "address:" + oj.result.address);
                        result += oj.result.address;
                    }
                    Toast.makeText(TaskPostActivity.this, result, Toast.LENGTH_SHORT).show();
                    // result += oj.result.address;
                }


            }

            @Override
            public void onFailure(int arg0, PreferenceActivity.Header[] arg1, String arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub

            }
*/
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
/*    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TaskPost Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }*/

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String price = mEtPrice.getText().toString();
            int posDot = price.indexOf('.');
            if (posDot <= 0)
                return;
            else if (price.length() - posDot - 1 > 2) {
                String string;
                string = price.substring(0, price.length() - 1);
                mEtPrice.setText(string);
                mEtPrice.setSelection(string.length());
            }
        }

        public void afterTextChanged(Editable s) {
        }
    }

    boolean getTaskPost() {
        //mId,mPpassport,mTitle,mTag,mDescription,mPrice,mPath,mActiveTime,mPosition
        mUserOperatorController = UserOperatorController.getInstance();
        mId = mUserOperatorController.getId();
        mPpassport = mUserOperatorController.getPassport();
        mTitle = mEtTitle.getText().toString();
        mDescription = mEtDescription.getText().toString();
        mPrice = mEtPrice.getText().toString();
        mPath = mEtStart.getText().toString() + "|" + mEtdestination.getText().toString();
        mActiveTime = mEtActiveTime.getText().toString();
        mPosition = "31.2296,121.403";
        //TODO:

        if (mPrice.equals("") || mActiveTime.equals("") || mEtStart.getText().toString().equals("")
                || (mEtdestination.getText().toString().equals("")))
            return false;
        //description不能为空???
        return true;
    }


    //TODO: 啊啊啊这个怎么才能不跳转界面乖乖显示Toast
    /*private void judgeEdittext()
    {
        if(mTitle.equals(""))
        {
            Toast.makeText(TaskPostActivity.this, "标题不能为空哦～",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mPrice.equals(""))
        {
            Toast.makeText(TaskPostActivity.this, "价格不能为空哦～",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mActiveTime.equals(""))
        {
            Toast.makeText(TaskPostActivity.this, "时效不能为空哦～",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mEtStart.getText().toString().equals(""))
        {
            Toast.makeText(TaskPostActivity.this, "出发地不能为空哦～", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mEtdestination.getText().toString().equals(""))
        {
            Toast.makeText(TaskPostActivity.this, "目的地不能为空哦～", Toast.LENGTH_SHORT).show();
            return;
        }

    }*/

}


