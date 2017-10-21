package ecnu.uleda.view_controller;


import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import java.util.ArrayList;

import ecnu.uleda.R;

public class LocationListActivity extends AppCompatActivity {

    private TencentLocationManager mLocationManager;
    private TencentSearch mTencentSearch;
    private float latitude = 0;
    private float longitude = 0;
    private ListView mListView;
    private Location mLocation=null;
    private int mPageIndex=1;
    private ArrayList<SearchResultObject.SearchResultData> list = new ArrayList<>();
    private EditText mEditText;
    private String mKeyWord="华东师范大学";
    private Button mButton;
    private Button mButtonBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mEditText=(EditText)findViewById(R.id.location_edit_text);
        mEditText.setText(mKeyWord);
        mEditText.setSelection(mKeyWord.length());
        mButton=(Button)findViewById(R.id.location_choose_button);
        mButton.setEnabled(false);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=mEditText.getText().toString();
                if(str.length()==0){
                    Toast.makeText(LocationListActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                list.clear();
                mPageIndex=0;
                mKeyWord=str;
                searchPOIAndPut();
            }
        });
        mListView=(ListView)findViewById(R.id.list_view_location);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(mListView.getLastVisiblePosition()==mListView.getCount()-1){
                    if(mLocation!=null) {
                        mPageIndex++;
                        searchPOIAndPut();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchResultObject.SearchResultData a=list.get(i);
            Intent intent=new Intent();
                intent.putExtra("title",a.title);
                intent.putExtra("lat",a.location.lat);
                intent.putExtra("lng",a.location.lng);
            LocationListActivity.this.setResult(0,intent);
            LocationListActivity.this.finish();
        }
        });
//        getLocation();
        mTencentSearch = new TencentSearch(getApplicationContext());
        searchPOIAndPut();
        mButtonBack = (Button)findViewById(R.id.button_location_list_back);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void searchPOIAndPut(){
        mButton.setEnabled(false);
        mLocation = new Location();
        mLocation.lat(31.228470f).lng(121.40640f); // 记得移除
        SearchParam.Nearby mNearBy = new SearchParam.Nearby().point(mLocation);
        mNearBy.r(5000);
        SearchParam object = new SearchParam().keyword(mKeyWord).boundary(mNearBy);
        object.page_size(20);
        object.page_index(mPageIndex);
        mTencentSearch.search(object,new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                SearchResultObject oj = (SearchResultObject) baseObject;
                int loc=mListView.getFirstVisiblePosition();
                if (oj.data != null) {
                    for (SearchResultObject.SearchResultData data : oj.data) {
                        list.add(data);
                    }
                }
                setListViewAdapter();
                mListView.setSelection(loc==0?0:loc+1);
                mButton.setEnabled(true);
            }
            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                throwable.printStackTrace();
                Log.e("bb", "fail");
                mButton.setEnabled(true);
            }
        });
    }
    private void getLocation()
    {
        mLocationManager = TencentLocationManager.getInstance(this.getApplication());

        int errorCode = mLocationManager.requestLocationUpdates(TencentLocationRequest.create()
                .setInterval(5000)
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA), new TencentLocationListener() {
            @Override
            public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
                mLocationManager.removeUpdates(this);
                if (i != TencentLocation.ERROR_OK) {
                    Toast.makeText(LocationListActivity.this, "定位错误，请检查GPS状态", Toast.LENGTH_SHORT).show();
                    return;
                }
                latitude = (float) tencentLocation.getLatitude();
                longitude = (float) tencentLocation.getLongitude();

                getNearBy();
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {
            }
        });
        if (errorCode > 0) {
            Log.e("LocationListActivity", "errorCode: " + errorCode);
        }
    }

    private void getNearBy() {
        mTencentSearch = new TencentSearch(getApplicationContext());
        mLocation = new Location().lat(latitude).lng(longitude);
        searchPOIAndPut();
    }


    private void setListViewAdapter(){
        mListView.setAdapter(new LocationListAdapter(this,list));
    }

}
