package ecnu.uleda;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    //private SearchParam mSearchParam;
    private SearchResultObject mSearchResultObject;
    private SearchResultObject.SearchResultData mSearchResultData;
    private TencentSearch mTencentSearch;
    //private Location mLocation;
    //private SearchParam.Nearby mNearBy;
    private float latitude = 0;
    private float longitude = 0;
    private String result;
    private String[] address=new String[50];
    private ListView mlistView;
    private List LocationList;

    private ArrayList<String> list = new ArrayList<String>();
    //private Button mButtontest ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        /*mButtontest= (Button)findViewById(R.id.test);



        //getNearBy();
        mButtontest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mButtontest.setText(address[1]);
            }
        });*/

        getLocation();

        //mTaskListAdapter=(TaskListAdapter)mUTaskManager
          //      .setListView(mListView,this.getActivity().getApplicationContext());


       /* ArrayAdapter locationAdapter = new ArrayAdapter
                (ListView.this,android.R.layout.simple_list_item_1,result);
        mlistView = (ListView)findViewById(R.id.list_view_location);

        mlistView.setAdapter(locationAdapter);
        setListViewClick();*/
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
        Location mLocation = new Location().lat(latitude).lng(longitude);
        SearchParam.Nearby mNearBy = new SearchParam.Nearby().point(mLocation);
        mNearBy.r(4000);//= 2000f;
        SearchParam object = new SearchParam().keyword("华东师范").boundary(mNearBy);
        object.page_size(20);

        mTencentSearch.search(object,new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                SearchResultObject oj = (SearchResultObject) baseObject;
                if (oj.data != null) {
                    //result = "poi";
                    for (SearchResultObject.SearchResultData data : oj.data) {
                        //Log.v("demo", "title:" + data.address);
                        list.add(data.title);
                    }
                }
                address = list.toArray(new String[list.size()]);
                listInit();
            }
            @Override
            public void onFailure(int i, String s, Throwable throwable) {
            }
        });
    }

    private void setListViewClick(){
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Thanks to MicroDog.
                String location=(String) adapterView.getItemAtPosition(i);
                Intent intent=new Intent(LocationListActivity.this,TaskPostActivity.class);
                intent.putExtra("UTask",location);
                startActivity(intent);
            }
        });
    }

    private void listInit(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (LocationListActivity.this,android.R.layout.simple_list_item_1,address);
        ListView listview = (ListView)findViewById(R.id.list_view_location);
        listview.setAdapter(adapter);
    }

}
