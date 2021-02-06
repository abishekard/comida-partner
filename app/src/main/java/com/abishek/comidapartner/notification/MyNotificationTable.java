package com.abishek.comidapartner.notification;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyNotificationTable {

    public static int NEW = 1;
    public static int OLD = 0;

    @PrimaryKey(autoGenerate = true)
    private Integer notiId;

    private String title;
    private String des;
    public int newORold;
    private String unixTime;
    private String notiType;
    private String iconURL;


    public MyNotificationTable(String title, String des, String iconURL, String unixTime, String notiType) {
        this.notiId = null;
        this.title = title;
        this.des = des;
        this.iconURL = iconURL;
        this.unixTime = unixTime;
        this.notiType = notiType;
        this.newORold = NEW;
    }

    public void setNotiId(Integer notiId) {
        this.notiId = notiId;
    }

    public Integer getNotiId() {
        return notiId;
    }

    public String getTitle() {
        return title;
    }

    public String getDes() {
        return des;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getUnixTime() {
        return unixTime;
    }

    public String getNotiType() {
        return notiType;
    }
}
