package com.example.mybluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class ShowRecord extends AppCompatActivity {

    private String username;
    private ListView record_list;

    ArrayList<ArrayList<Integer>> history = new ArrayList<>(); //기록들을 담는 리스트
    ArrayList<String> record_name = new ArrayList<>();

    // Retrofit, API 백엔드 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);

        record_list = (ListView)findViewById(R.id.results);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, record_name);
        record_list.setAdapter(adapter);

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");

        // 백엔드와 공동 작업 : 서버에서 데이터리스트 가져오기

        record_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /* ShowRecordGraph 완성 후 주석해제
                Intent intent = new Intent(getApplicationContext(), ShowRecordGraph.class);
                intent.putExtra("OldData", history.get(position));
                startActivity(intent);

                */
            }
        });
    }
    
    public void onBack1BtnClicked(View v) {
        finish();
    }

    /*
    public void onTestBtnClicked(View v) { // 테스트를 위한 임시 버튼
        Intent intent = new Intent(getApplicationContext(), ShowRecordGraph.class);
        intent.putExtra("OldData", history);
        startActivity(intent);
    }

    */
}