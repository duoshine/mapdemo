package cn.chenanduo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class MainActivity extends AppCompatActivity {

    private double mLatitude;
    private double mLongitude;
    private boolean isSucceed;
    private TextView mTvMap;
    private Location mLoc_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvMap = (TextView) findViewById(R.id.tvMap);

    }
    private void initLocation() {
        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = null;
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功
                        isSucceed = true;
                        switch (aMapLocation.getLocationType()) {
                            case 0:
                                mTvMap.setText("定位失败");
                                break;
                            case 1:
                                getLat(aMapLocation);
                                mTvMap.setText("GPS定位成功:"+aMapLocation.getAddress());
                                break;
                            case 2:
                                getLat(aMapLocation);
                                mTvMap.setText("前次定位结果:"+aMapLocation.getAddress());
                                break;
                            case 5:
                                getLat(aMapLocation);
                                mTvMap.setText("wifi定位成功:"+aMapLocation.getAddress());
                                break;
                            case 6:
                                getLat(aMapLocation);
                                mTvMap.setText("基站定位成功:"+aMapLocation.getAddress());
                                break;
                        }
                        aMapLocation.getAccuracy();//获取精度信息
                    }else {
                        mTvMap.setText("定位失败");
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = null;
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void getLat(AMapLocation aMapLocation) {
        //可在其中解析amapLocation获取相应内容。
        mLatitude = aMapLocation.getLatitude();//获取纬度
        mLongitude = aMapLocation.getLongitude();//获取经度
        mLoc_end = new Location();
        mLoc_end.setLat(mLatitude);
        mLoc_end.setLng(mLongitude);
        Toast.makeText(this, "纬度"+mLatitude+"经度"+ mLongitude, Toast.LENGTH_SHORT).show();
    }

    public void btn1(View view) {//开始定位
        //初始化位置信息
        initLocation();
    }

    public void btn2(View view) {//开始导航
        if (mLoc_end != null) {
            NativeDialog msgDialog = new NativeDialog(this, null, mLoc_end);
            msgDialog.show();
        }
    }
}
