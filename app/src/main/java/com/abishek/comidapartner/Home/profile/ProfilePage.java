package com.abishek.comidapartner.Home.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_EDIT;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_SHOW;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class ProfilePage extends AppCompatActivity {

    public final String TAG = "ProfilePage";
    private int PROFILE_REQUEST = 100;
    private int CROP_PROFILE = 101;

    private TextView btnStoreDetail;

    private TextView nameView, emailView, mobileView, topNameView;

    private ImageView profileImageView;
    private String userId;
    private TextView editProfileImage;
    private SwitchCompat availableSwitch;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        btnStoreDetail = findViewById(R.id.store_detail);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        mobileView = findViewById(R.id.mobile);
        topNameView = findViewById(R.id.top_name);
        availableSwitch = findViewById(R.id.available_switch);
        profileImageView = findViewById(R.id.profile_image);
        editProfileImage = findViewById(R.id.edit_profile);

        btnStoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, StoreDetailPage.class));
            }
        });

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProfilePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfilePage.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }


                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag", 1);

                startActivityForResult(gallaryIntent, PROFILE_REQUEST);
            }
        });

        availableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(availableSwitch.isChecked())
                    changeAvailability("1");
                if(!availableSwitch.isChecked())
                    changeAvailability("2");
            }
        });

        getUserInfo();

        fetchProfile();

        progressDialog = new ProgressDialog(ProfilePage.this);
        progressDialog.setMessage("Wait....");
        progressDialog.setCancelable(false);
    }


    public void getUserInfo() {
        LoginSessionManager loginSessionManager = new LoginSessionManager(ProfilePage.this);
        HashMap<String, String> user = loginSessionManager.getUserDetailsFromSP();
        nameView.setText(user.get("name"));
        mobileView.setText(user.get("mobile"));
        emailView.setText(user.get("email"));
        topNameView.setText("Hi " + user.get("name") + " !");
        if (!user.get("profile_image").equals("null"))
            Picasso.get().load(BASE_IMAGE + user.get("profile_image")).into(profileImageView);

        userId = user.get("user_id");
        Log.e(TAG, "......profileImage: " + user.get("profile_image"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        HashMap<String, String> matchTypes = new HashMap<>();


        if (requestCode == PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            Intent cropIntent = CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(ProfilePage.this);
            startActivityForResult(cropIntent, CROP_PROFILE);


        }


        if (requestCode == CROP_PROFILE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri();
                profileImageView.setImageURI(resultUri);


                Bitmap mIdBitmap = null;
                try {
                    mIdBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                String imageName = "mekvahan" + ".jpg";

                Uri mIduri = getImageUri(mIdBitmap);
                String mIdRealPath = getRealPathFromURI(mIduri);


                File file = new File(mIdRealPath);
//
                sendDataToServer(new File(resultUri.getPath()));


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ProfilePage.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public Uri getImageUri(Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void sendDataToServer(File file) {

        Log.e(TAG, "sendDataToServer : called......." + userId);


        AndroidNetworking.upload(BASE_PROFILE_EDIT)
                .addMultipartFile("profile_image", file)
                .addMultipartParameter("id", userId)
                .addHeaders(getHeader())
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        Log.e(TAG, "upload Processing....");
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());


                        try {


                            int rc = response.getInt("status");
                            if (rc != 200) {
                                Toast.makeText(ProfilePage.this, "server error", Toast.LENGTH_SHORT).show();

                                return;
                            }


                            Log.e(TAG, "Image updated");

                            Toast.makeText(ProfilePage.this, "profile Image updated", Toast.LENGTH_SHORT).show();



                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.e(TAG, e.toString());
                        }


                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e(TAG, error.getErrorBody());

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
        String tokenType = new LoginSessionManager(ProfilePage.this).getUserDetailsFromSP().get(TOKEN_TYPE);
        String accessToken = new LoginSessionManager(ProfilePage.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

        header.put("Accept", "application/json");
        header.put("Authorization", tokenType + " " + accessToken);
        Log.e(TAG, "Authorization " + tokenType + " " + accessToken);

        return header;
    }


    public void fetchProfile() {


        Log.e(TAG, "fetchAllProductList : called");

        final String URL = BASE_PROFILE_SHOW + userId;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");

                    JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);

                    String profileImage = data.getString("profile_image");
                    int available = data.getInt("available");

                    if(available==1)
                    {
                        availableSwitch.setChecked(true);
                    }


                    if (!profileImage.equals("null")) {
                        Picasso.get().load(BASE_IMAGE + profileImage).into(profileImageView);
                        new LoginSessionManager(ProfilePage.this).addProfileImage(profileImage);
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


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(ProfilePage.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(ProfilePage.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

                //String fullKey = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjcxNDdmNGFjNWFlN2IzZDM4MmYyNTAwNWVhNTIwOGUyNDAzNjYwNzMyOWMyYjZiYWQ1YTlhMmNlZTEzZDI3ZTgzN2RkOTY5NzcxNWNhMzUxIn0.eyJhdWQiOiIxIiwianRpIjoiNzE0N2Y0YWM1YWU3YjNkMzgyZjI1MDA1ZWE1MjA4ZTI0MDM2NjA3MzI5YzJiNmJhZDVhOWEyY2VlMTNkMjdlODM3ZGQ5Njk3NzE1Y2EzNTEiLCJpYXQiOjE1NTExOTc0MjcsIm5iZiI6MTU1MTE5NzQyNywiZXhwIjoxNTgyNzMzNDI3LCJzdWIiOiI4NSIsInNjb3BlcyI6W119.kLmk7mEukKdoS9e_v31VQX29ypn7hJb7qAJvKA_GqeiYEYe2EQ9zLTd1IwO-S31CofoypnJ-LvAT7D4I0EZ9iYM1AS5A6-7bWH3-h01-glLQubbfedhvlg0xfT60s2r1onxlEMUnt-0kB2tbYgX_df4zJPExUhHRpzlnLNChzC3r1QD1dzgn-814GjxlQkwfgv_5dsKzyMlvVCHiTDg2z35h2uiWeRuVhmznbUGaGCWcxPwHpNV4k9pHOH9yrCwkjJuHlcSIiXD7W_QsRnzEa_dY6wASdymtGqHb99c3kfWmiKKwngAC9GY56OeMP0vLnYpXOAspu5rDlQkLCzCeh58KnqbqMUrQ0bZ3ChTaeATXM_fncQiByfMgAAfiVfu8GpKsnQKSYobzcqrqjmAgPTNEcq5ba4BCUuw1ysv0LodTqHGUHsSNsiZfx3GyqLoyOCMWY5oWO4M4saOTo3pUSGPSq15BsqRQXqbvzshxk9ysaAU1K9dZj-AZpy4mUxf3y4UX8-EADqJmYV7ywEph_FveDbdWNNUF72bqbTg8DTxwJ6V53cEOsxbmNb82jFJnz1vSxLFDDXv9Vvf23W5hm4Io2Ogxv8wyE5vNUgL2XepFrGwWWANEsp4fLebzfgFD3045vkrcfRPc164LVKHdLyaHhxB8TrYeK9TOqeEfk7M";

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);


                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return super.getParams();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(ProfilePage.this).addToRequestQueue(stringRequest);


    }


    public void changeAvailability(String available) {


        String url = BASE_PROFILE_EDIT;
        Log.e(TAG, "setDataToServer: called "+available);

        progressDialog.show();
        AndroidNetworking.upload(url)
                .addMultipartParameter("id", userId)
                .addMultipartParameter("available",available)
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
                                Toast.makeText(ProfilePage.this, "Updated", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();


                            }
                            if (status == 300) {
                                Toast.makeText(ProfilePage.this, "Server error", Toast.LENGTH_SHORT).show();
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



}