package com.abishek.comidapartner.Home.fragment.model;

public class MyOrderModel {

    private String orderId;
    private String deliveredAddress;
    private String customerAddressId;
    private String status;
    private String createAt;
    private String totalPrice;
    private String partnerId;
    private String image;

    public MyOrderModel(String orderId, String deliveredAddress,
                        String customerAddressId, String status, String createAt,
                        String totalPrice, String partnerId, String image) {
        this.orderId = orderId;
        this.deliveredAddress = deliveredAddress;
        this.customerAddressId = customerAddressId;
        this.status = status;
        this.createAt = createAt;
        this.totalPrice = totalPrice;
        this.partnerId = partnerId;
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDeliveredAddress() {
        return deliveredAddress;
    }

    public String getCustomerAddressId() {
        return customerAddressId;
    }

    public String getStatus() {
        return status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getUserId() {
        return partnerId;
    }

    public String getImage() {
        return image;
    }
}
