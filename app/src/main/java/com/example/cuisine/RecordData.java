package com.example.cuisine;

public class RecordData {
    private String foodId,foodName,date;
    byte[] foodImg;

    public RecordData(String foodId, String foodName, String date, byte[] foodImg) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.date = date;
        this.foodImg = foodImg;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getDate() {
        return date;
    }

    public byte[] getFoodImg() {
        return foodImg;
    }
}
