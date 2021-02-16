package com.abishek.comidapartner.Home.sales;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abishek.comidapartner.Home.profile.ProfilePage;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_SHOW;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_SALES_DATA;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class Sales extends AppCompatActivity {

    private final String TAG = "Sales";

    private TextView todayCodCountView,todayOnlineCountView,todayCodTotalView,todayOnlineTotalView;
    private TextView weekCodCountView,weekOnlineCountView,weekCodTotalView,weekOnlineTotalView;
    private TextView monthCodCountView,monthOnlineCountView,monthCodTotalView,monthOnlineTotalView;
    private String userId;
    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        todayCodCountView = findViewById(R.id.today_cod_count);
        todayOnlineCountView = findViewById(R.id.today_online_count);
        todayCodTotalView = findViewById(R.id.today_cod_total);
        todayOnlineTotalView = findViewById(R.id.today_online_total);

        weekCodCountView = findViewById(R.id.week_cod_count);
        weekOnlineCountView = findViewById(R.id.week_online_count);
        weekCodTotalView = findViewById(R.id.week_cod_total);
        weekOnlineTotalView = findViewById(R.id.week_online_total);

        monthCodCountView = findViewById(R.id.month_cod_count);
        monthOnlineCountView = findViewById(R.id.month_online_count);
        monthCodTotalView = findViewById(R.id.month_cod_total);
        monthOnlineTotalView = findViewById(R.id.month_online_total);

        loadingLayout = findViewById(R.id.loading);

        userId = new LoginSessionManager(Sales.this).getUserDetailsFromSP().get("user_id");

        fetchSalesData();
    }


    public void fetchSalesData() {


        Log.e(TAG, "fetchAllProductList : called");

       String URL = BASE_SALES_DATA;

       loadingLayout.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String todayCodCount=jsonObject.getString("today_cod_count");
                    String todayOnlineCount=jsonObject.getString("today_online_count");
                    String todayCodTotal=jsonObject.getString("today_cod_total");
                    String todayOnlineTotal=jsonObject.getString("today_online_total");
                    String weekCodCount=jsonObject.getString("week_cod_count");
                    String weekOnlineCount=jsonObject.getString("week_online_count");
                    String weekCodTotal=jsonObject.getString("week_cod_total");
                    String weekOnlineTotal=jsonObject.getString("week_online_total");
                    String monthCodCount=jsonObject.getString("month_cod_count");
                    String monthOnlineCount=jsonObject.getString("month_online_count");
                    String monthCodTotal=jsonObject.getString("month_cod_total");
                    String monthOnlineTotal=jsonObject.getString("month_online_total");

                    todayCodCountView.setText(todayCodCount);
                    todayOnlineCountView.setText(todayOnlineCount);
                    todayCodTotalView.setText(todayCodTotal);
                    todayOnlineTotalView.setText(todayOnlineTotal);

                    weekCodCountView.setText(weekCodCount);
                    weekOnlineCountView.setText(weekOnlineCount);
                    weekCodTotalView.setText(weekCodTotal);
                    weekOnlineTotalView.setText(weekOnlineTotal);

                    monthCodCountView.setText(monthCodCount);
                    monthOnlineCountView.setText(monthOnlineCount);
                    monthCodTotalView.setText(monthCodTotal);
                    monthOnlineTotalView.setText(monthOnlineTotal);

                    loadingLayout.setVisibility(View.GONE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(Sales.this,"server problem",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(Sales.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(Sales.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

                //String fullKey = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxNDdmNGFjNWFlN2IzZDM4MmYyNTAwNWVhNTIwOGUyNDAzNjYwNzMyOWMyYjZiYWQ1YTlhMmNlZTEzZDI3ZTgzN2RkOTY5NzcxNWNhMzUxIn0.eyJhdWQiOiIxIiwianRpIjoiNzE0N2Y0YWM1YWU3YjNkMzgyZjI1MDA1ZWE1MjA4ZTI0MDM2NjA3MzI5YzJiNmJhZDVhOWEyY2VlMTNkMjdlODM3ZGQ5Njk3NzE1Y2EzNTEiLCJpYXQiOjE1NTExOTc0MjcsIm5iZiI6MTU1MTE5NzQyNywiZXhwIjoxNTgyNzMzNDI3LCJzdWIiOiI4NSIsInNjb3BlcyI6W119.kLmk7mEukKdoS9e_v31VQX29ypn7hJb7qAJvKA_GqeiYEYe2EQ9zLTd1IwO-S31CofoypnJ-LvAT7D4I0EZ9iYM1AS5A6-7bWH3-h01-glLQubbfedhvlg0xfT60s2r1onxlEMUnt-0kB2tbYgX_df4zJPExUhHRpzlnLNChzC3r1QD1dzgn-814GjxlQkwfgv_5dsKzyMlvVCHiTDg2z35h2uiWeRuVhmznbUGaGCWcxPwHpNV4k9pHOH9yrCwkjJuHlcSIiXD7W_QsRnzEa_dY6wASdymtGqHb99c3kfWmiKKwngAC9GY56OeMP0vLnYpXOAspu5rDlQkLCzCeh58KnqbqMUrQ0bZ3ChTaeATXM_fncQiByfMgAAfiVfu8GpKsnQKSYobzcqrqjmAgPTNEcq5ba4BCUuw1ysv0LodTqHGUHsSNsiZfx3GyqLoyOCMWY5oWO4M4saOTo3pUSGPSq15BsqRQXqbvzshxk9ysaAU1K9dZj-AZpy4mUxf3y4UX8-EADqJmYV7ywEph_FveDbdWNNUF72bqbTg8DTxwJ6V53cEOsxbmNb82jFJnz1vSxLFDDXv9Vvf23W5hm4Io2Ogxv8wyE5vNUgL2XepFrGwWWANEsp4fLebzfgFD3045vkrcfRPc164LVKHdLyaHhxB8TrYeK9TOqeEfk7M";

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);


                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> params = new HashMap<>();
                params.put("partner_id",userId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(Sales.this).addToRequestQueue(stringRequest);


    }
}