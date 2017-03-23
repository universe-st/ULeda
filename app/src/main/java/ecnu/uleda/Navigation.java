package ecnu.uleda;

import android.content.Context;
import android.widget.Toast;

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
 * 导航控制模块
 */

public class Navigation implements HttpResponseListener,TencentLocationListener{

    private static Navigation sNavigation=null;
    private Marker mMarker;
    private Context mContext;
    private TencentMap mMap;
    private Polyline mPolyline;
    private TencentLocationManager mManager;
    private boolean mIsWorking=false;
    public interface OnArriveListener {
        void onArrive();
    }

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
        mIsWorking=true;
        TencentSearch tencentSearch = new TencentSearch(mContext);
        WalkingParam walkingParam = new WalkingParam();
        walkingParam.from(start);
        walkingParam.to(end);
        tencentSearch.getDirection(walkingParam,this);//通过腾讯地图API计算路线，返回结果加载给onSuccess
    }


    @Override
    public void onSuccess(int arg, BaseObject baseObject){
        if(baseObject==null){
            return;
        }
        WalkingResultObject object=(WalkingResultObject)baseObject;
        drawPolylineOnMap(object);
        showText("路线计算完成");
        mManager.requestLocationUpdates(TencentLocationRequest.create()
                .setInterval(2000)
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA),this);
    }
    //在地图上绘制折线
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
    }
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if(mMarker==null){
            mMarker=mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tencentLocation.getLatitude(),tencentLocation.getLongitude()))
                    .title("你的位置")
                    .icon(BitmapDescriptorFactory
                        .defaultMarker())
                    );
        }else{
            mMarker.setPosition(new LatLng( tencentLocation.getLatitude(),tencentLocation.getLongitude() ) );
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        //TODO:添加状态变化提示用户
    }
    @Override
    public void onFailure(int arg,String state,Throwable t){
        //todo:路线计算失败的提示
    }
    public void endNavigation(){
        mIsWorking=false;
        if(mPolyline!=null) {
            mPolyline.remove();
        }
        if(mMarker!=null){
            mMarker.remove();
        }
    }
    public boolean isWorking(){
        return mIsWorking;
    }
    private void showText(String s){
        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
    }
    private Navigation(){

    }
}
