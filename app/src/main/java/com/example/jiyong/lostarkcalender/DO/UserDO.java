package com.example.jiyong.lostarkcalender.DO;

import java.io.Serializable;

public class UserDO implements Serializable {
    private int id;
    private String img_url;
    private String name;
    private String level;
    private int selected;

    public UserDO(int _id, String img_url, String name, String level, int selected){
        this.id = _id;
        this.img_url = img_url;
        this.name = name;
        this.level = level;
        this.selected = selected;
    }

    public int getId(){ return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) { this.img_url = img_url; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getSelected() { return selected; }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
