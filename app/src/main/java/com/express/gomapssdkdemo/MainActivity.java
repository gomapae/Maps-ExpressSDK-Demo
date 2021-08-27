package com.express.gomapssdkdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.mapzen.android.GoMapSDK;
import com.mapzen.android.data.NavigationResult;
import com.mapzen.android.graphics.GoMap;
import com.mapzen.android.graphics.MapFragment2;
import com.mapzen.android.graphics.model.BitmapMarker;
import com.mapzen.android.graphics.model.BitmapMarkerOptions;
import com.mapzen.android.rge.OnNavListener;
import com.mapzen.android.rge.TravelData;
import com.mapzen.tangram.LabelPickListener;
import com.mapzen.tangram.LabelPickResult;
import com.mapzen.tangram.LngLat;
import com.mapzen.tangram.LngLat4Category;
import com.mapzen.tangram.Marker;

import java.util.ArrayList;
import java.util.List;

import jni.bean.Poi;
import jni.bean.Route;
import jni.bean.RoutesInfo;

public class MainActivity extends AppCompatActivity {

    MapFragment2 mapView;
    GoMap goMap;

    Marker currentMarker;
    BitmapMarker bitmapMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoMapSDK.getInstance().init();
        mapView = (MapFragment2) getSupportFragmentManager().findFragmentById(R.id.fragment);
        String[]  permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else if (shouldShowRequestPermissionRationale(permissions[0])) {

        } else {
            requestPermissions(permissions, 100101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap();
            } else {//explain to user

            }
        }
    }

    private void initMap() {


        assert mapView != null;
        mapView.getMapAsync(map -> {
            goMap = map;
            map.setMyLocationEnabled(true);
            map.setCompassButtonEnabled(true);
            map.setPersistMapState(true);
            map.setLayersIconEnable(true);
            map.setFindMePosition(50);
            //设置点击地图区域大小 dp
            map.getMapController().setPickRadius(10f);
            map.setZoom(16f);


            //set label listener on map
            map.setLabelPickListener(labelPickResult -> {
                if (currentMarker != null) {
                    goMap.removeMarker(currentMarker.getMarkerId());
                }
                if (labelPickResult != null && labelPickResult.getCoordinates() != null) {
                    currentMarker = addMarker(labelPickResult.getCoordinates().longitude, labelPickResult.getCoordinates().latitude);
                }
            });
            //set route plan listener
            map.setOnNavListener(new OnNavListener() {
                @Override
                public void onNaviCancel() {

                }

                @Override
                public void onSettingClick() {

                }

                @Override
                public void onPlanRouteSuccess(RoutesInfo routesInfo) {
                    //you can start navigation after plan route success
                }

                @Override
                public void onPlanRouteFailure() {

                }

                @Override
                public void onNavFinish(TravelData travelData) {

                }

                @Override
                public void onSelectPlanRoute(Route route) {

                }

                @Override
                public void onNaviUpdate(NavigationResult navigationResult) {

                }
            });
        });
    }
    public void removeAllMarker(View view) {
        goMap.removeMarker();
        if (bitmapMarker != null) {
            goMap.removeBitmapMarker(bitmapMarker);
        }
    }
    public void marker(View view) {
        if (currentMarker != null) {
            goMap.removeMarker(currentMarker.getMarkerId());
            currentMarker = null;
        }
        if (goMap.getLastKnowLocation() != null) {
            currentMarker = addMarker(goMap.getLastKnowLocation().getLongitude(), goMap.getLastKnowLocation().getLatitude());
        }
    }
    public void bitmapMarker(View view) {
       Drawable drawable = getResources().getDrawable(R.mipmap.icon_test_drive,null);
        if (currentMarker != null) {
            goMap.removeMarker(currentMarker.getMarkerId());
            currentMarker = null;
        }
        if (goMap.getLastKnowLocation() != null) {
            bitmapMarker = addBitmapMarker(goMap.getLastKnowLocation().getLongitude(), goMap.getLastKnowLocation().getLatitude(), drawable);
        }
    }
    public void planRoute(View view) {
        List<Poi> dest = new ArrayList<>();
        if (currentMarker != null) {
            Poi d = new Poi();
            d.setPt(new LngLat(currentMarker.getPosition().getLongitude(), currentMarker.getPosition().getLatitude()));
            dest.add(d);
        }
        Poi start = new Poi();
        start.setPt(new LngLat(goMap.getLastKnowLocation().getLongitude(), goMap.getLastKnowLocation().getLatitude()));
        goMap.planRoute(start, dest);
    }
    public void startNavi(View view) {
        goMap.startNavi();
    }


    /**
     * 添加单个marker
     */
    private Marker addMarker(double lng, double lat) {
        if (goMap != null) {
            return goMap.addMarker(new LngLat4Category(lng, lat));
        }
        return null;
    }

    private BitmapMarker addBitmapMarker(double lng, double lat, Drawable drawable) {
        if (goMap != null) {
            BitmapMarkerOptions bitmapMarkerOptions =
                    new BitmapMarkerOptions().position(new LngLat(lng, lat))
                .icon(drawable);
            return goMap.addBitmapMarker(bitmapMarkerOptions);
        }
        return null;
    }
}