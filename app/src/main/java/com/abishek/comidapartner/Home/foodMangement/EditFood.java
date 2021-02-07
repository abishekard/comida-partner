package com.abishek.comidapartner.Home.foodMangement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.abishek.comidapartner.Home.adapter.SpinnerAdapter;
import com.abishek.comidapartner.Home.profile.ProfilePage;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.airbnb.lottie.L;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_CREATE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_DETAIL;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_EDIT;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_EDIT;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_SHOW;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class EditFood extends AppCompatActivity {

    private static final String TAG = "EditFood";
    private int PROFILE_REQUEST = 100;
    private int CROP_PROFILE = 101;


    private Spinner choosePriceType, chooseFoodCategory, chooseVegNonVeg;
    private ArrayList<String> priceTypeList, foodCategoryList, vegNonVegList;
    private ProgressDialog progressDialog;
    private String userId;
    private EditText edtFoodName, edtPrice, edtDiscount;
    private Button btnUpdate;

    private ImageView foodImageView;
    private String productId;
    private HashMap<String, String> dataToUpload;
    private HashMap<String, File> fileTOUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        inItUi();
        productId = getIntent().getStringExtra("product_id");
        fetchFoodDetail(productId);
        dataToUpload = new HashMap<>();
        fileTOUpload = new HashMap<>();
    }

    public void inItUi() {
        edtFoodName = findViewById(R.id.food_name);
        edtPrice = findViewById(R.id.price);
        edtDiscount = findViewById(R.id.discount);
        btnUpdate = findViewById(R.id.btn_add);

        choosePriceType = findViewById(R.id.choose_price_type);
        chooseFoodCategory = findViewById(R.id.food_category);
        chooseVegNonVeg = findViewById(R.id.veg_or_non_veg);

        foodImageView = findViewById(R.id.food_image);

        priceTypeList = new ArrayList<>();
        priceTypeList.add("Choose Price Type");
        priceTypeList.add("Piece");
        priceTypeList.add("Plate");

        foodCategoryList = new ArrayList<>();
        foodCategoryList.add("Food Category");
        foodCategoryList.add("chicken");
        foodCategoryList.add("fish");
        foodCategoryList.add("panner");
        foodCategoryList.add("sweet");
        foodCategoryList.add("soup");
        foodCategoryList.add("snacks");
        foodCategoryList.add("desert");
        foodCategoryList.add("drinks");
        foodCategoryList.add("south Indian");
        foodCategoryList.add("pulse");
        foodCategoryList.add("rice");
        foodCategoryList.add("chappati");
        foodCategoryList.add("egg");
        foodCategoryList.add("mutton");

        vegNonVegList = new ArrayList<>();
        vegNonVegList.add("Veg OR Non-Veg");
        vegNonVegList.add("Veg");
        vegNonVegList.add("Non Veg");

        choosePriceType.setAdapter(new SpinnerAdapter(EditFood.this, 0, priceTypeList));
        chooseFoodCategory.setAdapter(new SpinnerAdapter(EditFood.this, 0, foodCategoryList));
        chooseVegNonVeg.setAdapter(new SpinnerAdapter(EditFood.this, 0, vegNonVegList));

        progressDialog = new ProgressDialog(EditFood.this);
        progressDialog.setMessage("wait...");
        progressDialog.setCancelable(false);

        userId = new LoginSessionManager(EditFood.this).getUserDetailsFromSP().get("user_id");

        foodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditFood.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditFood.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }


                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag", 1);

                startActivityForResult(gallaryIntent, PROFILE_REQUEST);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromUi();
            }
        });




    }

    public void getDataFromUi() {
        String foodName = edtFoodName.getText().toString();
        String vegNonVeg = chooseVegNonVeg.getSelectedItem().toString();
        String price = edtPrice.getText().toString();
        String priceType = choosePriceType.getSelectedItem().toString();
        String discount = edtDiscount.getText().toString();
        String category = chooseFoodCategory.getSelectedItem().toString();
        if (foodName.equals("")) {
            Toast.makeText(EditFood.this, "Please enter FoodName", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vegNonVeg.toLowerCase().equals("veg or non-veg")) {
            Toast.makeText(EditFood.this, "Please select veg or non-veg", Toast.LENGTH_SHORT).show();
            return;
        }
        if (price.equals("")) {
            Toast.makeText(EditFood.this, "Please enter price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (discount.equals("")) {
            Toast.makeText(EditFood.this, "Please enter discount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceType.toLowerCase().equals("choose price type")) {
            Toast.makeText(EditFood.this, "Please select price type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (category.toLowerCase().equals("food category")) {
            Toast.makeText(EditFood.this, "Please select food category", Toast.LENGTH_SHORT).show();
            return;
        }


        sendDataToServer(foodName, vegNonVeg, price, priceType, discount, category);


    }


    public void fetchFoodDetail(String productId) {


        Log.e(TAG, "fetchFoodDetail : called  " + productId);

        final String URL = BASE_PRODUCT_DETAIL;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");

                    JSONObject subJson = jsonObject.getJSONObject("data");

                    String itemName = subJson.getString("item_name");
                    String itemImage = subJson.getString("item_image");
                    String price = subJson.getString("price");
                    String priceType = subJson.getString("price_type");
                    String discount = subJson.getString("discount");
                    String VegNonVeg = subJson.getString("veg_non_veg");
                    String category = subJson.getString("category");


                    setDataToView(itemName, itemImage, price, priceType, discount, VegNonVeg, category);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(EditFood.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(EditFood.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

                //String fullKey = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxNDdmNGFjNWFlN2IzZDM4MmYyNTAwNWVhNTIwOGUyNDAzNjYwNzMyOWMyYjZiYWQ1YTlhMmNlZTEzZDI3ZTgzN2RkOTY5NzcxNWNhMzUxIn0.eyJhdWQiOiIxIiwianRpIjoiNzE0N2Y0YWM1YWU3YjNkMzgyZjI1MDA1ZWE1MjA4ZTI0MDM2NjA3MzI5YzJiNmJhZDVhOWEyY2VlMTNkMjdlODM3ZGQ5Njk3NzE1Y2EzNTEiLCJpYXQiOjE1NTExOTc0MjcsIm5iZiI6MTU1MTE5NzQyNywiZXhwIjoxNTgyNzMzNDI3LCJzdWIiOiI4NSIsInNjb3BlcyI6W119.kLmk7mEukKdoS9e_v31VQX29ypn7hJb7qAJvKA_GqeiYEYe2EQ9zLTd1IwO-S31CofoypnJ-LvAT7D4I0EZ9iYM1AS5A6-7bWH3-h01-glLQubbfedhvlg0xfT60s2r1onxlEMUnt-0kB2tbYgX_df4zJPExUhHRpzlnLNChzC3r1QD1dzgn-814GjxlQkwfgv_5dsKzyMlvVCHiTDg2z35h2uiWeRuVhmznbUGaGCWcxPwHpNV4k9pHOH9yrCwkjJuHlcSIiXD7W_QsRnzEa_dY6wASdymtGqHb99c3kfWmiKKwngAC9GY56OeMP0vLnYpXOAspu5rDlQkLCzCeh58KnqbqMUrQ0bZ3ChTaeATXM_fncQiByfMgAAfiVfu8GpKsnQKSYobzcqrqjmAgPTNEcq5ba4BCUuw1ysv0LodTqHGUHsSNsiZfx3GyqLoyOCMWY5oWO4M4saOTo3pUSGPSq15BsqRQXqbvzshxk9ysaAU1K9dZj-AZpy4mUxf3y4UX8-EADqJmYV7ywEph_FveDbdWNNUF72bqbTg8DTxwJ6V53cEOsxbmNb82jFJnz1vSxLFDDXv9Vvf23W5hm4Io2Ogxv8wyE5vNUgL2XepFrGwWWANEsp4fLebzfgFD3045vkrcfRPc164LVKHdLyaHhxB8TrYeK9TOqeEfk7M";

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);


                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put("product_id", productId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(EditFood.this).addToRequestQueue(stringRequest);


    }


    public void setDataToView(String foodName, String foodImage, String price,
                              String priceType, String discount, String vegNonVeg, String category) {
        edtFoodName.setText(foodName);
        if (vegNonVeg.toLowerCase().equals("veg"))
            chooseVegNonVeg.setSelection(1);
        else
            chooseVegNonVeg.setSelection(2);

        edtPrice.setText(price);

        if (priceType.toLowerCase().equals("plate"))
            choosePriceType.setSelection(2);
        else
            choosePriceType.setSelection(1);

        edtDiscount.setText(discount);

        int aa = foodCategoryList.indexOf(category);
        chooseFoodCategory.setSelection(aa);

        Picasso.get().load(BASE_IMAGE + foodImage).into(foodImageView);
        Log.e(TAG, BASE_IMAGE + foodImage);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            Intent cropIntent = CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(EditFood.this);
            startActivityForResult(cropIntent, CROP_PROFILE);


        }


        if (requestCode == CROP_PROFILE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri();
                foodImageView.setImageURI(resultUri);

                fileTOUpload.put("item_image", new File(resultUri.getPath()));


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(EditFood.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }




    public void sendDataToServer(String foodName, String vegNonVeg, String price,
                                 String priceType, String discount, String foodCategory) {


        String url = BASE_PRODUCT_EDIT;
        Log.e(TAG, "setDataToServer: called");
        Log.e(TAG,foodName+" "+vegNonVeg+" "+price+" "+priceType+" "+discount+" "+foodCategory);

        progressDialog.show();
        btnUpdate.setEnabled(false);
        AndroidNetworking.upload(url)
                .addMultipartFile(fileTOUpload)
                .addMultipartParameter("item_name", foodName)
                .addMultipartParameter("price", price)
                .addMultipartParameter("veg_non_veg", vegNonVeg)
                .addMultipartParameter("category", foodCategory)
                .addMultipartParameter("price_type", priceType)
                .addMultipartParameter("discount", discount)
                .addMultipartParameter("id", productId)
                .addHeaders(getHeader())
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());


                        try {


                            int status = response.getInt("status");
                            if (status == 200) {
                                Toast.makeText(EditFood.this, "food item updated", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                                finish();

                            }
                            if (status == 300) {
                                Toast.makeText(EditFood.this, response.toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                btnUpdate.setEnabled(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            progressDialog.dismiss();
                            btnUpdate.setEnabled(true);
                            Log.e(TAG, e.toString());
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, error.getErrorBody());

                        progressDialog.dismiss();
                        btnUpdate.setEnabled(true);
                        if (error.getErrorCode() != 0) {
                            // received error from server
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());


                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }

                    }

                });
    }


    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        String tokenType = new LoginSessionManager(EditFood.this).getUserDetailsFromSP().get(TOKEN_TYPE);
        String accessToken = new LoginSessionManager(EditFood.this).getUserDetailsFromSP().get(ACCESS_TOKEN);


        header.put("Accept", "application/json");
        header.put("Authorization", tokenType + " " + accessToken);

        return header;
    }

}