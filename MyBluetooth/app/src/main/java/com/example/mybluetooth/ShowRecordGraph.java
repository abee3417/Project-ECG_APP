
package com.example.mybluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;

        /*
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;
        */

public class ShowRecordGraph extends AppCompatActivity {

    private LineChart lineChart;
    private String username;
    ArrayList<Integer> data = new ArrayList<>(1024);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record_graph);

        Intent intent = getIntent();
        data = intent.getIntegerArrayListExtra("OldData");
        username = intent.getStringExtra("Username");

        ArrayList<Entry> entries = new ArrayList<>(); // 차트 데이터 리스트
        lineChart = (LineChart) findViewById(R.id.oldchart);
        LineData chartData = new LineData(); // 차트 데이터

        for(int i = 0; i < 1024; i++) { // 차트 데이터 리스트에 data를 add
            int tmp = data.get(i);
            if(tmp != 0) {
                entries.add(new Entry(i + 1, tmp));
            }
        }

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
    public void onBack2BtnClicked(View v) { finish(); }

}