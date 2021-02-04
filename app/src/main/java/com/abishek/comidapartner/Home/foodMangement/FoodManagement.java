package com.abishek.comidapartner.Home.foodMangement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abishek.comidapartner.Home.foodMangement.adapter.ProductParentAdapter;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_CATEGORY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;

public class FoodManagement extends AppCompatActivity {

    private static final String TAG ="FoodManagement" ;
    private FloatingActionButton createFood;

    private ArrayList<CategoryModel> categoryList;
    private RecyclerView categoryRecycler;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_mangement);

        categoryRecycler = findViewById(R.id.category_recycler_view);


        userId = new LoginSessionManager(FoodManagement.this).getUserDetailsFromSP().get("user_id");

        createFood = findViewById(R.id.create_food);
        createFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodManagement.this,CreateFood.class));
            }
        });

        fetchProductList();
    }




    public void fetchProductList()
    {


        Log.e(TAG, "fetchCategory : called");

        final String URL = BASE_PRODUCT_CATEGORY;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                categoryList = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    JSONArray subJson = jsonObject.getJSONArray("data");

                    for(int i=0;i<subJson.length();i++)
                    {
                        String categoryName = subJson.getJSONObject(i).getString("category_name");
                        JSONArray childArray = subJson.getJSONObject(i).getJSONArray("category_data");
                        ArrayList<FoodModel> foodList= new ArrayList<>();
                        for(int j=0;j<childArray.length();j++)
                        {
                            JSONObject childJson = childArray.getJSONObject(j);
                            String productId =childJson.getString("id");
                            String itemName=childJson.getString("item_name");
                            String itemImage = childJson.getString("item_image");
                            String price = childJson.getString("price");
                            String priceType=childJson.getString("price_type");
                            String discount=childJson.getString("discount");
                            String vegNonVeg=childJson.getString("veg_non_veg");
                            String category=childJson.getString("category");
                            int inStock = childJson.getInt("in_stock");
                            String createAt = childJson.getString("created_at");

                            foodList.add(new FoodModel(productId,itemName,itemImage,price,priceType,discount,vegNonVeg,category,inStock,createAt));

                        }


                        categoryList.add(new CategoryModel(categoryName,foodList));

                    }


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

                Toast.makeText(FoodManagement.this,"server problem",Toast.LENGTH_SHORT).show();

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
                params.put("id",userId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(FoodManagement.this).addToRequestQueue(stringRequest);


    }

    private void setDataToView()
    {
        categoryRecycler = findViewById(R.id.category_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FoodManagement.this);
        ProductParentAdapter productParentAdapter = new ProductParentAdapter(categoryList, FoodManagement.this);
        categoryRecycler.setAdapter(productParentAdapter);
        categoryRecycler.setLayoutManager(linearLayoutManager);
        productParentAdapter.notifyDataSetChanged();



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        fetchProductList();
    }
}