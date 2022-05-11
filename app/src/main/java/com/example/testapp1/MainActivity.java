package com.example.testapp1;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView; //카카오 맵뷰 임포트

import android.util.Log;
import android.view.View;
import android.view.ViewGroup; //뷰 그룹 임포트
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import net.daum.mf.map.api.MapPOIItem;

import android.widget.Toast;
import android.widget.Button;




public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {


    private static final String LOG_TAG = "MainActivity";

    public double lat;
    public double lng;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private int ButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = new MapView(this);


        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view); //뷰그룹 사용
        mapViewContainer.addView(mapView);

        mapView.setMapViewEventListener(this);



        //트래킹 모드
        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        //Searchparser(테스트)
        SearchParser searchParser = new SearchParser();


        //Coordparse (테스트)
        CoordParser apiData = new CoordParser();


        //PolyLineSeter클래스
        PolylineSeter polylineSeter = new PolylineSeter();


        //Achieve
        Achieve achieve = new Achieve();
        achieve.Information(mapView);

        //텍스트뷰 (테스트)
        TextView textView1 = (TextView) findViewById(R.id.text1);

        //검색(SearchView) 테스트
        SearchView searchView = findViewById(R.id.SearchView1);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ArrayList<SearchCoord> scData = searchParser.getCoord(s);
                ArrayList<CoorData> dataArr = apiData.getData(scData.get(0).SearchHelper());
                polylineSeter.set_poly(mapView, dataArr);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //button
        findViewById(R.id.location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button location = (Button) findViewById(R.id.location);

                if (ButtonCount % 3 == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("1").setMessage("현재위치");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                    mapView.setShowCurrentLocationMarker(true);
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                    } else {
                        checkRunTimePermission();
                    }
                    location.setBackgroundResource(R.drawable.ic_baseline_my_location_24);


                } else if (ButtonCount % 3 == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("2").setMessage("현재위치와방향");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                    } else {
                        checkRunTimePermission();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("3").setMessage("지도중심");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                    mapView.setShowCurrentLocationMarker(false);
                    location.setBackgroundResource(R.drawable.ic_baseline_location_searching_24);
                }
                ButtonCount++;
            }

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeAllViews();
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        lat = mapPointGeo.latitude;
        lng = mapPointGeo.longitude;
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }


    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }

    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {


            boolean check_result = true;


            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음

            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 이미 퍼미션있는경우

        } else {  //퍼미션요청
            // 퍼미션 거부된경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 퍼미션 거부한적없는경우 퍼미션요청
                // 요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
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
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
class Achieve {
    //lat 가져다 쓰고 싶은 클래스
    //+-0.0002
    MainActivity a = new MainActivity();
    double lat1 = a.lat;
    double lng1 = a.lng;


    //정상 좌표 (임시)
    double startlat=37.88168;
    double startlng=127.73467;
    double endlat=37.88168;
    double endlng=127.73467;

    //업적확인
    public void AchieveCheck() {

        if (lat1 >= startlat-0.003  && lat1 <= startlat+0.003 ){
            if (lng1 >= startlng-0.003  && lng1 <= startlng+0.003 ){
                //등산 시작
                double startinglat = lat1;
                double startinglng = lng1;





            }

        }

    }
    //산 정보
    public void Information(MapView mapView) {
        //setPOIItemEventListener 마커클릭시 이벤트
        MapPOIItem marker = new MapPOIItem();
        MapPoint MARKER_POINT1 = MapPoint.mapPointWithGeoCoord(37.88937, 127.73469);
        marker.setItemName("봉의산");
        marker.setTag(0);
        marker.setMapPoint(MARKER_POINT1);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
    }
}





class SearchCoord {
    String x;
    String y;
    public String SearchHelper() {
        Double minX = Double.parseDouble(x)-0.01;
        Double maxX = Double.parseDouble(x)+0.01;
        Double minY = Double.parseDouble(y)-0.01;
        Double maxY = Double.parseDouble(y)+0.01;
        String result = minX.toString()+","+minY.toString()+","+maxX.toString()+","+maxY.toString()+")";
        return result;
    }
}

class SearchParser {
    public ArrayList<SearchCoord> getCoord(String mName) {
        //return data 부분
        ArrayList<SearchCoord> searchCoords = new ArrayList<SearchCoord>();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    //요청 Url
                    String searchUrl = "https://api.vworld.kr/req/search?service=search&request=search&version=2.0&crs=EPSG:4326&size=10&page=1&type=place&format=xml&errorformat=xml&key=F931BD24-945F-3AA9-8CB7-853B5D40C5A8&query=" + mName;
                    URL url = new URL(searchUrl);
                    InputStream is = url.openStream();

                    //xmlParser 생성
                    XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = xmlFactory.newPullParser();
                    parser.setInput(is,"utf-8");

                    //xml과 관련된 변수들
                    boolean isTitle = false;
                    boolean isThat = false;
                    boolean isFirst = true;
                    boolean isCate = false;
                    boolean isMount = false;
                    boolean isX = false;
                    boolean isY = false;
                    String X = "";
                    String Y = "";
                    String Title = "";
                    String Cate = "";

                    // 파싱 시작
                    while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        int type = parser.getEventType();
                        SearchCoord sc = new SearchCoord();

                        //태그 검사(태그가 gml:posList인 경우 찾기)
                        if(type == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("category")) {
                                isCate = true;
                            }
                            else if(parser.getName().equals("title")){
                                isTitle = true;
                            }
                            else if(parser.getName().equals("x")){
                                isX = true;
                            }
                            else if(parser.getName().equals("y")){
                                isY = true;
                            }
                        }

                        else if(type == XmlPullParser.TEXT) { //텍스트확인
                            if(isTitle){ //태그가 타이틀일 때
                                if(isFirst){
                                    Title = parser.getText();
                                }
                                if(parser.getText()==Title){
                                    isThat = true;
                                }
                                isTitle = false;
                            }
                            else if(isCate) { //태그가 카테고리일때
                                if(isFirst){
                                    Cate = parser.getText();
                                    isFirst = false;
                                }
                                if(parser.getText()==Cate){//데이터 분류가 산이라면
                                    isMount = true; //이 데이터가 산임을 명시
                                }
                                isCate = false; //카테고리 태그 끝
                            }
                            else if(isX) { //태그가 x일 때
                                if(isThat && isMount){ //데이터가 입력한 산이라면
                                    X = parser.getText(); //x좌표 X에 저장
                                }
                                isX = false; //x 태그 끝
                            }
                            else if(isY) { //태그가 y일 때
                                if(isThat && isMount) { //데이터가 입력한 산이라면
                                    Y = parser.getText(); // y좌표 Y에 저장
                                }
                                isY = false; //y 태그 끝
                            }
                        }

                        // y태그가 끝날 때데이터 추가 ()
                        else if(type == XmlPullParser.END_TAG && parser.getName().equals("y") && isThat && isMount) {
                            sc.x = X;
                            sc.y = Y;
                            searchCoords.add(sc);
                            isThat = false; // 산 데이터에 대한 x,y 좌표 저장 끝났으므로
                            isMount = false; //isThat과 isMount 초기화
                        }


                        type = parser.next();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return searchCoords;
    }
}


