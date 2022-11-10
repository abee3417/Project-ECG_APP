
package com.example.mybluetooth;

import androidx.appcompat.app.AppCompatActivity;

        import android.util.Log;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.SeekBar;
        import android.widget.SeekBar.OnSeekBarChangeListener;
        import android.widget.Toast;

        import android.graphics.Color;

import java.util.ArrayList;
    /*
        import com.github.mikephil.charting.charts.LineChart;
        import com.github.mikephil.charting.data.Entry;
        import com.github.mikephil.charting.data.LineData;
        import com.github.mikephil.charting.data.LineDataSet;

        import java.util.ArrayList;
        import java.util.HashMap;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;

     */

public class ShowRecordGraph extends AppCompatActivity {



    // private LineChart lineChart;
    // private SeekBar seekBar;
    private String username;
    ArrayList<Integer> data = new ArrayList<>(1024);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record_graph);

        Intent intent = getIntent();
        data = intent.getIntegerArrayListExtra("ECG DATA");
        username = intent.getStringExtra("Username");

        /*
        ArrayList<Entry> entry_chart1 = new ArrayList<>(); // 데이터를 담을 Arraylist

        lineChart = (LineChart) findViewById(R.id.chart2);

        LineData chartData = new LineData(); // 차트에 담길 데이터


        for(int i=0; i<512; i++) {
            int tmp = data.get(i);
            if(tmp!=0)
                entry_chart1.add(new Entry(i+1, tmp));
        }



        LineDataSet lineDataSet1 = new LineDataSet(entry_chart1, "LineGraph1"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.

        lineDataSet1.setColor(Color.RED); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.

        lineDataSet1.setDrawValues(!lineDataSet1.isDrawValuesEnabled());
        lineDataSet1.setDrawCircles(false);

        chartData.addDataSet(lineDataSet1); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        lineChart.setData(chartData); // 차트에 위의 DataSet을 넣는다.


        lineChart.invalidate(); // 차트 업데이트
        //lineChart.setScaleEnabled(true);
        //lineChart.setVisibleXRangeMaximum(100);
        lineChart.setVisibleXRange(50,150);
        //lineChart.setDragXEnabled();
        lineChart.setPinchZoom(true);
        //lineChart.setTouchEnabled(false); // 차트 터치 disable


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int past_xscale=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                past_xscale=50+seekBar.getProgress();
                System.out.print("11: ");
                System.out.println(50+seekBar.getProgress());
                lineChart.setVisibleXRange(50+seekBar.getProgress(),150);
                lineChart.resetTracking();
                lineChart.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println(+seekBar.getProgress());
                System.out.print("22: ");
                System.out.println(50+seekBar.getProgress());

                lineChart.setVisibleXRange(50+seekBar.getProgress(),150);
                //lineChart.setVisibleXRangeMaximum(50+seekBar.getProgress());
                lineChart.resetTracking();
                lineChart.invalidate();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

         */


    }
    public void onBack2BtnClicked(View v) { finish(); }
}