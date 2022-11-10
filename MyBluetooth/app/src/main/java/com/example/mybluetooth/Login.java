package com.example.mybluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText username;
    EditText password;

    // Retrofit, API 백엔드 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.ID_input);
        password = findViewById(R.id.PS_input);
    }

    public void onSignUpBtnClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }

    public void onLoginBtnClicked(View v) {
        // 백엔드과 공동 작업 : 서버에서 회원 있는지 확인하여 로그인
    }

    public void onTestBtnClicked(View v) { // 테스트를 위한 임시 버튼
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Login", "Yunseong");
        username.setText("");
        password.setText("");
        startActivity(intent);
    }
}