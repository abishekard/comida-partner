package com.abishek.comidapartner.Home.deliveryPartner;

public class DeliveryPartnerModel {

    private String Name;
    private String id;
    private String mobile;
    private String aadharNum;
    private String profileImage;

    public DeliveryPartnerModel(String name, String id, String mobile, String aadharNum, String profileImage) {
        Name = name;
        this.id = id;
        this.mobile = mobile;
        this.aadharNum = aadharNum;
        this.profileImage = profileImage;
    }

    public String getName() {
        return Name;
    }

    public String getId() {
        return id;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAadharNum() {
        return aadharNum;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
