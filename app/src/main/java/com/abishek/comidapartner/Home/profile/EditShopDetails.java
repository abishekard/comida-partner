package com.abishek.comidapartner.Home.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.abishek.comidapartner.loginAndSignUp.AddNewAddress;
import com.abishek.comidapartner.loginAndSignUp.AddShopDetails;
import com.abishek.comidapartner.loginAndSignUp.TimeChooser;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_GET_SHOP;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_EDIT;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_SHOW;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class EditShopDetails extends AppCompatActivity implements View.OnClickListener, TimeChooser.TimePickerListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int CROP_REQUEST = 188;
    private static final int GALLERY_REQUEST = 111;

    private static final String TAG = "EditDetails";
    private EditText edtOwnerName, edtShopName,edtSpeciality;
    private TextView txtOpenTime, txtCloseTime;
    private String openTime, closeTime;
    private ProgressDialog progressDialog;
    private ImageView shopImageView;
    private String userId;
    private File shopFile;
    private Button btnUpdate;
    private HashMap<String,String> dataToUpload;
    private HashMap<String,File> fileToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop_details);
        inItUi();
        getUserIdAndName();
        fetchProfile();
        progressDialog = new ProgressDialog(EditShopDetails.this);
        progressDialog.setMessage("wait...");
        progressDialog.setCancelable(false);
        dataToUpload = new HashMap<>();
        fileToUpload = new HashMap<>();
    }

    public void inItUi() {

        edtOwnerName = findViewById(R.id.owner_name);
        edtShopName = findViewById(R.id.shop_name);

        txtCloseTime = findViewById(R.id.close_time);
        txtOpenTime = findViewById(R.id.open_time);

        shopImageView = findViewById(R.id.shop_image);
        btnUpdate = findViewById(R.id.update_profile);
        edtSpeciality = findViewById(R.id.shop_speciality);

        txtOpenTime.setOnClickListener(this);
        txtCloseTime.setOnClickListener(this);
        shopImageView.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.open_time:
                TimeChooser openTimeChooser = new TimeChooser("open");
                openTimeChooser.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.close_time:
                TimeChooser closeTimeChooser = new TimeChooser("close");
                closeTimeChooser.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.shop_image:

                if (ContextCompat.checkSelfPermission(EditShopDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditShopDetails.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }


                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag", 1);

                startActivityForResult(gallaryIntent, GALLERY_REQUEST);
                break;
            case R.id.update_profile: getDataFromUi();
                break;
        }
    }

    @Override
    public void getTime(String time, String completeTime, String type) {
        if (type.equals("open")) {
            txtOpenTime.setText(time);
            openTime = completeTime;
            dataToUpload.put("open_time",completeTime);
        }
        if (type.equals("close")) {
            txtCloseTime.setText(time);
            closeTime = completeTime;
            dataToUpload.put("close_time",completeTime);
        }
        Log.e(TAG, completeTime);
    }

    public void getUserIdAndName() {
        LoginSessionManager loginSessionManager = new LoginSessionManager(EditShopDetails.this);

        HashMap<String, String> user = loginSessionManager.getUserDetailsFromSP();
        userId = user.get("user_id");
        edtOwnerName.setText(user.get("name"));
        Log.e(TAG, "userId: " + userId + "  " + user.get("name"));

    }


    public void fetchProfile() {


        Log.e(TAG, "fetchProfile : called");

        final String URL = BASE_PROFILE_SHOW + userId;


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
                    String SopenTime = childJson.getString("open_time");
                    String ScloseTime = childJson.getString("close_time");
                    String available = childJson.getString("available");
                    String name = childJson.getString("name");


                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                    Date dtOpen,dtClose;
                    try {
                        dtOpen = sdf.parse(SopenTime);
                        dtClose = sdf.parse(ScloseTime);
                        txtOpenTime.setText(sdfs.format(dtOpen));
                        txtCloseTime.setText(sdfs.format(dtClose));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    edtShopName.setText(shopName);
                    edtSpeciality.setText(speciality);

                    Picasso.get().load(BASE_IMAGE + shopImage).into(shopImageView);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(EditShopDetails.this, "server problem", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(EditShopDetails.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(EditShopDetails.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

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
        MySingleton.getInstance(EditShopDetails.this).addToRequestQueue(stringRequest);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {



            Intent cropIntent = CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(EditShopDetails.this);
            startActivityForResult(cropIntent, CROP_REQUEST);


        }

        if (requestCode == CROP_REQUEST && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            shopImageView.setImageURI(resultUri);
            shopFile = new File(resultUri.getPath());
            fileToUpload.put("shop_image",shopFile);


        }


    }

    public Uri getImageUri(Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(EditShopDetails.this.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void getDataFromUi()
    {
        String speciality = edtSpeciality.getText().toString();

        dataToUpload.put("speciality",speciality);

        Log.e(TAG,"userid......"+userId);
        sendDataToServer();

    }

    public void sendDataToServer() {


        String url = BASE_PROFILE_EDIT;
        Log.e(TAG, "setDataToServer: called");

        progressDialog.show();
        AndroidNetworking.upload(url)
                .addMultipartParameter("id", userId)
                .addMultipartParameter(dataToUpload)
                .addMultipartFile(fileToUpload)
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
                                Toast.makeText(EditShopDetails.this, "Shop Detail Updated", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();

                            }
                            if (status == 300) {
                                Toast.makeText(EditShopDetails.this, "Server error", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            progressDialog.dismiss();
                            Log.e(TAG, e.toString());
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, error.getErrorBody());

                        progressDialog.dismiss();
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
        String tokenType = new LoginSessionManager(EditShopDetails.this).getUserDetailsFromSP().get(TOKEN_TYPE);
        String accessToken = new LoginSessionManager(EditShopDetails.this).getUserDetailsFromSP().get(ACCESS_TOKEN);


        header.put("Accept", "application/json");
        header.put("Authorization", tokenType + " " + accessToken);

        return header;
    }
}