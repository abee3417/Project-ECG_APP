package com.example.polygraph;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    //GUI COMPONENTS
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText newEmail;
    private EditText newPassword;
    private Button newRegister;
    private FirebaseFirestore mFireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("myecg");

        newEmail = findViewById(R.id.email_Input);
        newPassword = findViewById(R.id.PS_Input);
        newRegister = findViewById(R.id.signup);

        newRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작
                String strEmail = newEmail.getText().toString();
                String strpwd = newPassword.getText().toString();


                //Firebase 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strpwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strpwd);

                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(SignUp.this, "성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, Login.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(SignUp.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


    }

    public void onSignupBackBtnClicked(View v) {
        finish();
    }

}