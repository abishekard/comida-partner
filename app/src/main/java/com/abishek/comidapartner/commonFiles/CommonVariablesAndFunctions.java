package com.abishek.comidapartner.commonFiles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonVariablesAndFunctions {

    private static final String TAG = "CommanVarAndFun";

    public static final int RETRY_SECONDS = 10000;
    public static final int NO_OF_RETRY = 2;
    public static final String TIME_FORMAT = "hh:mm a";

    public static final String BASE = "http://androasu.in/comida/api/customer/";
    public static final String BASE_IMAGE = "http://androasu.in/comida/";
    public static final String BASE_ALL_PRODUCTS = BASE+"product/all";
    public static final String BASE_PRODUCT_CATEGORY = BASE+"product/category/all/";
    public static final String BASE_RESTAURANT_ALL = BASE+"restaurent/all";
    public static final String BASE_RESTAURANT_INFO = BASE+"partner/info/";
    public static final String BASE_LOGIN_OTP = BASE+"login/send/otp";
    public static final String BASE_LOGIN = BASE+"login/with/otp";
    public static final String BASE_SIGN_UP = BASE+"create/new";
    public static final String BASE_PROFILE_SHOW = BASE+"show/profile/";
    public static final String BASE_PROFILE_EDIT = BASE+"edit/profile";
    public static final String BASE_ADDRESS_SHOW = BASE+"show/address/";
    public static final String BASE_ADDRESS_ADD = BASE+"create/address";
    public static final String BASE_ADDRESS_DELETE = BASE+"delete/address/";
    public static final String BASE_FCM = BASE+"store/fcm";
    public static final String BASE_PLACE_ORDER = BASE+"placeOrder";
    public static final String BASE_NEW_ORDER = BASE+"orders/new/";
    public static final String BASE_COMPLETE_ORDER = BASE+"orders/completed/";
    public static final String BASE_ORDER_DETAIL = BASE+"order/detail/";



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    // Input - unix in sec
    // Output - Format : 1st Mar, 2019
    public static String getFormattedDate(String TAG, String unix) {

        String formatedstring = "NA";

        SimpleDateFormat sdf = new SimpleDateFormat("d");

        Long longUnix = null;

        try {

            longUnix = Long.valueOf(unix);
            String date = sdf.format(new Date(longUnix * 1000L));

            if (date.endsWith("1") && !date.endsWith("11"))
                sdf = new SimpleDateFormat("d'st' MMM, yyyy");
            else if (date.endsWith("2") && !date.endsWith("12"))
                sdf = new SimpleDateFormat("d'nd' MMM, yyyy");
            else if (date.endsWith("3") && !date.endsWith("13"))
                sdf = new SimpleDateFormat("d'rd' MMM, yyyy");
            else
                sdf = new SimpleDateFormat("d'th' MMM, yyyy");

        } catch (Exception e) {
            Log.e(TAG, "Exception cought while converting time : " + e.toString());
        }
        formatedstring = sdf.format(new Date(longUnix * 1000L));


        return formatedstring;
    }



    // Input - unix in sec
    // Output - Format : 06:45 PM
    public static String getFormattedTime(String TAG, String s) {

        String time = "NA";
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);

        try {
            Long unix = Long.valueOf(s);
            date = new Date(unix * 1000L);

            time = sdf.format(date);


        } catch (Exception e) {
            Log.e(TAG, "Exception cought while converting time : " + e.toString());

        }

        return time;

    }


}
