package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.adminapp.DeleteNotice.DeleteNotice;
import com.example.adminapp.DeleteNotice.upload;
import com.example.adminapp.Faculty.ShowFaculty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cardView,cardView1,cardView3,cardView4,cardView5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardView = findViewById(R.id.addevent);
        cardView1 = findViewById(R.id.updateimage);
        cardView3 = findViewById(R.id.uploadEbook);
        cardView5= findViewById(R.id.delete);
        cardView4 = findViewById(R.id.faculty);
        cardView.setOnClickListener(this);
        cardView1.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        cardView5.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.addevent :
                intent = new Intent(this, upload.class);
                startActivity(intent);
                break;
            case R.id.updateimage:
                intent = new Intent(this,UploadImage.class);
                startActivity(intent);
                break;
            case R.id.uploadEbook:
                intent = new Intent(this,UploadPdf.class);
                startActivity(intent);
                break;
            case R.id.faculty:
                intent = new Intent(this, ShowFaculty.class);
                startActivity(intent);
                break;
            case R.id.delete:
                intent = new Intent(this, DeleteNotice.class);
                startActivity(intent);
                break;
            default:
                return ;
        }
    }
}