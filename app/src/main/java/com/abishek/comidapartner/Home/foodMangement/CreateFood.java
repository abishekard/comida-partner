package com.abishek.comidapartner.Home.foodMangement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abishek.comidapartner.Home.adapter.SpinnerAdapter;
import com.abishek.comidapartner.Home.profile.ProfilePage;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.loginAndSignUp.AddNewAddress;
import com.abishek.comidapartner.loginAndSignUp.AddShopDetails;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_GET_SHOP;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PRODUCT_CREATE;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class CreateFood extends AppCompatActivity {

    private static final String TAG = "CreateFood" ;
    private int PROFILE_REQUEST = 100;
    private int CROP_PROFILE = 101;


    private Spinner choosePriceType,chooseFoodCategory,chooseVegNonVeg;
    private ArrayList<String> priceTypeList,foodCategoryList,vegNonVegList;
    private ProgressDialog progressDialog;
    private String userId;
    private EditText edtFoodName,edtPrice,edtDiscount;
    private Button btnAdd;
    private File foodImageFile;

    private ImageView foodImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        inItUi();



    }

    public void inItUi()
    {
        edtFoodName = findViewById(R.id.food_name);
        edtPrice = findViewById(R.id.price);
        edtDiscount = findViewById(R.id.discount);
        btnAdd = findViewById(R.id.btn_add);

        choosePriceType = findViewById(R.id.choose_price_type);
        chooseFoodCategory = findViewById(R.id.food_category);
        chooseVegNonVeg= findViewById(R.id.veg_or_non_veg);

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

        choosePriceType.setAdapter(new SpinnerAdapter(CreateFood.this,0,priceTypeList));
        chooseFoodCategory.setAdapter(new SpinnerAdapter(CreateFood.this,0,foodCategoryList));
        chooseVegNonVeg.setAdapter(new SpinnerAdapter(CreateFood.this,0,vegNonVegList));

        progressDialog = new ProgressDialog(CreateFood.this);
        progressDialog.setMessage("wait...");
        progressDialog.setCancelable(false);

        userId = new LoginSessionManager(CreateFood.this).getUserDetailsFromSP().get("user_id");

        foodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CreateFood.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateFood.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }


                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag", 1);

                startActivityForResult(gallaryIntent, PROFILE_REQUEST);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   getDataFromUi();
            }
        });

    }


    public void getDataFromUi()
    {
        String foodName = edtFoodName.getText().toString();
        String vegNonVeg = chooseVegNonVeg.getSelectedItem().toString();
        String price = edtPrice.getText().toString();
        String priceType = choosePriceType.getSelectedItem().toString();
        String discount = edtDiscount.getText().toString();
        String category = chooseFoodCategory.getSelectedItem().toString();
        if(foodName.equals(""))
        {
            Toast.makeText(CreateFood.this,"Please enter FoodName",Toast.LENGTH_SHORT).show();
            return;
        }
        if(vegNonVeg.toLowerCase().equals("veg or non-veg"))
        {
            Toast.makeText(CreateFood.this,"Please select veg or non-veg",Toast.LENGTH_SHORT).show();
            return;
        }
        if(price.equals(""))
        {
            Toast.makeText(CreateFood.this,"Please enter price",Toast.LENGTH_SHORT).show();
            return;
        }
        if(priceType.toLowerCase().equals("choose price type"))
        {
            Toast.makeText(CreateFood.this,"Please select price type",Toast.LENGTH_SHORT).show();
            return;
        }
        if(category.toLowerCase().equals("food category"))
        {
            Toast.makeText(CreateFood.this,"Please select food category",Toast.LENGTH_SHORT).show();
            return;
        }
        if(foodImageFile==null)
        {
            Toast.makeText(CreateFood.this,"Please provide food image",Toast.LENGTH_SHORT).show();
            return;
        }

        sendDataToServer(foodName,vegNonVeg,price,priceType,discount,category);

    }


    public void sendDataToServer(String foodName, String vegNonVeg, String price,
                                 String priceType, String discount, String foodCategory) {


        String url = BASE_PRODUCT_CREATE;
        Log.e(TAG,"setDataToServer: called");

        progressDialog.show();
        btnAdd.setEnabled(false);
        AndroidNetworking.upload(url)
                .addMultipartFile("item_image", foodImageFile)
                .addMultipartParameter("item_name", foodName)
                .addMultipartParameter("item_price", price)
                .addMultipartParameter("veg_non_veg", vegNonVeg)
                .addMultipartParameter("category", foodCategory)
                .addMultipartParameter("price_type", priceType)
                .addMultipartParameter("discount", discount)
                .addMultipartParameter("partner_id", userId)
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
                                Toast.makeText(CreateFood.this, "food item created", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                                finish();

                            }
                            if(status==300)
                            {
                                Toast.makeText(CreateFood.this, response.toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                btnAdd.setEnabled(true);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();

                            progressDialog.dismiss();
                            btnAdd.setEnabled(true);
                            Log.e(TAG, e.toString());
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, error.getErrorBody());

                        progressDialog.dismiss();
                        btnAdd.setEnabled(true);
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
        String tokenType = new LoginSessionManager(CreateFood.this).getUserDetailsFromSP().get(TOKEN_TYPE);
        String accessToken = new LoginSessionManager(CreateFood.this).getUserDetailsFromSP().get(ACCESS_TOKEN);


        header.put("Accept", "application/json");
        header.put("Authorization", tokenType + " " + accessToken);

        return header;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        HashMap<String, String> matchTypes = new HashMap<>();


        if (requestCode == PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            Intent cropIntent = CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(CreateFood.this);
            startActivityForResult(cropIntent, CROP_PROFILE);


        }


        if (requestCode == CROP_PROFILE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri();
                foodImageView.setImageURI(resultUri);

                foodImageFile  = new File(resultUri.getPath());



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(CreateFood.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}