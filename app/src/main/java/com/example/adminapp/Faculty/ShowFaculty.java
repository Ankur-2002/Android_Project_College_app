package com.example.adminapp.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.adminapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowFaculty extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private RecyclerView csDept,mechDept,elecDept;
    private LinearLayout csNodata,MeNodata,EeNodata;
    private DatabaseReference databaseReference;
    private List<Teacher_data> list,list1,list2,list3;
    private Teacher_adapter teacher_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_faculty);
        floatingActionButton = findViewById(R.id.AddButton);
        csDept = findViewById(R.id.Csdepartment);
        mechDept = findViewById(R.id.Mechanicaldepartment);
        elecDept = findViewById(R.id.Electricaldepartment);
        csNodata = findViewById(R.id.Csdepartment_no_data);
        MeNodata = findViewById(R.id.Mechanicaldepartment_no_data);
        EeNodata = findViewById(R.id.Electricaldepartment_no_data);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("faculty");
        LoadCsData();
        LoadMeData();
        LoadEleData();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddFaculty.class));
            }
        });

    }

    private void LoadCsData() {
        databaseReference = databaseReference.child("Computer science");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList();
                if(!snapshot.exists()){
                    csNodata.setVisibility(View.VISIBLE);
                    csDept.setVisibility(View.GONE);
                }else{
                    csNodata.setVisibility(View.GONE);
                    csDept.setVisibility(View.VISIBLE);

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                       Teacher_data d = dataSnapshot.getValue(Teacher_data.class);
                         list.add(d);
                    }
//                     csDept.setHasFixedSize(true);
                     csDept.setLayoutManager(new LinearLayoutManager(ShowFaculty.this));
//                    // Initialize Adapter
                     teacher_adapter = new Teacher_adapter(list,ShowFaculty.this);
//                    // Fill All details using adapter
                    try {
                        csDept.setAdapter(teacher_adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference = databaseReference.getParent();
    }
    private void LoadEleData() {
        databaseReference = databaseReference.child("Electrical Engineering");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1 = new ArrayList();
                if(!snapshot.exists()){
                    EeNodata.setVisibility(View.VISIBLE);
                    elecDept.setVisibility(View.GONE);
                }else{
                    EeNodata.setVisibility(View.GONE);
                    elecDept.setVisibility(View.VISIBLE);

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Teacher_data d = dataSnapshot.getValue(Teacher_data.class);
                        list1.add(d);
                    }
                    elecDept.setHasFixedSize(true);
                    elecDept.setLayoutManager(new LinearLayoutManager(ShowFaculty.this));
//                    // Initialize Adapter
                    teacher_adapter = new Teacher_adapter(list1,ShowFaculty.this);
//                    // Fill All details using adapter
                    try {
                        elecDept.setAdapter(teacher_adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference = databaseReference.getParent();
    }
    private void LoadMeData() {
        databaseReference = databaseReference.child("Mechanical Engineering");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2 = new ArrayList();
                if(!snapshot.exists()){
                    MeNodata.setVisibility(View.VISIBLE);
                    mechDept.setVisibility(View.GONE);
                }else{
                    MeNodata.setVisibility(View.GONE);
                    mechDept.setVisibility(View.VISIBLE);

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Teacher_data d = dataSnapshot.getValue(Teacher_data.class);
                        list2.add(d);
                    }
                    mechDept.setHasFixedSize(true);
                    mechDept.setLayoutManager(new LinearLayoutManager(ShowFaculty.this));
//                    // Initialize Adapter
                    teacher_adapter = new Teacher_adapter(list2,ShowFaculty.this);
//                    // Fill All details using adapter
                    try {
                        mechDept.setAdapter(teacher_adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     databaseReference = databaseReference.getParent();
    }
}