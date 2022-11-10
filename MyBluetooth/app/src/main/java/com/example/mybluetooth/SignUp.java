package com.example.mybluetooth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    //GUI COMPONENTS
    private EditText newEmail;
    private EditText newPassword;
    private EditText newConfirmPassword;

    // Retrofit, API 백엔드 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        newEmail = findViewById(R.id.email_Input);
        newPassword = findViewById(R.id.PS_Input);
        newConfirmPassword = findViewById(R.id.confirm_PS_Input);

    }

    public void onSignupBackBtnClicked(View v) {
        finish();
    }

    public void onRegisterBtnClicked(View v) {
        if(newPassword.getText().toString().equals(newConfirmPassword.getText().toString())) {
            HashMap<String, Object> input = new HashMap<>();
            input.put("email", newEmail.getText().toString());
            input.put("password", newPassword.getText().toString());

            // 백엔드와 공동 작업 : 서버에 회원 정보 저장

        }
        else {
            Toast.makeText(getApplicationContext(),"비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
        }
    }
}