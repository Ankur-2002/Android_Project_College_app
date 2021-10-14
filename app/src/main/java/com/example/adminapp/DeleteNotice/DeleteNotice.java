package com.example.adminapp.DeleteNotice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.adminapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DeleteNotice extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<NoticeUpload> list;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private NoticeAdapter adapter;
    private ProgressBar p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        recyclerView = findViewById(R.id.notices);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        p = findViewById(R.id.progressBar);
//        storageReference = FirebaseStorage.getInstance().getReference();
        LoadData();
    }
    public void LoadData(){
    databaseReference = databaseReference.child("Notice");
    databaseReference.addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list = new ArrayList<>();
            for (DataSnapshot snap: snapshot.getChildren()) {
                NoticeUpload data = snap.getValue(NoticeUpload.class);
                list.add(data);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter = new NoticeAdapter(DeleteNotice.this,list);
            recyclerView.setAdapter(adapter);
            p.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    databaseReference = databaseReference.getRoot();
    }
}
