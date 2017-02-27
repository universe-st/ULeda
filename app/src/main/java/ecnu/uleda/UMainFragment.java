package ecnu.uleda;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.lbssearch.object.Location;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import java.util.ArrayList;
import android.Manifest;


public class UMainFragment extends Fragment {
    //主界面的Fragment
    private Button[] mButtons;
    private FloatingActionButton mFab;
    private int mCurrent;
    private MapView mMapView;
    private TencentMap mTencentMap;
    private ArrayList<Marker> mMarkers=new ArrayList<>();
    private UTaskManager mUTaskManager = UTaskManager.getInstance();
    private TencentLocationManager mLocationManager;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLocationManager = TencentLocationManager.getInstance(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.u_main_fragment,parent,false);
        init(v);
        mMapView.onCreate(savedInstanceState);
        //测试代码
        mTencentMap.setCenter(new LatLng(31.2284994411d,121.4063922732d));
        //测试代码
        mTencentMap.setZoom(18);

        return v;
    }

    @Override
    public void onResume(){
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop(){
        mMapView.onStop();
        super.onStop();
    }
    @Override
    public void onPause(){
        mMapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy(){
        mMapView.onDestroy();
        super.onDestroy();
    }
    public MapView getMapView(){
        return mMapView;
    }
    private void init(View v){
        mFab=(FloatingActionButton)v.findViewById(R.id.float_button);
        mFab.setAlpha(0.7f);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UMainFragment.this.getActivity(),TaskPostActivity.class);
                startActivity(intent);
            }
        });
        mButtons=new Button[5];
        mButtons[0]=(Button)v.findViewById(R.id.recommended_bt);
        mButtons[1]=(Button)v.findViewById(R.id.help_each_other_bt);
        mButtons[2]=(Button)v.findViewById(R.id.activity_bt);
        mButtons[3]=(Button)v.findViewById(R.id.follow_bt);
        mButtons[4]=(Button)v.findViewById(R.id.navigation_bt);

        mButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(0);
            }
        });
        mButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(1);
                onPressHelpEachOtherButton();
            }
        });
        mButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(2);
            }
        });
        mButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(3);
            }
        });
        mButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(4);
                mLocationManager.requestLocationUpdates(TencentLocationRequest.create()
                        .setInterval(5000)
                        .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA), new TencentLocationListener() {
                    @Override
                    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
                        mLocationManager.removeUpdates(this);
                        if(i!=TencentLocation.ERROR_OK){
                            Toast.makeText(UMainFragment.this.getActivity(),"定位错误，请检查GPS状态",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Navigation.getInstance(UMainFragment.this.getActivity(),mTencentMap)
                                .startNavigation(new Location((float) tencentLocation.getLatitude(),
                                        (float)tencentLocation.getLongitude()),
                                        new Location(31.2296355001f,121.4034706544f));
                    }

                    @Override
                    public void onStatusUpdate(String s, int i, String s1) {

                    }
                });
            }
        });
        setCurrent(0);
        mMapView=(MapView)v.findViewById(R.id.map_view);
        mTencentMap=mMapView.getMap();
    }
    public void setCurrent(int p){
        mCurrent=p;
        setButtonsPressed(p);
    }
    private void setButtonsPressed(int i){
        for(int j=0;j<5;++j){
            if(j==i){
                mButtons[j].setBackgroundResource(R.drawable.bt_shape);
            }else{
                mButtons[j].setBackgroundResource(R.drawable.bt_shape_unpressed);
            }
        }
    }
    private void showMarkers(){
        if(mMarkers!=null){
            for(Marker m :mMarkers){
                if(m!=null){
                    m.setVisible(true);
                }
            }
        }
    }

    private void hideMarkers(){
        if(mMarkers!=null){
            for(Marker m:mMarkers){
                if(m!=null){
                    m.setVisible(false);
                }
            }
        }
    }

    private void onPressHelpEachOtherButton(){
        //TODO:设置【互助】按钮按下后的操作
    }
}
