package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadImage extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private Spinner spinner;
    private CardView cardView;
    private String item[] =  {"Select Category","First","Second"};
    private Bitmap bitmap;
    private  Uri uri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String Category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        imageView = findViewById(R.id.UploadImageView);
        button = findViewById(R.id.UploadImageButton);
        spinner = findViewById(R.id.imageCategory);
        cardView = findViewById(R.id.AddImage);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,item)) ;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category = item[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }

        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap == null)
                    Toast.makeText(getApplicationContext(),"No image",Toast.LENGTH_LONG).show();
                else if(Category == item[0])
                    Toast.makeText(getApplicationContext(),"No category",Toast.LENGTH_LONG).show();
                else{
                    UploadImage();
                }
            }
        });
    }

    public void UploadImage(){
        progressDialog.setMessage("Waiting ....");
        progressDialog.show();
        ByteArrayOutputStream image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,image);
        byte finalImage[] = image.toByteArray();
        final StorageReference path;
        path = storageReference.child("Gallery").child(finalImage.toString()+"jpg");
        final UploadTask uploadTask = path.putBytes(finalImage);
//        Toast.makeText(getApplicationContext(),"finalImage",Toast.LENGTH_LONG).show();
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String paths = String.valueOf(uri);
                                    Upload(paths);
                                }
                            });
                        }
                    });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"No new ",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void Upload(String Download){
        databaseReference = databaseReference.child("Gallery");
        databaseReference = databaseReference.child(Category);
        final String key = databaseReference.push().getKey();

//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
//        String data = simpleDateFormat.format(calendar.getTime());
//        SimpleDateFormat time = new SimpleDateFormat("hh-mm-a");
//        String Time = time.format(calendar.getTime());
//        NoticeUpload noticeUpload = new NoticeUpload(Download,key,Category,data,Time);
        databaseReference.child(key).setValue(Download).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfull Upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference = databaseReference.getRoot();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1  && resultCode == RESULT_OK) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageURI(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}