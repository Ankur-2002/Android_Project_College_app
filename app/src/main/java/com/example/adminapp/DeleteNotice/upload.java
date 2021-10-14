package com.example.adminapp.DeleteNotice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adminapp.DeleteNotice.NoticeUpload;
import com.example.adminapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class upload extends AppCompatActivity {
    CardView cardView;
    ImageView imageView;
    TextInputEditText name;
    MaterialButton button;
    Uri uri;
    Bitmap bitmap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    String Download = "";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // firebase storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        cardView = findViewById(R.id.UploadItem);
        imageView = findViewById(R.id.NoticeImageView);
        progressDialog = new ProgressDialog(this);
        name = findViewById(R.id.NoticeTitle);
        button = findViewById(R.id.UploadButton);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty())
                {
                    name.setError("Add Image");
                    name.requestFocus();
                } else if(bitmap != null){
                    uploadImage();
                }else {
                    uploadData(null);

                }
            }
        });
    }

    private void uploadImage() {
        // Conversion of Image

        final String key = databaseReference.push().getKey();
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        byte[] finalImage =  byteArrayOutputStream.toByteArray();

        //  Reference of Firestore
        final StorageReference path;
        path =  storageReference.child("Notice").child(key+"jpg");

        // Enter the data in path
        final UploadTask uploadTask = path.putBytes(finalImage);

        uploadTask.addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Download = String.valueOf(uri);
                                    uploadData(key);
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private void uploadData(String key) {

        if(key == null){

            key = databaseReference.push().getKey();
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        String date = dateFormat.format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh-mm a");
        String time = dateFormat.format(calendar.getTime());

        NoticeUpload noticeUpload = new NoticeUpload(Download,key,name.getText().toString(),date,time);
        databaseReference.child("Notice").child(key).setValue(noticeUpload).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(),"SuccessFull uploaded",Toast.LENGTH_LONG);
                toast.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_LONG);
                toast.show();
            }
        });
        databaseReference = databaseReference.getParent();
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            uri = data.getData();
            imageView.setImageURI(uri);
            try {
               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}