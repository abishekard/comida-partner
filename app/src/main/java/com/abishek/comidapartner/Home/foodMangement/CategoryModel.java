package com.abishek.comidapartner.Home.foodMangement;

import java.util.ArrayList;

public class CategoryModel {

    private String categoryName;
    private ArrayList<FoodModel> foodList;


    public CategoryModel(String categoryName, ArrayList<FoodModel> foodList) {
        this.categoryName = categoryName;
        this.foodList = foodList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ArrayList<FoodModel> getFoodList() {
        return foodList;
    }
}
