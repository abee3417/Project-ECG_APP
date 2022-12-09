package com.example.polygraph;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Login extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText newEmail;
    private EditText newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("myecg");

        newEmail = findViewById(R.id.ID_input);
        newPassword = findViewById(R.id.PS_input);



        Button btn_login = findViewById(R.id.login_Btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //로그인
                String strEmail = newEmail.getText().toString();
                String strPwd = newPassword.getText().toString();


                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //로그인 성공
                            Toast.makeText(Login.this, "로그인 성공",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(Login.this, "로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        Button btn_register = findViewById(R.id.signUp_Btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 화면으로 이동
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}
