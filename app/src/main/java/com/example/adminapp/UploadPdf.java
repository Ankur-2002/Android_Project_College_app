package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class UploadPdf extends AppCompatActivity {

    private CardView cardView;
    private Button button;
    private EditText text;
    private TextView textView;
    // database Related
    private DatabaseReference databaseReference;
    private StorageReference reference;
    private ProgressDialog pd;
    private String name;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        databaseReference = FirebaseDatabase.getInstance().getReference();;
        reference = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(this);
        cardView = findViewById(R.id.AddEbook);
        text = findViewById(R.id.pdfTitle);
        textView = findViewById(R.id.pdfname);
        button = findViewById(R.id.UploadEbookButton);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf/*");
                    startActivityForResult(intent,1);
                }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.length() == 0){
                    textView.setError("Pdf Should be there");
                } else if(text.getText().toString().isEmpty()){
                    text.setError("Title must be there");
                } else{
                     uploadPdf();
                }
            }
        });
    }

    private void uploadPdf() {
        pd.setMessage("Loading..");
        pd.show();
        StorageReference ref = reference.child("Pdf").child(name+"-"+System.currentTimeMillis()+".pdf");
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while(!task.isComplete());
                Uri uri1 = task.getResult();
                UploadData(String.valueOf(uri1));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Something wentWrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    class Data {
        public  String PdfName;
        public String Downloadurl;

        public Data(String pdfName, String downloadurl) {
            PdfName = pdfName;
            Downloadurl = downloadurl;
        }
    };
    private void UploadData(String data) {
        DatabaseReference ref = databaseReference.child("pdf");
        String key = ref.push().getKey();

        Data data1 = new Data(name,data);

        ref.child(key).setValue(data1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "SuccessFull Upploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.getRoot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
          uri = data.getData();
            if(uri.toString().startsWith("content://")){
                Cursor cursor = null;
                cursor = getApplicationContext().getContentResolver().query(uri,null,null,null,null);
                if(cursor != null && cursor.moveToFirst()){
                name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }else if(uri.toString().startsWith("file://")){
                name = new File(uri.toString()).getName();
            }
            textView.setText(name);
        }
    }
}