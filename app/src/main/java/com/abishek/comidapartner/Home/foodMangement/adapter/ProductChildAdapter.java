package com.abishek.comidapartner.Home.foodMangement.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.foodMangement.CategoryModel;
import com.abishek.comidapartner.Home.foodMangement.FoodManagement;
import com.abishek.comidapartner.Home.foodMangement.FoodModel;
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

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_CHANGE_STOCK;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_CATEGORY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_DELETE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;


public class ProductChildAdapter extends RecyclerView.Adapter<ProductChildAdapter.ProductChildHolder> {

    private List<FoodModel> foodList, cartFoodList;
    private Context context;

    private String TAG = "productChildAdapter";

    public ProductChildAdapter(ArrayList<FoodModel> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;




    }

    @NonNull
    @Override
    public ProductChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_food_management, parent, false);


        return new ProductChildHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductChildHolder holder, int position) {



        holder.foodNameView.setText(foodList.get(position).getFoodName());
        holder.priceView.setText("₹" + foodList.get(position).getPrice() + "/" + foodList.get(position).getPrice_type());
        Picasso.get().load(BASE_IMAGE + foodList.get(position).getFoodImage()).into(holder.foodImageView);
        holder.discountView.setText(foodList.get(position).getDiscount());
        if(foodList.get(position).getVegNonVeg().toLowerCase().equals("veg"))
        {
            holder.vegNonVeg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_veg));
        }
        if(foodList.get(position).getVegNonVeg().toLowerCase().equals("non veg"))
        {
            holder.vegNonVeg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_non_veg));
        }
        if(foodList.get(position).getInStock()==1)
        {
            holder.inStockView.setChecked(true);
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   deleteProduct(foodList.get(position).getProductId(),position);
            }
        });

        holder.inStockView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    changeStock(foodList.get(position).getProductId(),"1");
                else
                    changeStock(foodList.get(position).getProductId(),"0");
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


    public static class ProductChildHolder extends RecyclerView.ViewHolder {

        private TextView foodNameView;
        private TextView priceView,btnEdit,btnDelete,discountView;;
        private ImageView foodImageView,vegNonVeg;
        private SwitchCompat inStockView;


        public ProductChildHolder(@NonNull View v) {
            super(v);

            foodNameView = v.findViewById(R.id.food_name);
            priceView = v.findViewById(R.id.price);
            foodImageView = v.findViewById(R.id.food_image);
            inStockView = v.findViewById(R.id.in_stock);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
            discountView = v.findViewById(R.id.discount_view);
            vegNonVeg = v.findViewById(R.id.veg_or_non_veg);

        }
    }



    public void deleteProduct(String productId,int position)
    {


        Log.e(TAG, "deleteProduct : called");

        final String URL = BASE_PRODUCT_DELETE+productId;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    int status = jsonObject.getInt("status");
                    if(status==200) {
                        Toast.makeText(context, "food deleted", Toast.LENGTH_SHORT).show();
                        foodList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,foodList.size());
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

                Toast.makeText(context,"server problem",Toast.LENGTH_SHORT).show();

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
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }



    public void changeStock(String productId,String inStock)
    {


        Log.e(TAG, "changeStock : called");

        final String URL = BASE_CHANGE_STOCK;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    int status = jsonObject.getInt("status");
                    if(status==200)
                        Toast.makeText(context,"stock changed", Toast.LENGTH_SHORT).show();



                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(context,"server problem",Toast.LENGTH_SHORT).show();

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
                params.put("product_id",productId);
                params.put("in_stock",inStock);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }


}
