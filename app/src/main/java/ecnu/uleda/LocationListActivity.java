package ecnu.uleda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class LocationListActivity extends AppCompatActivity {

    private TencentLocationManager mLocationManager;
    private SearchParam mSearchParam;
    private SearchResultObject mSearchResultObject;
    private SearchResultObject.SearchResultData mSearchResultData;
    private float latitude = 0;
    private float longitude = 0;
    private String[] data;
    private Geo2AddressResultObject.ReverseAddressResult.Poi[] address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_activity);


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
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {
            }
        });

        getNearBy();

    }

    private void getNearBy() {

        TencentSearch mtencentSearch = new TencentSearch(getApplicationContext());

        Location location = new Location().lat(latitude).lng(longitude);
        SearchParam.Nearby nearBy = new SearchParam.Nearby().point(location);
        nearBy.r(2000);//= 2000f;
        SearchParam object = new SearchParam().keyword("").boundary(nearBy);
        object.page_size(20);


    }

}
