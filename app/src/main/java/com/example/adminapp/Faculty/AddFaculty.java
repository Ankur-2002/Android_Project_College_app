package com.example.adminapp.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adminapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddFaculty extends AppCompatActivity {

    private ImageView imageView;
    private Spinner spinner;
    private EditText Name,post,email;
    private Button button;
    private String category[] = {"Select category","Computer science","Mechanical Engineering", "Electrical Engineering"};
    private byte Req = 1;
    private Bitmap bitmap;
    private Uri uri;
    private AlertDialog alertDialog;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    String TeacherCategory;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        spinner = findViewById(R.id.Category);
        Name = findViewById(R.id.Addteachername);
        post = findViewById(R.id.AddteacherPost);
        button = findViewById(R.id.Addteacher);
        email = findViewById(R.id.AddteacherEmail);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
        imageView = findViewById(R.id.teacher);
        spinner.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,category));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Req);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TeacherCategory = category[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap == null) {
                    Toast.makeText(getApplicationContext(), "Add teacher Image", Toast.LENGTH_SHORT).show();
                }
                else if(TeacherCategory == category[0])
                {
                    Toast.makeText(getApplicationContext(), "Add teacher category", Toast.LENGTH_SHORT).show();
                }
                else if(Name.getText().toString().isEmpty()){
                    Name.setError("Required");
                }
                else if(post.getText().toString().isEmpty())
                {
                    post.setError("Required");
                }else if(email.getText().toString().isEmpty()){
                    email.setError("Required");
                }else {
                    Upload();
                }

            }
        });
    }

    private void Upload() {
        databaseReference = databaseReference.child("faculty").child(TeacherCategory);
        String key = databaseReference.push().getKey();
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        byte [] image = byteArrayOutputStream.toByteArray();
        StorageReference path = storageReference.child("faculty").child(key+"jpg");
        path.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                    UploadData(String.valueOf(uri),key);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                
            }
        });
    }

    private void UploadData(String  url,String key) {


        Teacher_data data1 = new Teacher_data(Name.getText().toString(),post.getText().toString(),TeacherCategory,url,key,email.getText().toString());
        databaseReference.child(key).setValue(data1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
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

        databaseReference = databaseReference.getRoot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        if(requestCode == Req && resultCode == RESULT_OK){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                imageView.setImageURI(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}