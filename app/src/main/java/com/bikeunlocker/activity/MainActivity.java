package com.bikeunlocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bikeunlocker.R;
import com.bikeunlocker.util.SharedPrefManager;
import com.bikeunlocker.util.api.BaseApiService;
import com.bikeunlocker.util.api.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnLogout)
    Button btnLogout;
    SharedPrefManager sharedPrefManager;
    Location mLocation = null;
    public static final String TAG = "TAG";
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefManager = new SharedPrefManager(this);
        mContext = this;
        mApiService = UtilsApi.getApiService();
        ButterKnife.bind(this);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN_SUCCEED, false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        askPermissionsAndShowMyLocation();
        requestLocation();
    }

    public void requestLocation() {
        mApiService.locationRequest(Double.toString(mLocation.getLatitude()), Double.toString(mLocation.getLongitude())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("DEBUG", "onResponse: Success!");
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("error").equals("false")) {
                            Toast.makeText(mContext, "Registration Success!", Toast.LENGTH_LONG).show();
                        } else {
                            String error_message = jsonObject.getString("error_msg");
                            Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Debug", "onFailure: ERROR " + t.getMessage());
                Toast.makeText(mContext, "Connect Internet Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void askPermissionsAndShowMyLocation() {
        // Yêu cầu quyền truy cập vị trí, chỉ yêu cầu trong lần chạy chương trình đầu tiên
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // Cac quyen can duoc cho phep:
                String[] perrmissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
                android.support.v4.app.ActivityCompat.requestPermissions(this, perrmissions, REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }
            if (accessCoarsePermission == PackageManager.PERMISSION_GRANTED && accessCoarsePermission == PackageManager.PERMISSION_GRANTED) {
                this.showMyLocation();
            }
            return;
        }
        this.showMyLocation();
    }

    /* Sự kiện sau khi trả lời yêu cầu quyền vị trí
    * @param grantResults: Nếu yc bị bỏ qua thì grantResults[] = rỗng*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                    this.showMyLocation();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = this.getEnableLocationProvider();
        final long MIN_TIME = 1000; // ms
        final float MIN_DISTANCE = 1; //m
        if (locationProvider == null) {
            return;
        }
        try {
            // Sau su kien cho phep
            locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
            // Lay ra vi tri
            mLocation = locationManager.getLastKnownLocation(locationProvider);

        } catch (SecurityException e) {
            Toast.makeText(this, " Show my location error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Show my location error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        if (mLocation != null) {
            Log.i(Double.toString(mLocation.getLongitude()), Double.toString(mLocation.getLatitude()));
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Location not found!");
        }

    }

    // Tim 1 nha cung cap vi tri hien thoi dang mo
    private String getEnableLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria(); // Tieu chi de tim 1 nha cung cap
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (!locationManager.isProviderEnabled(bestProvider)) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No location provider enabled!");
            return null;
        } else
            return bestProvider;
    }
}
