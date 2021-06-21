package com.example.dongbina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dongbina.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.round;
import static java.lang.Thread.sleep;

public class APIMainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private MapView mapView;
    static String[] happy;
    static double latitude;
    static double longitude;
    private GpsTracker gpsTracker;
    public static double getStartPoint1() {
        return latitude;
    }
    public static double getStartPoint2() {
        return longitude;
    }
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apimain);
        // ...

        gpsTracker = new GpsTracker(APIMainActivity.this);

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        System.out.println("MIMI");
        System.out.println(latitude);
        System.out.println(longitude);

        APIExamDatalabTrend thread1 = new APIExamDatalabTrend();
        thread1.start();
        try{// thread 속도를 맞추기 위해서.
            Thread.sleep(3000);
        }catch (InterruptedException e){
            System.out.println(e);
        }

        // 현재 위치의 좌표화
        // https://webnautes.tistory.com/1315
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        //final TextView textview_address = (TextView)findViewById(R.id.textview);


        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(APIMainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(APIMainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(APIMainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(APIMainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(APIMainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(APIMainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(APIMainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
        naverMap.addOnLocationChangeListener(location -> naverMap.setLocationTrackingMode(LocationTrackingMode.Face));
        /* 실시간 좌표 위치 제공
        naverMap.addOnLocationChangeListener(location ->
                Toast.makeText(this,
                        location.getLatitude() + ", " + location.getLongitude(),
                        Toast.LENGTH_SHORT).show());

        */
        /*
        Marker marker2 = new Marker();
        marker2.setPosition(new LatLng(37.359708, 127.1058342));
        marker2.setMap(naverMap);

         */
        double lalatitude = 37.359708 ;
        double lalongtitude = 127.1058342 ;
        int type = register.getType();
        if(type == 1)
        {
            lalatitude = koreanfood.getStartPoint1();
            lalongtitude = koreanfood.getStartPoint2();
        }
        if(type == 2)
        {
            lalatitude = chinesefood.getStartPoint1();
            lalongtitude = chinesefood.getStartPoint2();
        }
        if(type == 3)
        {
            lalatitude = chicken.getStartPoint1();
            lalongtitude = chicken.getStartPoint2();
        }
        if(type == 4)
        {
            lalatitude = pizza.getStartPoint1();
            lalongtitude = pizza.getStartPoint2();
        }
        if(type == 5)
        {
            lalatitude = fastfood.getStartPoint1();
            lalongtitude = fastfood.getStartPoint2();
        }
        if(type == 6)
        {
            lalatitude = pig.getStartPoint1();
            lalongtitude = pig.getStartPoint2();
        }
        Marker marker = new Marker();
        marker.setPosition(new LatLng(lalatitude, lalongtitude));
        marker.setMap(naverMap);




        for(int i=0; i<1000; i++) // thread 속도를 맞추기 위해서.
            happy = JsonParser.getNami();
        //happy = JsonParser.getHami();



    /*
        for(String s : happy) {
            System.out.println("Boy");
            System.out.println(s);
        }
    */
        System.out.println("Like");
        // System.out.println(Double.parseDouble(happy[3000]));
        System.out.println(happy.length);
        System.out.println(Double.parseDouble(happy[happy.length-1]));
        System.out.println(Double.parseDouble(happy[happy.length-2]));
        System.out.println(Double.parseDouble(happy[0]));
        System.out.println(Double.parseDouble(happy[1]));
        int lucky = happy.length;

        //double start1 = APIMainActivity.getStartPoint1();
        //double start2 = APIMainActivity.getStartPoint2();
        //129.0823278826256"+","+"35.23099867771562

        double start1 = 129.08418631247693;
        double start2 = 35.231588495738066;
        System.out.println("LOOK");

        int look = round(lucky/20);
        System.out.println(look);
        if(look%2==1)
            look--;

        PathOverlay path = new PathOverlay();
        if(lucky==8){
            path.setCoords(Arrays.asList(
                    //new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),

                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==20){
            path.setCoords(Arrays.asList(
                    //new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),

                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==22){
            path.setCoords(Arrays.asList(
                   // new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),



                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==24){
            path.setCoords(Arrays.asList(
                   // new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),


                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==26){
            path.setCoords(Arrays.asList(
                   // new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),



                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==28){
            path.setCoords(Arrays.asList(
                    //new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),


                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==30){
            path.setCoords(Arrays.asList(
                   // new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),

                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==32){
            path.setCoords(Arrays.asList(
                   // new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),


                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==34){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),

                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==36){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),


                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==38){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==40){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==42){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==44){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==46){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==48){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==50){
            path.setCoords(Arrays.asList(
                //    new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==52){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==54){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==56){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==58){
            path.setCoords(Arrays.asList(
                //    new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==60){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==62){
            path.setCoords(Arrays.asList(
                //    new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==64){
            path.setCoords(Arrays.asList(
               //     new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==66){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==68){
            path.setCoords(Arrays.asList(
                //    new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==70){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==72){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==74){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==76){
            path.setCoords(Arrays.asList(
                   // new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==78){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==80){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[79]), Double.parseDouble(happy[78])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==82){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[79]), Double.parseDouble(happy[78])),
                    new LatLng(Double.parseDouble(happy[81]), Double.parseDouble(happy[80])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==84){
            path.setCoords(Arrays.asList(
                //    new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[79]), Double.parseDouble(happy[78])),
                    new LatLng(Double.parseDouble(happy[81]), Double.parseDouble(happy[80])),
                    new LatLng(Double.parseDouble(happy[83]), Double.parseDouble(happy[82])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==86){
            path.setCoords(Arrays.asList(
                  //  new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[79]), Double.parseDouble(happy[78])),
                    new LatLng(Double.parseDouble(happy[81]), Double.parseDouble(happy[80])),
                    new LatLng(Double.parseDouble(happy[83]), Double.parseDouble(happy[82])),
                    new LatLng(Double.parseDouble(happy[85]), Double.parseDouble(happy[84])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==88){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[79]), Double.parseDouble(happy[78])),
                    new LatLng(Double.parseDouble(happy[81]), Double.parseDouble(happy[80])),
                    new LatLng(Double.parseDouble(happy[83]), Double.parseDouble(happy[82])),
                    new LatLng(Double.parseDouble(happy[85]), Double.parseDouble(happy[84])),
                    new LatLng(Double.parseDouble(happy[87]), Double.parseDouble(happy[86])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else if(lucky==90){
            path.setCoords(Arrays.asList(
                 //   new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[3]), Double.parseDouble(happy[2])),
                    new LatLng(Double.parseDouble(happy[5]), Double.parseDouble(happy[4])),
                    new LatLng(Double.parseDouble(happy[7]), Double.parseDouble(happy[6])),
                    new LatLng(Double.parseDouble(happy[9]), Double.parseDouble(happy[8])),
                    new LatLng(Double.parseDouble(happy[11]), Double.parseDouble(happy[10])),
                    new LatLng(Double.parseDouble(happy[13]), Double.parseDouble(happy[12])),
                    new LatLng(Double.parseDouble(happy[15]), Double.parseDouble(happy[14])),
                    new LatLng(Double.parseDouble(happy[17]), Double.parseDouble(happy[16])),
                    new LatLng(Double.parseDouble(happy[19]), Double.parseDouble(happy[18])),
                    new LatLng(Double.parseDouble(happy[21]), Double.parseDouble(happy[20])),
                    new LatLng(Double.parseDouble(happy[23]), Double.parseDouble(happy[22])),
                    new LatLng(Double.parseDouble(happy[25]), Double.parseDouble(happy[24])),
                    new LatLng(Double.parseDouble(happy[27]), Double.parseDouble(happy[26])),
                    new LatLng(Double.parseDouble(happy[29]), Double.parseDouble(happy[28])),
                    new LatLng(Double.parseDouble(happy[31]), Double.parseDouble(happy[30])),
                    new LatLng(Double.parseDouble(happy[33]), Double.parseDouble(happy[32])),
                    new LatLng(Double.parseDouble(happy[35]), Double.parseDouble(happy[34])),
                    new LatLng(Double.parseDouble(happy[37]), Double.parseDouble(happy[36])),
                    new LatLng(Double.parseDouble(happy[39]), Double.parseDouble(happy[38])),
                    new LatLng(Double.parseDouble(happy[41]), Double.parseDouble(happy[40])),
                    new LatLng(Double.parseDouble(happy[43]), Double.parseDouble(happy[42])),
                    new LatLng(Double.parseDouble(happy[45]), Double.parseDouble(happy[44])),
                    new LatLng(Double.parseDouble(happy[47]), Double.parseDouble(happy[46])),
                    new LatLng(Double.parseDouble(happy[49]), Double.parseDouble(happy[48])),
                    new LatLng(Double.parseDouble(happy[51]), Double.parseDouble(happy[50])),
                    new LatLng(Double.parseDouble(happy[53]), Double.parseDouble(happy[52])),
                    new LatLng(Double.parseDouble(happy[55]), Double.parseDouble(happy[54])),
                    new LatLng(Double.parseDouble(happy[57]), Double.parseDouble(happy[56])),
                    new LatLng(Double.parseDouble(happy[59]), Double.parseDouble(happy[58])),
                    new LatLng(Double.parseDouble(happy[61]), Double.parseDouble(happy[60])),
                    new LatLng(Double.parseDouble(happy[63]), Double.parseDouble(happy[62])),
                    new LatLng(Double.parseDouble(happy[65]), Double.parseDouble(happy[64])),
                    new LatLng(Double.parseDouble(happy[67]), Double.parseDouble(happy[66])),
                    new LatLng(Double.parseDouble(happy[69]), Double.parseDouble(happy[68])),
                    new LatLng(Double.parseDouble(happy[71]), Double.parseDouble(happy[70])),
                    new LatLng(Double.parseDouble(happy[73]), Double.parseDouble(happy[72])),
                    new LatLng(Double.parseDouble(happy[75]), Double.parseDouble(happy[74])),
                    new LatLng(Double.parseDouble(happy[77]), Double.parseDouble(happy[76])),
                    new LatLng(Double.parseDouble(happy[79]), Double.parseDouble(happy[78])),
                    new LatLng(Double.parseDouble(happy[81]), Double.parseDouble(happy[80])),
                    new LatLng(Double.parseDouble(happy[83]), Double.parseDouble(happy[82])),
                    new LatLng(Double.parseDouble(happy[85]), Double.parseDouble(happy[84])),
                    new LatLng(Double.parseDouble(happy[87]), Double.parseDouble(happy[86])),
                    new LatLng(Double.parseDouble(happy[89]), Double.parseDouble(happy[88])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)


            ));
        }
        else
            path.setCoords(Arrays.asList(
                //    new LatLng(start2, start1),
                    new LatLng(Double.parseDouble(happy[1]), Double.parseDouble(happy[0])),
                    new LatLng(Double.parseDouble(happy[1+look]), Double.parseDouble(happy[0+look])),

                    new LatLng(Double.parseDouble(happy[1+2*look]), Double.parseDouble(happy[0+2*look])),

                    new LatLng(Double.parseDouble(happy[1+3*look]), Double.parseDouble(happy[0+3*look])),
                    new LatLng(Double.parseDouble(happy[1+4*look]), Double.parseDouble(happy[0+4*look])),

                    new LatLng(Double.parseDouble(happy[1+5*look]), Double.parseDouble(happy[0+5*look])),

                    new LatLng(Double.parseDouble(happy[1+6*look]), Double.parseDouble(happy[0+6*look])),

                    new LatLng(Double.parseDouble(happy[1+7*look]), Double.parseDouble(happy[0+7*look])),
                    new LatLng(Double.parseDouble(happy[1+8*look]), Double.parseDouble(happy[0+8*look])),

                    new LatLng(Double.parseDouble(happy[1+9*look]), Double.parseDouble(happy[0+9*look])),

                    new LatLng(Double.parseDouble(happy[1+10*look]), Double.parseDouble(happy[0+10*look])),
                    new LatLng(Double.parseDouble(happy[1+11*look]), Double.parseDouble(happy[0+11*look])),

                    new LatLng(Double.parseDouble(happy[1+12*look]), Double.parseDouble(happy[0+12*look])),
                    new LatLng(Double.parseDouble(happy[1+13*look]), Double.parseDouble(happy[0+13*look])),
                    new LatLng(Double.parseDouble(happy[1+14*look]), Double.parseDouble(happy[0+14*look])),

                    new LatLng(Double.parseDouble(happy[1+15*look]), Double.parseDouble(happy[0+15*look])),
                    new LatLng(Double.parseDouble(happy[1+16*look]), Double.parseDouble(happy[0+16*look])),

                    new LatLng(Double.parseDouble(happy[1+17*look]), Double.parseDouble(happy[0+17*look])),
                    new LatLng(Double.parseDouble(happy[1+18*look]), Double.parseDouble(happy[0+18*look])),
                    new LatLng(Double.parseDouble(happy[1+19*look]), Double.parseDouble(happy[0+19*look])),

         /*

                        new LatLng(Double.parseDouble(happy[1+20*look]), Double.parseDouble(happy[0+20*look])),
                        new LatLng(Double.parseDouble(happy[1+21*look]), Double.parseDouble(happy[0+21*look])),
                        new LatLng(Double.parseDouble(happy[1+22*look]), Double.parseDouble(happy[0+22*look])),
                        new LatLng(Double.parseDouble(happy[1+23*look]), Double.parseDouble(happy[0+23*look])),
                        new LatLng(Double.parseDouble(happy[1+24*look]), Double.parseDouble(happy[0+24*look])),
                        new LatLng(Double.parseDouble(happy[1+25*look]), Double.parseDouble(happy[0+25*look])),
                        new LatLng(Double.parseDouble(happy[1+26*look]), Double.parseDouble(happy[0+26*look])),
                        new LatLng(Double.parseDouble(happy[1+27*look]), Double.parseDouble(happy[0+27*look])),
                        new LatLng(Double.parseDouble(happy[1+28*look]), Double.parseDouble(happy[0+28*look])),
                        new LatLng(Double.parseDouble(happy[1+29*look]), Double.parseDouble(happy[0+29*look])),

                        new LatLng(Double.parseDouble(happy[1+30*look]), Double.parseDouble(happy[0+30*look])),
                        new LatLng(Double.parseDouble(happy[1+31*look]), Double.parseDouble(happy[0+31*look])),
                        new LatLng(Double.parseDouble(happy[1+32*look]), Double.parseDouble(happy[0+32*look])),
                        new LatLng(Double.parseDouble(happy[1+33*look]), Double.parseDouble(happy[0+33*look])),
                        new LatLng(Double.parseDouble(happy[1+34*look]), Double.parseDouble(happy[0+34*look])),
                        new LatLng(Double.parseDouble(happy[1+35*look]), Double.parseDouble(happy[0+35*look])),
                        new LatLng(Double.parseDouble(happy[1+36*look]), Double.parseDouble(happy[0+36*look])),
                        new LatLng(Double.parseDouble(happy[1+37*look]), Double.parseDouble(happy[0+37*look])),
                        new LatLng(Double.parseDouble(happy[1+38*look]), Double.parseDouble(happy[0+38*look])),
                        new LatLng(Double.parseDouble(happy[1+39*look]), Double.parseDouble(happy[0+39*look])),
                        new LatLng(Double.parseDouble(happy[1+40*look]), Double.parseDouble(happy[0+40*look])),
                        new LatLng(Double.parseDouble(happy[1+41*look]), Double.parseDouble(happy[0+41*look])),
                        new LatLng(Double.parseDouble(happy[1+42*look]), Double.parseDouble(happy[0+42*look])),
                        new LatLng(Double.parseDouble(happy[1+43*look]), Double.parseDouble(happy[0+43*look])),
                        new LatLng(Double.parseDouble(happy[1+44*look]), Double.parseDouble(happy[0+44*look])),
                        new LatLng(Double.parseDouble(happy[1+45*look]), Double.parseDouble(happy[0+45*look])),
                        new LatLng(Double.parseDouble(happy[1+46*look]), Double.parseDouble(happy[0+46*look])),
                        new LatLng(Double.parseDouble(happy[1+47*look]), Double.parseDouble(happy[0+47*look])),
                        new LatLng(Double.parseDouble(happy[1+48*look]), Double.parseDouble(happy[0+48*look])),
                        new LatLng(Double.parseDouble(happy[1+49*look]), Double.parseDouble(happy[0+49*look])),

                        new LatLng(Double.parseDouble(happy[1+50*look]), Double.parseDouble(happy[0+50*look])),
                        new LatLng(Double.parseDouble(happy[1+51*look]), Double.parseDouble(happy[0+51*look])),
                        new LatLng(Double.parseDouble(happy[1+52*look]), Double.parseDouble(happy[0+52*look])),
                        new LatLng(Double.parseDouble(happy[1+53*look]), Double.parseDouble(happy[0+53*look])),
                        new LatLng(Double.parseDouble(happy[1+54*look]), Double.parseDouble(happy[0+54*look])),
                        new LatLng(Double.parseDouble(happy[1+55*look]), Double.parseDouble(happy[0+55*look])),
                        new LatLng(Double.parseDouble(happy[1+56*look]), Double.parseDouble(happy[0+56*look])),
                        new LatLng(Double.parseDouble(happy[1+57*look]), Double.parseDouble(happy[0+57*look])),
                        new LatLng(Double.parseDouble(happy[1+58*look]), Double.parseDouble(happy[0+58*look])),
                        new LatLng(Double.parseDouble(happy[1+59*look]), Double.parseDouble(happy[0+59*look])),
                        new LatLng(Double.parseDouble(happy[1+60*look]), Double.parseDouble(happy[0+60*look])),
                        new LatLng(Double.parseDouble(happy[1+61*look]), Double.parseDouble(happy[0+61*look])),
                        new LatLng(Double.parseDouble(happy[1+62*look]), Double.parseDouble(happy[0+62*look])),
                        new LatLng(Double.parseDouble(happy[1+63*look]), Double.parseDouble(happy[0+63*look])),
                        new LatLng(Double.parseDouble(happy[1+64*look]), Double.parseDouble(happy[0+64*look])),
                        new LatLng(Double.parseDouble(happy[1+65*look]), Double.parseDouble(happy[0+65*look])),
                        new LatLng(Double.parseDouble(happy[1+66*look]), Double.parseDouble(happy[0+66*look])),
                        new LatLng(Double.parseDouble(happy[1+67*look]), Double.parseDouble(happy[0+67*look])),
                        new LatLng(Double.parseDouble(happy[1+68*look]), Double.parseDouble(happy[0+68*look])),
                        new LatLng(Double.parseDouble(happy[1+69*look]), Double.parseDouble(happy[0+69*look])),

                        new LatLng(Double.parseDouble(happy[1+70*look]), Double.parseDouble(happy[0+70*look])),
                        new LatLng(Double.parseDouble(happy[1+71*look]), Double.parseDouble(happy[0+71*look])),
                        new LatLng(Double.parseDouble(happy[1+72*look]), Double.parseDouble(happy[0+72*look])),
                        new LatLng(Double.parseDouble(happy[1+73*look]), Double.parseDouble(happy[0+73*look])),
                        new LatLng(Double.parseDouble(happy[1+74*look]), Double.parseDouble(happy[0+74*look])),
                        new LatLng(Double.parseDouble(happy[1+75*look]), Double.parseDouble(happy[0+75*look])),
                        new LatLng(Double.parseDouble(happy[1+76*look]), Double.parseDouble(happy[0+76*look])),
                        new LatLng(Double.parseDouble(happy[1+77*look]), Double.parseDouble(happy[0+77*look])),
                        new LatLng(Double.parseDouble(happy[1+78*look]), Double.parseDouble(happy[0+78*look])),
                        new LatLng(Double.parseDouble(happy[1+79*look]), Double.parseDouble(happy[0+79*look])),
                        new LatLng(Double.parseDouble(happy[1+80*look]), Double.parseDouble(happy[0+80*look])),
                        new LatLng(Double.parseDouble(happy[1+81*look]), Double.parseDouble(happy[0+81*look])),
                        new LatLng(Double.parseDouble(happy[1+82*look]), Double.parseDouble(happy[0+82*look])),
                        new LatLng(Double.parseDouble(happy[1+83*look]), Double.parseDouble(happy[0+83*look])),
                        new LatLng(Double.parseDouble(happy[1+84*look]), Double.parseDouble(happy[0+84*look])),
                        new LatLng(Double.parseDouble(happy[1+85*look]), Double.parseDouble(happy[0+85*look])),
                        new LatLng(Double.parseDouble(happy[1+86*look]), Double.parseDouble(happy[0+86*look])),
                        new LatLng(Double.parseDouble(happy[1+87*look]), Double.parseDouble(happy[0+87*look])),
                        new LatLng(Double.parseDouble(happy[1+88*look]), Double.parseDouble(happy[0+88*look])),
                        new LatLng(Double.parseDouble(happy[1+89*look]), Double.parseDouble(happy[0+89*look])),

                        new LatLng(Double.parseDouble(happy[1+90*look]), Double.parseDouble(happy[0+90*look])),
                        new LatLng(Double.parseDouble(happy[1+91*look]), Double.parseDouble(happy[0+91*look])),
                        new LatLng(Double.parseDouble(happy[1+92*look]), Double.parseDouble(happy[0+92*look])),
                        new LatLng(Double.parseDouble(happy[1+93*look]), Double.parseDouble(happy[0+93*look])),
                        new LatLng(Double.parseDouble(happy[1+94*look]), Double.parseDouble(happy[0+94*look])),
                        new LatLng(Double.parseDouble(happy[1+95*look]), Double.parseDouble(happy[0+95*look])),
                        new LatLng(Double.parseDouble(happy[1+96*look]), Double.parseDouble(happy[0+96*look])),
                        new LatLng(Double.parseDouble(happy[1+97*look]), Double.parseDouble(happy[0+97*look])),
                        new LatLng(Double.parseDouble(happy[1+98*look]), Double.parseDouble(happy[0+98*look])),
                        new LatLng(Double.parseDouble(happy[1+99*look]), Double.parseDouble(happy[0+99*look])),
                        */


                    //new LatLng(Double.parseDouble(happy[happy.length-1-2*look]), Double.parseDouble(happy[happy.length-2-2*look])),
                    new LatLng(Double.parseDouble(happy[happy.length-1-1*look]), Double.parseDouble(happy[happy.length-2-1*look])),
                    new LatLng(Double.parseDouble(happy[happy.length-1]), Double.parseDouble(happy[happy.length-2])),
                    new LatLng(lalatitude, lalongtitude)

            ));
        System.out.println("sibal");

        path.setColor(Color.RED);
        path.setMap(naverMap);
    }






}