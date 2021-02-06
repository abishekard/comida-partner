package com.abishek.comidapartner.notification;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM MyNotificationTable ORDER BY MyNotificationTable.unixTime DESC")
    List<MyNotificationTable> getAllNotifications();

    @Query("SELECT * FROM MyNotificationTable WHERE MyNotificationTable.notiId= :id")
    MyNotificationTable getNotificationById(Integer id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(MyNotificationTable myNotification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotifications(List<MyNotificationTable> list);

    @Query("DELETE FROM MyNotificationTable WHERE MyNotificationTable.notiId = :id")
    void deleteNotificationById(int id);


    @Query("DELETE FROM MyNotificationTable")
    void deleteAllNotifications();
}
