package com.example.mybluetooth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;

public class ShowResult extends AppCompatActivity {

    private LineChart lineChart;
    private String username;
    ArrayList<Integer> data = new ArrayList<>(1024);

    // Retrofit, API 백엔드 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lie_detector);

        Intent intent = getIntent();
        data = intent.getIntegerArrayListExtra("Data");
        username = intent.getStringExtra("Username");

        ArrayList<Entry> entries = new ArrayList<>(); // 차트 데이터 리스트
        lineChart = (LineChart) findViewById(R.id.chart);
        LineData chartData = new LineData(); // 차트 데이터

        //test용 for문
        Random r = new Random();
        for(int i = 0; i < 1024; i++) { // 차트 데이터 리스트에 data를 add
            entries.add(new Entry(i + 1, r.nextInt(100)+1));
        }

        /*
        for(int i = 0; i < 1024; i++) { // 차트 데이터 리스트에 data를 add
            int tmp = data.get(i);
            if(tmp != 0) {
                entries.add(new Entry(i + 1, tmp));
            }
        }

         */

        // 차트 그래프 추가
        LineDataSet lineDataSet = new LineDataSet(entries, "Heartbeat");
        lineDataSet.setDrawValues(!lineDataSet.isDrawValuesEnabled());
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(Color.parseColor("#DB4455"));
        chartData.addDataSet(lineDataSet);

        // 차트 업데이트
        lineChart.setData(chartData);
        lineChart.setScaleEnabled(true);
        lineChart.setVisibleXRangeMaximum(100);
        lineChart.setVisibleXRange(50,150);
        lineChart.setDragXEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.invalidate();

    }

    public void onSaveBtnClicked(View v) {
        HashMap<String, Object> input = new HashMap<>();
        input.put("ecg", data.toString());
        input.put("ecg_user", username);
        Log.d("TEST", data.toString());

        // 백엔드와 공동 작업 : 서버에 그래프 저장
    }

    public void onBackBtnClicked(View v) { finish(); }

}