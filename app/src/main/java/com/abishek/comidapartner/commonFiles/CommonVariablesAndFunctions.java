package com.abishek.comidapartner.commonFiles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonVariablesAndFunctions {

    private static final String TAG = "CommanVarAndFun";

    public static final int RETRY_SECONDS = 10000;
    public static final int NO_OF_RETRY = 2;
    public static final String TIME_FORMAT = "hh:mm a";

    public static final String BASE = "http://androasu.in/comida/api/partner/";
    public static final String BASE_IMAGE = "http://androasu.in/comida/";
    public static final String BASE_LOGIN_OTP = BASE+"login/send/otp";
    public static final String BASE_LOGIN = BASE+"login/with/otp";
    public static final String BASE_SIGN_UP = BASE+"create/new";
    public static final String BASE_FCM = BASE+"store/fcm";
    public static final String BASE_GET_SHOP = BASE+"get/shop";
    public static final String BASE_GET_ADDRESS = BASE+"get/address";
    public static final String BASE_PROFILE_SHOW = BASE+"profile/show/";
    public static final String BASE_PROFILE_EDIT = BASE+"profile/edit";
    public static final String BASE_PRODUCT_CATEGORY = BASE+"product/show";
    public static final String BASE_PRODUCT_CREATE = BASE+"product/create";
    public static final String BASE_PRODUCT_DELETE = BASE+"product/delete/";
    public static final String BASE_PRODUCT_DETAIL = BASE+"product/detail/show";
    public static final String BASE_PRODUCT_EDIT = BASE+"product/edit";
    public static final String BASE_CHANGE_STOCK = BASE+"product/change/stock";
    public static final String BASE_ORDER_NEW = BASE+"order/new/";
    public static final String BASE_ORDER_PROGRESS = BASE+"order/progress/";
    public static final String BASE_ORDER_COMPLETED = BASE+"order/completed/";
    public static final String BASE_ORDER_DETAIL = BASE+"order/detail/";
    public static final String BASE_ORDER_QUEUE = BASE+"order/queue";
    public static final String BASE_ORDER_DISPATCH = BASE+"order/dispatch";
    public static final String BASE_SALES_DATA = BASE+"sales/current";
    public static final String BASE_GET_DELIVERY_PARTNER = BASE+"get/delivery/partner";
    public static final String BASE_ADD_DELIVERY_PARTNER = BASE+"add/delivery/partner";





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




    public static String getDate(String str) {
        Date date = null;
        try {

            date = new SimpleDateFormat("yyyy-MM-dd").parse(str);

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return new SimpleDateFormat("dd MMM").format(date);

    }

    public static String getTime(String str) {
        String dateString3 = str;
        String time = "";
        //old format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date3 = sdf.parse(dateString3);
            //new format
            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
            //formatting the given time to new format with AM/PM

            time = sdf2.format(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

}
