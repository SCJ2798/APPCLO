package com.project.appclo.dataentryapp;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public class Product {

    String name,barcode,price,colour,who;
    String catagory , sizeType , brand, qty;

    String sizeCatagory;

    ArrayList<HashMap> proFeature;
    HashMap<String,String> hashSize;
    ArrayList<String> proFeatureType;

    Bitmap FrontImg , BackImg;

    public Bitmap getFrontImg() {
        return FrontImg;
    }

    public void setFrontImg(Bitmap frontImg) {
        FrontImg = frontImg;
    }

    public Bitmap getBackImg() {
        return BackImg;
    }

    public void setBackImg(Bitmap backImg) {
        BackImg = backImg;
    }

    public String getSizeCatagory() {
        return sizeCatagory;
    }

    public void setSizeCatagory(String sizeCatagory) {
        this.sizeCatagory = sizeCatagory;
    }

    public ArrayList<String> getProFeatureType() {
        return proFeatureType;
    }

    public void setProFeatureType(ArrayList<String> proFeatureType) {
        this.proFeatureType = proFeatureType;
    }

    public HashMap<String, String> getHashSize() {
        return hashSize;
    }

    public void setHashSize(HashMap<String, String> hashSize) {
        this.hashSize = hashSize;
    }

    public ArrayList<HashMap> getProFeature() {
        return proFeature;
    }

    public void setProFeature(ArrayList<HashMap> proFeature) {
        this.proFeature = proFeature;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getSizeType() {
        return sizeType;
    }

    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}
