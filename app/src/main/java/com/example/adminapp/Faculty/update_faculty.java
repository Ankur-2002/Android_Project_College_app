package com.example.adminapp.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adminapp.R;
import com.example.adminapp.UploadImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class update_faculty extends AppCompatActivity {
    private Button button,button1;
    private TextView Name,post,email;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String category;
    private ProgressDialog pd;
    private  Uri uri;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);
        button = findViewById(R.id.deleteTeacher);
        button1 = findViewById(R.id.SaveTeacher);
        Name = findViewById(R.id.teacherName);
        post = findViewById(R.id.teacherPost);
        email = findViewById(R.id.teacherEmail);
        imageView = findViewById(R.id.teacher_image);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        pd = new ProgressDialog(this);
        Name.setText(intent.getCharSequenceExtra("name"));
        post.setText(intent.getCharSequenceExtra("post"));
        email.setText(intent.getCharSequenceExtra("email"));
        category = (String) intent.getCharSequenceExtra("category");
        String key = (String) intent.getCharSequenceExtra("key");
        Glide.with(this).load(Uri.parse((String) intent.getCharSequenceExtra("image"))).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage(key);
                pd.show();
                storageReference = storageReference.child("faculty");
                storageReference.child(key+"jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        DeleteData(key);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                storageReference = storageReference.getRoot();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Checkvalidation()){
                    return ;
                }
                pd.setMessage("Uploading...");
                pd.show();
                if(bitmap != null)
                 UploadData(key);
                else
                    Uploadtodatabase(key,null);
            }
        });
    }
    public void DeleteData(String key){
        databaseReference = databaseReference.child("faculty").child(category);

        databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "User Delete Successfully", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(),ShowFaculty.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });
    }
    public boolean Checkvalidation() {
        if (Name.getText().toString().isEmpty() || post.getText().toString().isEmpty() || email.getText().toString().isEmpty())
        {       Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
        return false;
        }
        return true;
    }
    public void Upload(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
                uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageURI(uri);

        }
    }
    public void UploadData(String key){
         ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
       byte[] image = byteArrayOutputStream.toByteArray();

        storageReference = storageReference.child("faculty");

        storageReference.child(key+"jpg").putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key+"jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Uploadtodatabase(key,uri);
                }
            })     ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void Uploadtodatabase (String key,Uri uri){
        String image ;
        if(uri == null){
            image = (String) getIntent().getCharSequenceExtra("image");
        }else {
            image = uri.toString();
        }
        pd.dismiss();
        databaseReference = databaseReference.child("faculty").child(category);
        Teacher_data data = new Teacher_data(Name.getText().toString(),post.getText().toString(),category,image,key,email.getText().toString());
        databaseReference.child(key).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Intent intent1 = new Intent(getApplicationContext(),ShowFaculty.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference = databaseReference.getRoot();
    }
}