package com.example.polygraph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/*
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

 */


public class ShowRecord extends AppCompatActivity {

    private ListView record_list;
    //private FirebaseAuth mFirebaseAuth;

    private String Uid;
    //private FirebaseFirestore firestore;
    ArrayList<ArrayList<Integer>> history = new ArrayList<>(); //기록들을 담는 리스트
    ArrayList<String> record_name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);

        record_list = (ListView)findViewById(R.id.results);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, record_name);
        record_list.setAdapter(adapter);

        /*
        mFirebaseAuth = FirebaseAuth.getInstance();

        Uid = mFirebaseAuth.getUid();

        firestore = FirebaseFirestore.getInstance();

        record_list = (ListView)findViewById(R.id.results);


        CollectionReference productRef = firestore.collection("User").document("doc").collection("data");

        productRef.whereEqualTo("uid",""+Uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){

                        record_name.add(""+document.getData());
                        adapter.notifyDataSetChanged();

                    }
                }
                else{}
            }
        });

         */
    }



    public void onBack1BtnClicked(View v) {
        finish();
    }
}