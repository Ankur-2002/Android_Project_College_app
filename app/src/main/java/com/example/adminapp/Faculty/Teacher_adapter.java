package com.example.adminapp.Faculty;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.List;

public class Teacher_adapter extends RecyclerView.Adapter<Teacher_adapter.TeacherViewAdapter> {
    public List<Teacher_data> list;
    public Context context;

    public Teacher_adapter(List<Teacher_data> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TeacherViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_data,parent,false);
        return new TeacherViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewAdapter holder, int position) {
    Teacher_data currentData = list.get(position);
    if(context == null) return ;
    holder.name.setText(currentData.getName());
    holder.email.setText(currentData.getEmail());
    holder.post.setText(currentData.getPost());
    holder.image.setImageURI(Uri.parse(currentData.getImageUrl()));
    Glide.with(context).load(Uri.parse(currentData.getImageUrl())).into(holder.image);
    holder.update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,update_faculty.class);
            intent.putExtra("name",currentData.getName());
            intent.putExtra("post",currentData.getPost());
            intent.putExtra("email",currentData.getEmail());
            intent.putExtra("image",currentData.getImageUrl());
            intent.putExtra("category",currentData.getCategory());
            intent.putExtra("key",currentData.getKey());
            context.startActivity(intent);

//            Toast.makeText(context, "Update SuccessFully", Toast.LENGTH_SHORT).show();
        }
    });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TeacherViewAdapter extends RecyclerView.ViewHolder {
        private  TextView name;
        private  TextView post;
        private  TextView email;
        private  Button update;
        private final ImageView image;
        public TeacherViewAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Faculty_name);
            email = itemView.findViewById(R.id.Faculty_email);
            post = itemView.findViewById(R.id.Faculty_post);
            update = itemView.findViewById(R.id.Faculty_update);
            image = itemView.findViewById(R.id.Faculty_image);
        }
    }
}
