package com.example.testapp1;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;

import java.util.ArrayList;


public class AchieveDialog extends Activity {


    private TextView textView_time;
    private TextView textView_kcal;
    private TextView textView_achievename;
    private TextView textView_test;



    TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.achieve);




        Intent intent =getIntent();

        double startln = intent.getExtras().getDouble("startlng");
        double startla = intent.getExtras().getDouble("startlat");
        double endln = intent.getExtras().getDouble("endlng");
        double endla = intent.getExtras().getDouble("endlat");

        long startdate = intent.getExtras().getLong("startdate");
        long enddate = intent.getExtras().getLong("enddate");

        long time = (enddate-startdate)/60000;
        long kcal = (time*7);


        ArrayList<String> list = new ArrayList<>();
        list.add("봉의산1");
        list.add("봉의산2");
        list.add("봉의산3");
        list.add("봉의산4");
        list.add("봉의산5");

//업적확인
 /*
        int userachieve = 0;

        for (int i=0; i<=db_mountain; i++) {
            if (startln = db_mountain(i) && startla =db_mountain(i)) {
                if (startln = db_mountain(i) && startla =db_mountain(i)){
                    userachieve = i;


                }
            }
        }

 */


        //UI객체생성
        textView_achievename = (TextView)findViewById(R.id.dialog_default_txtview0);


        textView_achievename.setText("업적 달성 : " + "achieve"  );

        textView_time = (TextView)findViewById(R.id.dialog_default_txtview2);
        textView_time.setText("소요 시간 : " + time + "분");

        textView_kcal = (TextView)findViewById(R.id.dialog_default_txtview3);
        textView_kcal.setText("소모 칼로리(420kcal/h) : " + kcal + "kcal");




    }
    //확인버튼클릭
    public void mOnclose (View v){
        //데이터전달하기
        //Intent intent=new Intent();
        // intent.putExtra("result", "Close Popup");
        //setResult(RESULT_OK, intent);
        //액티비티(팝업) 닫기
        finish();

    }


    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    public void onBackPressed(){
        return;
    }


}