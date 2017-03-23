package ecnu.uleda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
    private Handler mHandler=new Handler(){
      @Override
      public void handleMessage(Message msg){
          if(msg.what==0){
              switch (mCurrent){
                  case 0:
                      putMarkers(mUTaskManager.getTasksInMap(UTaskManager.RECOMMEND));
                      break;
                  case 1:
                      putMarkers(mUTaskManager.getTasksInMap(UTaskManager.HELP_EACH_OTHER));
                      break;
                  case 2:
                      putMarkers(mUTaskManager.getTasksInMap(UTaskManager.U_ACTIVITY));
                      break;
                  case 3:
                      putMarkers(mUTaskManager.getTasksInMap(UTaskManager.FOLLOW));
                      break;
                  default:
                      break;
              }
          }else if(msg.what==1){
              Toast.makeText(UMainFragment.this.getActivity(),"错误："+msg.obj,Toast.LENGTH_SHORT).show();
          }
      }
    };
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
                onPressRecommendButton();
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
                onPressActivityButton();
            }
        });
        mButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(3);
                onPressFollowButton();
            }
        });
        mButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrent(4);
                onPressNavigationButton();
            }
        });
        setCurrent(0);
        mMapView=(MapView)v.findViewById(R.id.map_view);
        mTencentMap=mMapView.getMap();
        mTencentMap.setInfoWindowAdapter(new TencentMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v=View.inflate(UMainFragment.this.getActivity(),R.layout.u_marker_info_window,null);
                return v;
            }

            @Override
            public void onInfoWindowDettached(Marker marker, View view) {

            }
        });
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
    private void putMarkers(ArrayList<UTask> tasks){
        //TODO:放好Markers
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
    private void onPressRecommendButton(){
        hideMarkers();
        mMarkers=new ArrayList<>();

    }
    private void onPressHelpEachOtherButton(){

    }
    private void onPressActivityButton(){

    }
    private void onPressFollowButton(){

    }
    private void onPressNavigationButton(){

    }
}
