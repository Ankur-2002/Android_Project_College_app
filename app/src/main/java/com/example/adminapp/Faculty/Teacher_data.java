package com.example.adminapp.Faculty;

public class Teacher_data {

        public String name,post,category,imageUrl,key,email;
        public Teacher_data(){

        }
        public Teacher_data(String name, String post, String category, String imageUrl,String key,String email) {
            this.name = name;
            this.post = post;
            this.category = category;
            this.imageUrl = imageUrl;
            this.key = key;
            this.email = email;
        }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
