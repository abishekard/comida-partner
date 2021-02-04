package com.abishek.comidapartner.Home.foodMangement;


public class FoodModel {


    private String productId;
    private String foodName;
    private String foodImage;
    private String price;
    private String price_type;
    private String discount;
    private String vegNonVeg;
    private String type;
    private int inStock;
    private String createdAt;


    public FoodModel(String productId, String foodName, String foodImage,
                     String price, String price_type, String discount,
                     String vegNonVeg, String type, int inStock, String createdAt) {
        this.productId = productId;
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.price = price;
        this.price_type = price_type;
        this.discount = discount;
        this.vegNonVeg = vegNonVeg;
        this.type = type;
        this.inStock = inStock;
        this.createdAt = createdAt;


    }

    public String getProductId() {
        return productId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public String getPrice() {
        return price;
    }

    public String getPrice_type() {
        return price_type;
    }

    public String getDiscount() {
        return discount;
    }

    public String getVegNonVeg() {
        return vegNonVeg;
    }

    public String getType() {
        return type;
    }

    public int getInStock() {
        return inStock;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
