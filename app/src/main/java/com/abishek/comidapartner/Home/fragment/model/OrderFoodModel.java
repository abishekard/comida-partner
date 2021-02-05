package com.abishek.comidapartner.Home.fragment.model;

public class OrderFoodModel {

    private String discount;
    private String quantity;
    private String price;
    private String dateTime;
    private String itemName;
    private String itemImage;
    private String priceType;

    public OrderFoodModel(String discount, String quantity, String price,
                          String dateTime, String itemName, String itemImage,
                          String priceType) {
        this.discount = discount;
        this.quantity = quantity;
        this.price = price;
        this.dateTime = dateTime;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.priceType = priceType;
    }

    public String getDiscount() {
        return discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getPriceType() {
        return priceType;
    }
}
