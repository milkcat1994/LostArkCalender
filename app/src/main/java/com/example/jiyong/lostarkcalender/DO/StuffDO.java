package com.example.jiyong.lostarkcalender.DO;

public class StuffDO {
    private int id;
    private String img_url;
    private String name;
    private int price;

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

    public int getPrice() { return price; }

    public void setPrice(int selected) {
        this.price = selected;
    }
}
