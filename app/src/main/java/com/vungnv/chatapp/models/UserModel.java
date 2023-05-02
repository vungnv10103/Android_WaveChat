package com.vungnv.chatapp.models;

public class UserModel {
    private String id;
    private String name;
    private String phone;
    private String image;
    private String email;

    private String pass;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public UserModel(String id, String name, String phone, String image, String email, String pass) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.email = email;
        this.pass = pass;
    }

    public UserModel(String id, String phone) {
        this.id = id;
        this.phone = phone;
    }

    public UserModel(String name, String phone, String image, String email, String pass) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.email = email;
        this.pass = pass;
    }

    public UserModel() {

    }
    public boolean checkEmpty(){
        if (name.isEmpty() ||pass.isEmpty() ){
            return true;
        }
        return false;
    }
}
