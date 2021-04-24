package com.example.cuisine;

public class FoodData {
    private String foodId,foodName,foodDesc,foodVag,foodMeat,foodType,foodSpicy,material,nutrition,calorie,foodImg;

    public FoodData(String foodId, String foodName, String foodDesc,String foodVag,String foodMeat,String foodType,String foodSpicy, String material, String nutrition,String calorie,String foodTag, String foodImg) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodDesc = foodDesc;
        this.material = material;
        this.nutrition = nutrition;
        this.calorie = calorie;
        this.foodVag= foodVag;
        this.foodMeat = foodMeat;
        this.foodType = foodType;
        this.foodSpicy = foodSpicy;
        this.foodImg = foodImg;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public String getMaterial() {return material;}

    public String getNutrition() {
        return nutrition;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getFoodVag() {
        return foodVag;
    }

    public String getFoodMeat() {
        return foodMeat;
    }

    public String getFoodType() {
        return foodType;
    }

    public String getFoodSpicy() {
        return foodSpicy;
    }

    public String getFoodImg() {
        return foodImg;
    }
}
