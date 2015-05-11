package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.litesuits.android.log.Log;

/**
 * @author Tan Chunmao
 */
public abstract class LocationActivity extends ActivityBase {
    private static final double LOCATION_ERROR = 4.9E-324D;
    protected volatile BDLocation bdLocation;
    private boolean located;
    protected boolean needLocationWhenStart = true;
    private BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location == null)
                return;
            locationClient.stop();
            if (bdLocation == null) {
                bdLocation = location;
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        located = true;
                        if (LOCATION_ERROR == location.getLatitude() || LOCATION_ERROR == location.getLongitude())
                            onLocateFailure();
                        else
                            onLocated();
                    }
                }, 100);
            }
        }
    };
    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bdLocation = null;
        initLocationClient();
    }

    private void initLocationClient() {
        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); //设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("test", "location need location: " + needLocationWhenStart);
        if (bdLocation == null && needLocationWhenStart) {
            locationClient.registerLocationListener(bdLocationListener);
            locate();
        }
    }

    private void locate() {
        locationClient.start();
        showProgressDialog();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!located) {
                    locationClient.stop();
                    onLocateFailure();
                }
            }
        }, 3000);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (locationClient.isStarted()) {
            locationClient.stop();
            locationClient.unRegisterLocationListener(bdLocationListener);
        }
    }

    protected abstract void onLocated();

    protected void onLocateFailure() {
        bdLocation = new BDLocation();
        bdLocation.setLongitude(0.0);
        bdLocation.setLatitude(0.0);
    }
}
