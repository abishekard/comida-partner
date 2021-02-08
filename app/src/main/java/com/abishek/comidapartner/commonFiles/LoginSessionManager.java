package com.abishek.comidapartner.commonFiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.abishek.comidapartner.loginAndSignUp.AddShopDetails;
import com.abishek.comidapartner.loginAndSignUp.Login;
import com.abishek.comidapartner.notification.ComidaDatabase;
import com.abishek.comidapartner.notification.NotificationDao;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;


public class LoginSessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LoginPreference";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String ON_BOARDING_SHOWN = "onBoardingShown";

    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";

    public static final String USER_ID = "user_id";
    public static final String NAME = "name";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String PROFILE_IMAGE = "profile_image";
    public static final String SHOP_NAME = "shop_name";
    public static final String SHOP_IMAGE = "shop_image";
    public static final String ADDRESS = "address";
    public static final String PIN_CODE = "pin_code";

    public static final String FCM_TOKEN = "fcm_token";
    public static final String IS_FCM_REGISTERED = "is_fcm_registered";


    public LoginSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
      /*  if (onBoardingShown()) {
            int supportedVersion = new VersionChecker(context).getSupportedVersion();
            if (VERSION_CODE < supportedVersion) {
                new VersionChecker(context).openVersionNotSupportedDialog();
            }
        }*/

       /* if (!isFcmRegistered())
            context.startService(new Intent(context, SendFcmToMekvahanServer.class));*/

    }


    public void createLoginSession(String token_type, String accessToken, String userId, String name, String mobile,
                                   String email, String profileImage, String shopName, String shopImage, String address, String pinCode) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(TOKEN_TYPE, token_type);
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.putString(USER_ID, userId);
        editor.putString(NAME, name);
        editor.putString(MOBILE, mobile);
        editor.putString(EMAIL, email);
        editor.putString(PROFILE_IMAGE, profileImage);
        editor.putString(SHOP_NAME, shopName);
        editor.putString(SHOP_IMAGE, shopImage);
        editor.putString(ADDRESS, address);
        editor.putString(PIN_CODE, pinCode);
        editor.commit();
    }


    public void addProfileImage(String imageUrl) {
        editor.putString(PROFILE_IMAGE, imageUrl);
        editor.commit();
    }

    public void addShopDetail(String shopName) {
        editor.putString(SHOP_NAME, shopName);
        editor.commit();
    }

    public void addShopAddress(String address) {
        editor.putString(ADDRESS, address);
        editor.commit();
    }


    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(context, Login.class);

            Log.e("LogginSessionManager", "login :" + isLoggedIn());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }


    public boolean onBoardingShown() {
        return pref.getBoolean(ON_BOARDING_SHOWN, false);
    }

    public void setOnBoardingShown() {
        editor.putBoolean(ON_BOARDING_SHOWN, true);
        editor.apply();
    }

    public HashMap<String, String> getUserDetailsFromSP() {

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(TOKEN_TYPE, pref.getString(TOKEN_TYPE, ""));
        user.put(ACCESS_TOKEN, pref.getString(ACCESS_TOKEN, ""));
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(NAME, pref.getString(NAME, "You are Awesome"));
        user.put(MOBILE, pref.getString(MOBILE, null));
        user.put(EMAIL, pref.getString(EMAIL, ""));
        user.put(PROFILE_IMAGE, pref.getString(PROFILE_IMAGE, ""));
        user.put(SHOP_NAME, pref.getString(SHOP_NAME, "null"));
        user.put(SHOP_IMAGE, pref.getString(SHOP_IMAGE, "null"));
        user.put(ADDRESS, pref.getString(ADDRESS, "null"));
        user.put(PIN_CODE, pref.getString(PIN_CODE, "null"));


        return user;
    }

    public void logoutUser() {

        new Thread(() -> {
            try {
                FirebaseInstanceId.getInstance().deleteToken(getFcmToken(), "");
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        //-  context.stopService(new Intent(context, PhoneService.class));
        editor.clear();
        editor.commit();


        Intent i = new Intent(context, Login.class);


        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new ClearRoom(ComidaDatabase.getDatabase(context)).execute();


        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();
        context.startActivity(i);
        ((Activity) context).finish();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void setFCMToken(String s) {
        editor.putString(FCM_TOKEN, s);
        editor.commit();
    }

    public String getFcmToken() {
        return pref.getString(FCM_TOKEN, null);
    }

   /*- public void prepareFCMToken(MyListener listener) {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.e("prepareFCMToken", "onSuccess: " + instanceIdResult.getToken());
                setFCMToken(instanceIdResult.getToken());
                listener.updatePage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("prepareFCMToken", "onFailure: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }*/

    public boolean isFcmRegistered() {
        return pref.getBoolean(IS_FCM_REGISTERED, false);
    }

    public void setIsFcmRegistered(boolean value) {
        editor.putBoolean(IS_FCM_REGISTERED, value);
        editor.commit();
    }

    class ClearRoom extends AsyncTask<Void, Void, Void> {

        private final NotificationDao notificationDao;

        public ClearRoom(ComidaDatabase instance) {
            notificationDao = instance.getMyNotificationDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notificationDao.deleteAllNotifications();
            return null;
        }

    }

}
