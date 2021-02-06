package com.abishek.comidapartner.notification;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abishek.comidapartner.Home.foodMangement.FoodModel;


@Database(entities = {MyNotificationTable.class}, version = 1, exportSchema = false)
public abstract class ComidaDatabase extends RoomDatabase {


    private static ComidaDatabase INSTANCE;

   public static ComidaDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ComidaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    ComidaDatabase.class,
                                    "ComidaDatabase.db").build();
                }
            }
        }
        return INSTANCE;
    }




    public abstract NotificationDao getMyNotificationDao();
}
