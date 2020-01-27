package com.example.jiyong.lostarkcalender.ListVO;

import android.graphics.drawable.Drawable;

public class RecodeListVO {
    private int id;
    //이미지가 없을 경우 -1
    private int img;
    private String name;
    private String species;
    //0 is not success, 1 is success
    private int success;
    private String username;

    public RecodeListVO(int id, int img, String name, String species, int success, String username){
        this.id = id;
        this.img = img;
        this.name = name;
        this.species = species;
        this.success = success;
        this.username = username;
    }

    //구분선 저장
    public RecodeListVO(String name, String species, int success){
        this.id = -1;
        this.img = -1;
        this.name = name;
        this.species = species;
        this.success = success;
        this.username = null;
    }

    public int getId(){ return id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getImg(){ return img; }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() { return species; }

    public void setSpecies(String species) { this.species = species; }

    public int getSuccess() { return success; }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }


}