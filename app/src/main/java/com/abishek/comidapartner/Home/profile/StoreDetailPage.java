package com.abishek.comidapartner.Home.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.abishek.comidapartner.loginAndSignUp.AddNewAddress;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_SHOW;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class StoreDetailPage extends AppCompatActivity {

    private static final String TAG = "StoreDetailPage";

    private String userId;
    private ImageView aadharFrontView, aadharBackView, shopImageView;
    private TextView ownerNameView, shopNameView, shopSpecialityView, gstNumberView, aadharNumberView,
            closeTimeView, openTimeView;
    private Button btnDone;
    private ProgressDialog progressDialog;
    private TextView btnEditProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail_page);

        inItUi();
        getUserId();




    }

    public void inItUi() {
        aadharFrontView = findViewById(R.id.aadhar_front);
        aadharBackView = findViewById(R.id.aadhar_back);
        shopImageView = findViewById(R.id.shop_image);

        ownerNameView = findViewById(R.id.owner_name);
        shopNameView = findViewById(R.id.shop_name);
        shopSpecialityView = findViewById(R.id.speciality);
        gstNumberView = findViewById(R.id.gst_number);
        aadharNumberView = findViewById(R.id.aadhar_number);

        closeTimeView = findViewById(R.id.close_time);
        openTimeView = findViewById(R.id.open_time);
        btnEditProfile = findViewById(R.id.edit_profile);

        progressBar = findViewById(R.id.progress_bar);

        btnDone = findViewById(R.id.btn_done);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreDetailPage.this,EditShopDetails.class));
            }
        });
    }


    public void fetchShopDetail() {


        Log.e(TAG, "fetchAllProductList : called");

        final String URL = BASE_PROFILE_SHOW + userId;

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray subJson = jsonObject.getJSONArray("data");

                    JSONObject childJson = subJson.getJSONObject(0);
                    String shopName = childJson.getString("shop_name");
                    String speciality = childJson.getString("speciality");
                    String shopImage = childJson.getString("shop_image");
                    String address = childJson.getString("address");
                    String openTime = childJson.getString("open_time");
                    String closeTime = childJson.getString("close_time");
                    String available = childJson.getString("available");
                    String rating = childJson.getString("rating");
                    String aadharNum = childJson.getString("aadhar_number");
                    String gstNumber = childJson.getString("gst_number");
                    String aadharFront = childJson.getString("aadhar_front");
                    String aadharBack = childJson.getString("aadhar_back");
                    String name = childJson.getString("name");


                    ownerNameView.setText(name);
                    shopNameView.setText(shopName);

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                    Date dtOpen,dtClose;
                    try {
                        dtOpen = sdf.parse(openTime);
                        dtClose = sdf.parse(closeTime);
                        openTimeView.setText(sdfs.format(dtOpen));
                        closeTimeView.setText(sdfs.format(dtClose));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                   // openTimeView.setText(openTime);
                   // closeTimeView.setText(closeTime);
                    shopSpecialityView.setText(speciality);
                    gstNumberView.setText(gstNumber);
                    aadharNumberView.setText(aadharNum);
                    Picasso.get().load(BASE_IMAGE + aadharBack).into(aadharBackView);
                    Picasso.get().load(BASE_IMAGE + aadharFront).into(aadharFrontView);
                    Picasso.get().load(BASE_IMAGE + shopImage).into(shopImageView);

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(StoreDetailPage.this, "server problem", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(StoreDetailPage.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(StoreDetailPage.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

                //String fullKey = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxNDdmNGFjNWFlN2IzZDM4MmYyNTAwNWVhNTIwOGUyNDAzNjYwNzMyOWMyYjZiYWQ1YTlhMmNlZTEzZDI3ZTgzN2RkOTY5NzcxNWNhMzUxIn0.eyJhdWQiOiIxIiwianRpIjoiNzE0N2Y0YWM1YWU3YjNkMzgyZjI1MDA1ZWE1MjA4ZTI0MDM2NjA3MzI5YzJiNmJhZDVhOWEyY2VlMTNkMjdlODM3ZGQ5Njk3NzE1Y2EzNTEiLCJpYXQiOjE1NTExOTc0MjcsIm5iZiI6MTU1MTE5NzQyNywiZXhwIjoxNTgyNzMzNDI3LCJzdWIiOiI4NSIsInNjb3BlcyI6W119.kLmk7mEukKdoS9e_v31VQX29ypn7hJb7qAJvKA_GqeiYEYe2EQ9zLTd1IwO-S31CofoypnJ-LvAT7D4I0EZ9iYM1AS5A6-7bWH3-h01-glLQubbfedhvlg0xfT60s2r1onxlEMUnt-0kB2tbYgX_df4zJPExUhHRpzlnLNChzC3r1QD1dzgn-814GjxlQkwfgv_5dsKzyMlvVCHiTDg2z35h2uiWeRuVhmznbUGaGCWcxPwHpNV4k9pHOH9yrCwkjJuHlcSIiXD7W_QsRnzEa_dY6wASdymtGqHb99c3kfWmiKKwngAC9GY56OeMP0vLnYpXOAspu5rDlQkLCzCeh58KnqbqMUrQ0bZ3ChTaeATXM_fncQiByfMgAAfiVfu8GpKsnQKSYobzcqrqjmAgPTNEcq5ba4BCUuw1ysv0LodTqHGUHsSNsiZfx3GyqLoyOCMWY5oWO4M4saOTo3pUSGPSq15BsqRQXqbvzshxk9ysaAU1K9dZj-AZpy4mUxf3y4UX8-EADqJmYV7ywEph_FveDbdWNNUF72bqbTg8DTxwJ6V53cEOsxbmNb82jFJnz1vSxLFDDXv9Vvf23W5hm4Io2Ogxv8wyE5vNUgL2XepFrGwWWANEsp4fLebzfgFD3045vkrcfRPc164LVKHdLyaHhxB8TrYeK9TOqeEfk7M";

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);
                Log.e(TAG, "Authorization " + tokenType + " " + accessToken);

                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return super.getParams();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(StoreDetailPage.this).addToRequestQueue(stringRequest);


    }


    public void getUserId() {
        LoginSessionManager loginSessionManager = new LoginSessionManager(StoreDetailPage.this);

        HashMap<String, String> user = loginSessionManager.getUserDetailsFromSP();
        userId = user.get("user_id");


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fetchShopDetail();
    }
}