class CoorData {
    String[] Coords;
}

class CoordParser {
    public ArrayList<CoorData> getData(String boxData) {
        //return data 부분
        ArrayList<CoorData> dataArr = new ArrayList<CoorData>();
        Thread t = new Thread() {
            @Override
            public  void run() {
                try {
                    //요청 Url
                    String fullurl = "https://api.vworld.kr/req/data?service=data&version=2.0&request=getfeature&key=F931BD24-945F-3AA9-8CB7-853B5D40C5A8&domain=http://localhost:8080&format=xml&data=LT_L_FRSTCLIMB&crs=epsg:4326&geomfilter=BOX("+boxData;
                    URL url = new URL(fullurl);
                    InputStream is = url.openStream();

                    //xmlParser 생성
                    XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = xmlFactory.newPullParser();
                    parser.setInput(is,"utf-8");

                    //xml과 관련된 변수들
                    boolean isCoords = false;
                    String Coords = "";

                    // 파싱 시작
                    while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        int type = parser.getEventType();
                        CoorData data = new CoorData();

                        //태그 검사(태그가 gml:posList인 경우 찾기)
                        if(type == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("gml:posList")) {
                              isCoords = true;
                            }
                        }
                        //텍스트 확인 (Coords에 텍스트 임시 저장)
                        else if(type == XmlPullParser.TEXT) {
                            if(isCoords) {
                                Coords = parser.getText();
                                isCoords = false;
                            }
                        }
                        // 데이터 추가 (Coords데이터 공백으로 스플릿하여 저장)
                        else if(type == XmlPullParser.END_TAG && parser.getName().equals("gml:posList")) {
                            data.Coords = Coords.split(" ");

                            dataArr.add(data);
                        }

                        type = parser.next();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return dataArr;
    }
}

class PolylineSeter {
    public void set_poly(MapView mapView, ArrayList<CoorData> dataArr) {
        // Polyline 좌표 지정.
        mapView.removeAllPolylines();
        for(int i=0; i<dataArr.size(); i++) { // 매 CoorData마다 폴리라인 객체 생성
            MapPolyline polyline = new MapPolyline();
            polyline.setLineColor(Color.argb(128, i*10, 51, 0));
            polyline.setTag(1000);
            for(int x=0; x<dataArr.get(i).Coords.length/2; x++) {
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(dataArr.get(i).Coords[x*2+1]), Double.parseDouble(dataArr.get(i).Coords[x*2])));
            }
            mapView.addPolyline(polyline); // 폴리라인 객체 지도에 올리기
        }

        // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        mapView.fitMapViewAreaToShowAllPolylines();
    }
}