package com.abishek.comidapartner.Home.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abishek.comidapartner.Home.fragment.adapter.OrderDetailFoodAdapter;
import com.abishek.comidapartner.Home.fragment.model.OrderFoodModel;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.MySingleton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_DETAIL;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_DISPATCH;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_QUEUE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;

public class OrderDetail extends AppCompatActivity {

    private static final String TAG = "OrderDetail";

    private String from;

    private TextView subTotalView, deliveryAddressView,
            discountView, totalView, addressView, orderIdView;
    private ImageView shopImageView;
    private RecyclerView foodRecyclerView;
    private String orderId;
    private String customerId, deliveryAddress, createdAt, status;
    private List<OrderFoodModel> foodList;
    private int total = 0, Isubtotal = 0, Idiscount = 0;
    private Button btnQueue, btnDOne, btnDispatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getStringExtra("order_id");
        from = getIntent().getStringExtra("from");
        inItUi();
        foodList = new ArrayList<>();
    }

    public void inItUi() {

        foodRecyclerView = findViewById(R.id.order_recycler_view);


        subTotalView = findViewById(R.id.sub_total);
        discountView = findViewById(R.id.discount);
        totalView = findViewById(R.id.total);

        addressView = findViewById(R.id.delivered_address);
        orderIdView = findViewById(R.id.order_id);
        btnDispatch = findViewById(R.id.btn_dispatch);
        btnDOne = findViewById(R.id.btn_done);
        btnQueue = findViewById(R.id.btn_queue);


        btnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeOrderStatus(2);
            }
        });
        btnDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeOrderStatus(3);

            }
        });
        btnDOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setButtons();

        fetchOrderDetail();
    }

    public void setButtons() {
        if (from.equals("new")) {
            btnDOne.setVisibility(View.GONE);
            btnDispatch.setVisibility(View.GONE);

        }
        if (from.equals("progress")) {
            btnDOne.setVisibility(View.GONE);
            btnQueue.setVisibility(View.GONE);

        }
        if (from.equals("history")) {
            btnQueue.setVisibility(View.GONE);
            btnDispatch.setVisibility(View.GONE);

        }

    }

    public void fetchOrderDetail() {


        Log.e(TAG, "fetchCategory : called");

        final String URL = BASE_ORDER_DETAIL + orderId;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray subJson = jsonObject.getJSONArray("data");

                    status = jsonObject.getString("status");
                    customerId = jsonObject.getString("customer_id");
                    deliveryAddress = jsonObject.getString("delivery_address");
                    createdAt = jsonObject.getString("created_at");


                    for (int i = 0; i < subJson.length(); i++) {
                        JSONObject childJson = subJson.getJSONObject(i);
                        String discount = childJson.getString("discount");
                        String quantity = childJson.getString("quantity");
                        String price = childJson.getString("price");
                        String dateTime = childJson.getString("created_at");
                        String itemName = childJson.getString("item_name");
                        String itemImage = childJson.getString("item_image");
                        String priceType = childJson.getString("price_type");

                        Isubtotal = Isubtotal + (Integer.parseInt(price) * Integer.parseInt(quantity));
                        Idiscount = Idiscount + (Integer.parseInt(discount) * Integer.parseInt(quantity));
                        foodList.add(new OrderFoodModel(discount, quantity, price, dateTime, itemName, itemImage, priceType));

                    }

                    total = Isubtotal - Idiscount + 25;


                    setDataToView();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(OrderDetail.this, "server problem", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(getContext()).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(getContext()).getUserDetailsFromSP().get(ACCESS_TOKEN);

                //String fullKey = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxNDdmNGFjNWFlN2IzZDM4MmYyNTAwNWVhNTIwOGUyNDAzNjYwNzMyOWMyYjZiYWQ1YTlhMmNlZTEzZDI3ZTgzN2RkOTY5NzcxNWNhMzUxIn0.eyJhdWQiOiIxIiwianRpIjoiNzE0N2Y0YWM1YWU3YjNkMzgyZjI1MDA1ZWE1MjA4ZTI0MDM2NjA3MzI5YzJiNmJhZDVhOWEyY2VlMTNkMjdlODM3ZGQ5Njk3NzE1Y2EzNTEiLCJpYXQiOjE1NTExOTc0MjcsIm5iZiI6MTU1MTE5NzQyNywiZXhwIjoxNTgyNzMzNDI3LCJzdWIiOiI4NSIsInNjb3BlcyI6W119.kLmk7mEukKdoS9e_v31VQX29ypn7hJb7qAJvKA_GqeiYEYe2EQ9zLTd1IwO-S31CofoypnJ-LvAT7D4I0EZ9iYM1AS5A6-7bWH3-h01-glLQubbfedhvlg0xfT60s2r1onxlEMUnt-0kB2tbYgX_df4zJPExUhHRpzlnLNChzC3r1QD1dzgn-814GjxlQkwfgv_5dsKzyMlvVCHiTDg2z35h2uiWeRuVhmznbUGaGCWcxPwHpNV4k9pHOH9yrCwkjJuHlcSIiXD7W_QsRnzEa_dY6wASdymtGqHb99c3kfWmiKKwngAC9GY56OeMP0vLnYpXOAspu5rDlQkLCzCeh58KnqbqMUrQ0bZ3ChTaeATXM_fncQiByfMgAAfiVfu8GpKsnQKSYobzcqrqjmAgPTNEcq5ba4BCUuw1ysv0LodTqHGUHsSNsiZfx3GyqLoyOCMWY5oWO4M4saOTo3pUSGPSq15BsqRQXqbvzshxk9ysaAU1K9dZj-AZpy4mUxf3y4UX8-EADqJmYV7ywEph_FveDbdWNNUF72bqbTg8DTxwJ6V53cEOsxbmNb82jFJnz1vSxLFDDXv9Vvf23W5hm4Io2Ogxv8wyE5vNUgL2XepFrGwWWANEsp4fLebzfgFD3045vkrcfRPc164LVKHdLyaHhxB8TrYeK9TOqeEfk7M";

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);*/

                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {



                return super.getParams();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(OrderDetail.this).addToRequestQueue(stringRequest);


    }

    private void setDataToView() {


        addressView.setText(deliveryAddress);
        orderIdView.setText("#" + orderId);

        subTotalView.setText(Isubtotal + "");
        discountView.setText(Idiscount + "");
        totalView.setText(total + "");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetail.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        OrderDetailFoodAdapter orderDetailFoodAdapter = new OrderDetailFoodAdapter(foodList, OrderDetail.this);
        foodRecyclerView.setLayoutManager(linearLayoutManager);
        foodRecyclerView.setAdapter(orderDetailFoodAdapter);
        orderDetailFoodAdapter.notifyDataSetChanged();
    }


    public void changeOrderStatus(int stage) {
        String URL = "";
        if (stage == 2)
            URL = BASE_ORDER_QUEUE;
        else
            URL = BASE_ORDER_DISPATCH;
        Log.e(TAG, "fetchCategory : called");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");
                    if(status==200 )
                    {
                        if(stage==2)
                        Toast.makeText(OrderDetail.this,"order Queued",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(OrderDetail.this,"order Dispatched",Toast.LENGTH_SHORT).show();
                        finish();
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(OrderDetail.this, "server problem", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(getContext()).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(getContext()).getUserDetailsFromSP().get(ACCESS_TOKEN);

                //String fullKey = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxNDdmNGFjNWFlN2IzZDM4MmYyNTAwNWVhNTIwOGUyNDAzNjYwNzMyOWMyYjZiYWQ1YTlhMmNlZTEzZDI3ZTgzN2RkOTY5NzcxNWNhMzUxIn0.eyJhdWQiOiIxIiwianRpIjoiNzE0N2Y0YWM1YWU3YjNkMzgyZjI1MDA1ZWE1MjA4ZTI0MDM2NjA3MzI5YzJiNmJhZDVhOWEyY2VlMTNkMjdlODM3ZGQ5Njk3NzE1Y2EzNTEiLCJpYXQiOjE1NTExOTc0MjcsIm5iZiI6MTU1MTE5NzQyNywiZXhwIjoxNTgyNzMzNDI3LCJzdWIiOiI4NSIsInNjb3BlcyI6W119.kLmk7mEukKdoS9e_v31VQX29ypn7hJb7qAJvKA_GqeiYEYe2EQ9zLTd1IwO-S31CofoypnJ-LvAT7D4I0EZ9iYM1AS5A6-7bWH3-h01-glLQubbfedhvlg0xfT60s2r1onxlEMUnt-0kB2tbYgX_df4zJPExUhHRpzlnLNChzC3r1QD1dzgn-814GjxlQkwfgv_5dsKzyMlvVCHiTDg2z35h2uiWeRuVhmznbUGaGCWcxPwHpNV4k9pHOH9yrCwkjJuHlcSIiXD7W_QsRnzEa_dY6wASdymtGqHb99c3kfWmiKKwngAC9GY56OeMP0vLnYpXOAspu5rDlQkLCzCeh58KnqbqMUrQ0bZ3ChTaeATXM_fncQiByfMgAAfiVfu8GpKsnQKSYobzcqrqjmAgPTNEcq5ba4BCUuw1ysv0LodTqHGUHsSNsiZfx3GyqLoyOCMWY5oWO4M4saOTo3pUSGPSq15BsqRQXqbvzshxk9ysaAU1K9dZj-AZpy4mUxf3y4UX8-EADqJmYV7ywEph_FveDbdWNNUF72bqbTg8DTxwJ6V53cEOsxbmNb82jFJnz1vSxLFDDXv9Vvf23W5hm4Io2Ogxv8wyE5vNUgL2XepFrGwWWANEsp4fLebzfgFD3045vkrcfRPc164LVKHdLyaHhxB8TrYeK9TOqeEfk7M";

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);*/

                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("order_id",orderId);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(OrderDetail.this).addToRequestQueue(stringRequest);


    }

}