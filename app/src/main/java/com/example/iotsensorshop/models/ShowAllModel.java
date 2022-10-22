package com.example.iotsensorshop.models;

import java.io.Serializable;

public class ShowAllModel implements Serializable {
    String description;
    String name;
    int price;
    String img_url;
    String type;
    String documentId;
    int stock;

    public ShowAllModel() {
    }

    public ShowAllModel(String description, String name, int price, String img_url, String type, String documentId, int stock) {
        this.description = description;
        this.name = name;
        this.price = price;
        this.img_url = img_url;
        this.type = type;
        this.documentId = documentId;
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
