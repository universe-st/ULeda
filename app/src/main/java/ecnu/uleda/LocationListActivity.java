package ecnu.uleda;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.ArrayList;
import java.util.List;

public class LocationListActivity extends AppCompatActivity {

    private TencentLocationManager mLocationManager;
    private SearchResultObject mSearchResultObject;
    private SearchResultObject.SearchResultData mSearchResultData;
    private TencentSearch mTencentSearch;
    private float latitude = 0;
    private float longitude = 0;
    private ListView mListView;
    private Location mLocation=null;
    private int mPageIndex=1;
    private ArrayList<String[]> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
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
        getLocation();
    }
    private void searchPOIAndPut(){
        SearchParam.Nearby mNearBy = new SearchParam.Nearby().point(mLocation);
        mNearBy.r(5000);
        SearchParam object = new SearchParam().keyword("华东师范").boundary(mNearBy);
        object.page_size(20);
        object.page_index(mPageIndex);
        mTencentSearch.search(object,new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                SearchResultObject oj = (SearchResultObject) baseObject;
                int loc=mListView.getFirstVisiblePosition();
                if (oj.data != null) {
                    for (SearchResultObject.SearchResultData data : oj.data) {
                        list.add(new String[]{data.title,data.address});
                    }
                }
                setListViewAdapter();
                mListView.setSelection(loc+1);
            }
            @Override
            public void onFailure(int i, String s, Throwable throwable) {
            }
        });
    }
    private void getLocation()
    {
        mLocationManager = TencentLocationManager.getInstance(this.getApplication());

        mLocationManager.requestLocationUpdates(TencentLocationRequest.create()
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
