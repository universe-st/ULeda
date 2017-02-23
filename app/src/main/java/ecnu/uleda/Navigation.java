package ecnu.uleda;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.WalkingParam;
import com.tencent.lbssearch.object.result.WalkingResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.mapsdk.raster.model.Polyline;
import com.tencent.mapsdk.raster.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Shensheng on 2017/2/23.
 * 导航控制
 */

public class Navigation implements HttpResponseListener,TencentLocationListener{

    private static Navigation sNavigation=null;
    private Marker mMarker;
    private Runnable mRunnable=null;
    private Context mContext;
    private TencentMap mMap;
    private Polyline mPolyline;
    private TencentLocationManager mManager;
    public static Navigation getInstance(Context context, TencentMap map){
        if(sNavigation==null){
            sNavigation=new Navigation();
        }
        sNavigation.mContext=context.getApplicationContext();
        sNavigation.mMap=map;
        sNavigation.mManager=TencentLocationManager.getInstance(context);
        return sNavigation;
    }

    public void startNavigation(Location start, Location end){
        endNavigation();
        TencentSearch tencentSearch = new TencentSearch(mContext);
        WalkingParam walkingParam = new WalkingParam();
        walkingParam.from(start);
        walkingParam.to(end);
        tencentSearch.getDirection(walkingParam,this);
    }


    @Override
    public void onSuccess(int arg, BaseObject baseObject){
        if(baseObject==null){
            return;
        }
        WalkingResultObject object=(WalkingResultObject)baseObject;
        drawPolylineOnMap(object);
    }
    private void drawPolylineOnMap(WalkingResultObject object){
        WalkingResultObject.Route[] routes=object.result.routes.toArray(new WalkingResultObject.Route[object.result.routes.size()]);
        if(routes.length==0){
            return;
        }
        Arrays.sort(routes, new Comparator<WalkingResultObject.Route>() {
            @Override
            public int compare(WalkingResultObject.Route route, WalkingResultObject.Route t1) {
                return (int)((route.duration-t1.duration)*1000);
            }
        });
        WalkingResultObject.Route route=routes[0];
        List<LatLng> latLngs=new ArrayList<>();
        for(Location location:route.polyline){
            latLngs.add(new LatLng(location.lat,location.lng));
        }
        mPolyline=mMap.addPolyline(new PolylineOptions()
        .addAll(latLngs)
        .color(0xFFDD5A44)
        .width(10f));
        mManager.requestLocationUpdates(TencentLocationRequest.create()
                .setInterval(2000)
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA),this);
    }
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if(mMarker==null){
            mMarker=mMap.addMarker(new MarkerOptions()
            .position(new LatLng(tencentLocation.getLatitude(),tencentLocation.getLongitude()))
            .icon(BitmapDescriptorFactory
                    .defaultMarker()));
        }else{
            mMarker.setPosition(new LatLng( tencentLocation.getLatitude(),tencentLocation.getLongitude() ) );
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
    @Override
    public void onFailure(int arg,String state,Throwable t){

    }
    public void endNavigation(){
        if(mPolyline!=null) {
            mPolyline.remove();
        }
    }

    public void setOnEnd(Runnable runnable){
        mRunnable=runnable;
    }

    private Navigation(){

    }
}
