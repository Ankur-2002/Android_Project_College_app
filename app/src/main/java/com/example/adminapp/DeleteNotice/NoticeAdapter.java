package com.example.adminapp.DeleteNotice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeView> {

    private Context context;
    private List<NoticeUpload> list;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog pd;
    public NoticeAdapter(){

    }
    public NoticeAdapter(Context context, List<NoticeUpload> list) {
        this.context = context;
        this.list = list;

        pd = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public NoticeView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_feed,parent,false);
        return new NoticeView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeView holder, @SuppressLint("RecyclerView") int position) {
        NoticeUpload noticeUpload = list.get(position);
        holder.title.setText(noticeUpload.getTitle());
        try {
            if(!noticeUpload.getImage().isEmpty()) {
                Glide.with(context).load(Uri.parse(noticeUpload.getImage())).into(holder.noticeImage);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete it.");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd.setMessage("Deleting...");
                        pd.show();
                        databaseReference = FirebaseDatabase.getInstance().getReference();
//                storageReference = FirebaseStorage.getInstance().getReference();
                        databaseReference.child("Notice").child(noticeUpload.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                Toast.makeText(context.getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(context.getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                        databaseReference = databaseReference.getRoot();
                        notifyItemRemoved(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoticeView extends RecyclerView.ViewHolder {
        private TextView title,description;
        private ImageView Topimage,noticeImage;
        private Button button;
        public NoticeView(@NonNull View itemView) {
            super(itemView);
           title = itemView.findViewById(R.id.Title);
           Topimage = itemView.findViewById(R.id.teacherImage);
           noticeImage = itemView.findViewById(R.id.imageTag);
           button = itemView.findViewById(R.id.DeleteButton);
           description = itemView.findViewById(R.id.TextTag);

        }
    }
}


