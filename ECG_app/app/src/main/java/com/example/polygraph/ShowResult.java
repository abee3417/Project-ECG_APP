package com.example.polygraph;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;




import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;

public class ShowResult extends AppCompatActivity {

    private String username;
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private FirebaseFirestore mFireStore;
    private String newtmp = "70bpm";
    private String newlie = "lie";
    private String new_id;
    private String memo;

    private String runtime_str; // 총 측정 시간이 담긴 데이터
    private String lieP_str; // 심박 이상 횟수가 담긴 데이터

    private EditText memo_text; // 메모 표시
    private TextView runtime; // 측정시간 표시
    private TextView lieP; // 거짓말 확률 표시
    private ListView result_list; // 거짓말 시점을 보여주는 리스트

    Date date_now = new Date(System.currentTimeMillis());

    ArrayList<Integer> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int count = 0;
        int sec1 = 0;
        int sec2 = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lie_detector);

        Intent intent = getIntent();
        data = intent.getIntegerArrayListExtra("Data");
        username = intent.getStringExtra("Username");

        runtime = (TextView) findViewById(R.id.runtime_Text);
        lieP = (TextView) findViewById(R.id.nums_of_Lies);

        // 거짓말 시점을 리스트로 표현해주기 위한 ListView
        result_list = (ListView) findViewById(R.id.resultList);
        ArrayList<String> tmp = new ArrayList<>();
        ArrayAdapter<String> tmpAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tmp);
        result_list.setAdapter(tmpAdapter);

        if (data.size() > 1){
            for (int i = 0; i < data.size() - 1; i++){
                sec1 = data.get(i) / 1000;
                sec2 = data.get(i) % 1000;
                String tmp1 = Integer.toString(sec1) + "초 ";
                String tmp2 = Integer.toString(sec2);
                tmp.add(tmp1 + tmp2);
                count++;
            }
            tmpAdapter.notifyDataSetChanged();
            // 측정시간 저장, 출력
            lieP_str = Integer.toString(count);
            lieP.setText(lieP_str + "번");
        }
        else{ // 이상 심박횟수가 없을 경우 정상출력
            lieP_str = "0";
            lieP.setText("정상입니다!");
        }

        // 횟수 체크 표시 이후에 저장
        count = data.size() - 1; // 마지막 데이터는 측정시간
        sec1 = data.get(count) / 1000;
        sec2 = data.get(count) % 1000;

        runtime_str = Integer.toString(sec1) + "초 " + Integer.toString(sec2);
        runtime.setText(runtime_str);
        String save_runtime_str = Integer.toString(data.get(count));
        //runtime_str = save_runtime_str;

        // 메모 작성, 저장 코드
        memo_text = findViewById(R.id.memo_input);
        memo = memo_text.getText().toString();

        mFirebaseAuth = FirebaseAuth.getInstance();



    }

    public void setup(){
        mFireStore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFireStore.setFirestoreSettings(settings);
    }



    public void onSaveBtnClicked(View v) {
        HashMap<String, Object> input = new HashMap<>();
        input.put("ecg", data.toString());
        input.put("ecg_user", username);
        Log.d("TEST", data.toString());
        // 메모 작성, 저장 코드
        memo_text = findViewById(R.id.memo_input);
        memo = memo_text.getText().toString();

        setup();
        UserData data = new UserData();
        data.setMemo(memo);
        data.setLie(lieP_str);
        data.setUserTime(runtime_str);
        data.setUid(mFirebaseAuth.getUid());
        mFireStore.collection("User").document("doc").collection("data").document().set(data);
        Toast.makeText(ShowResult.this, "저장 성공",Toast.LENGTH_SHORT).show();




    }

    public void onBackBtnClicked(View v) { finish(); }

}