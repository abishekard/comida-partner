package com.abishek.comidapartner.loginAndSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.abishek.comidapartner.Home.profile.StoreDetailPage;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_GET_SHOP;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class AddShopDetails extends AppCompatActivity implements View.OnClickListener, TimeChooser.TimePickerListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int CROP_REQUEST = 188;
    private static final int MY_CAMERA_PERMISSION_CODE = 111;
    private static final String TAG = "AddShopDetails";

    private Button btnProceed;
    private ImageView aadharFrontView, aadharBackView, shopImageView;
    private EditText edtOwnerName, edtShopName, edtSpeciality, edtGstNumber, edtAadharNumber;
    private TextView txtOpenTime, txtCloseTime;
    private String userId;
    private File aadharFrontFile, aadharBackFile, shopFile;
    int currentImageView = 0;
    private String openTime, closeTime;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_details);

        inItUi();
        getUserIdAndName();
        progressDialog = new ProgressDialog(AddShopDetails.this);
        progressDialog.setMessage("wait...");
        progressDialog.setCancelable(false);
    }

    public void inItUi() {
        btnProceed = findViewById(R.id.proceed);
        aadharBackView = findViewById(R.id.aadhar_back);
        aadharFrontView = findViewById(R.id.aadhar_front);
        shopImageView = findViewById(R.id.shop_image);

        edtOwnerName = findViewById(R.id.owner_name);
        edtAadharNumber = findViewById(R.id.aadhar_number);
        edtGstNumber = findViewById(R.id.gst_number);
        edtShopName = findViewById(R.id.shop_name);
        edtSpeciality = findViewById(R.id.shop_speciality);

        txtCloseTime = findViewById(R.id.close_time);
        txtOpenTime = findViewById(R.id.open_time);

        aadharFrontView.setOnClickListener(this);
        aadharBackView.setOnClickListener(this);
        shopImageView.setOnClickListener(this);
        txtOpenTime.setOnClickListener(this);
        txtCloseTime.setOnClickListener(this);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(AddShopDetails.this,AddNewAddress.class));
                getDataFromUi();
            }
        });
    }

    public void getDataFromUi() {

        String shopName = edtShopName.getText().toString();
        String openTimee = txtOpenTime.getText().toString();
        String closeTimee = txtCloseTime.getText().toString();
        String shopSpeciality = edtSpeciality.getText().toString();
        String gstNumber = edtGstNumber.getText().toString();
        String aadharNumber = edtAadharNumber.getText().toString();

        if (shopName.equals("")) {
            Toast.makeText(AddShopDetails.this, "Please enter Shop name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (openTimee.equals("00:00 AM")) {
            Toast.makeText(AddShopDetails.this, "Please enter Shop open time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (closeTimee.equals("00:00 PM")) {
            Toast.makeText(AddShopDetails.this, "Please enter shop close time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shopSpeciality.equals("")) {
            Toast.makeText(AddShopDetails.this, "Please enter Shop speciality", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gstNumber.equals("")) {
            Toast.makeText(AddShopDetails.this, "Please enter gst number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aadharNumber.equals("")) {
            Toast.makeText(AddShopDetails.this, "Please enter aadhar number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (aadharFrontFile == null || aadharBackFile == null) {
            Toast.makeText(AddShopDetails.this, "Please provide aadhar Image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (shopFile == null) {
            Toast.makeText(AddShopDetails.this, "Please provide shop image", Toast.LENGTH_SHORT).show();
            return;
        }

        sendDataToServer(shopName,shopSpeciality,gstNumber,aadharNumber,openTime,closeTime);

    }

    public void clickImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(AddShopDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddShopDetails.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                }
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //   performCrop(photo);

            Intent cropIntent = CropImage.activity(getImageUri(photo))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(AddShopDetails.this);
            startActivityForResult(cropIntent, CROP_REQUEST);


        }

        if (requestCode == CROP_REQUEST && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();

            switch (currentImageView) {
                case 1:
                    aadharBackView.setImageURI(resultUri);
                    aadharBackFile = new File(resultUri.getPath());


                    break;
                case 2:

                    aadharFrontView.setImageURI(resultUri);
                    aadharFrontFile = new File(resultUri.getPath());
                    break;
                case 3:
                    shopImageView.setImageURI(resultUri);
                    shopFile = new File(resultUri.getPath());
                    break;

            }


        }


    }

    public Uri getImageUri(Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(AddShopDetails.this.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public File createImageFile(Bitmap bitmap) {
        //  Bitmap mIdBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

        Uri mIduri = getImageUri(bitmap);

        String mIdRealPath = getRealPathFromURI(mIduri);
        File file = new File(mIdRealPath);

        Log.e(TAG, " file name : " + file.getName());

        return file;


    }


    public void getUserIdAndName() {
        LoginSessionManager loginSessionManager = new LoginSessionManager(AddShopDetails.this);

        HashMap<String, String> user = loginSessionManager.getUserDetailsFromSP();
        userId = user.get("user_id");
        edtOwnerName.setText(user.get("name"));
        Log.e(TAG, "userId: " + userId + "  " + user.get("name"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aadhar_back:
                currentImageView = 1;
                clickImage();
                break;
            case R.id.aadhar_front:
                currentImageView = 2;
                clickImage();
                break;
            case R.id.shop_image:
                currentImageView = 3;
                clickImage();
                break;
            case R.id.open_time:
                TimeChooser openTimeChooser = new TimeChooser("open");
                openTimeChooser.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.close_time:
                TimeChooser closeTimeChooser = new TimeChooser("close");
                closeTimeChooser.show(getSupportFragmentManager(), "timePicker");
                break;
        }
    }

    @Override
    public void getTime(String time, String completeTime, String type) {

        if (type.equals("open")) {
            txtOpenTime.setText(time);
            openTime = completeTime;
        }
        if (type.equals("close")) {
            txtCloseTime.setText(time);
            closeTime = completeTime;
        }
        Log.e(TAG, completeTime);

    }

/*

    private void performCrop(Bitmap photo) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)

            Uri picUri = getImageUri(photo) ;
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1.5);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_REQUEST);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
*/


    public void sendDataToServer(String shopName, String speciality, String gstNumber, String aadharNum, String openTime, String closeTime) {


        String url = BASE_GET_SHOP;
        Log.e(TAG,"setDataToServer: called");

        progressDialog.show();
        AndroidNetworking.upload(url)
                .addMultipartFile("aadhar_front", aadharFrontFile)
                .addMultipartFile("aadhar_back", aadharBackFile)
                .addMultipartFile("shop_image", shopFile)
                .addMultipartParameter("id", userId)
                .addMultipartParameter("shop_name", shopName)
                .addMultipartParameter("speciality", speciality)
                .addMultipartParameter("gst_number", gstNumber)
                .addMultipartParameter("aadhar_number", aadharNum)
                .addMultipartParameter("open_time", openTime)
                .addMultipartParameter("close_time", closeTime)
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
                                Toast.makeText(AddShopDetails.this, "Shop Detail added successfully", Toast.LENGTH_SHORT).show();

                                new LoginSessionManager(AddShopDetails.this).addShopDetail(shopName);
                                startActivity(new Intent(AddShopDetails.this,AddNewAddress.class));
                                progressDialog.dismiss();
                                finish();

                            }
                            if(status==300)
                            {
                                Toast.makeText(AddShopDetails.this, "gst or Aadhar already registered.", Toast.LENGTH_SHORT).show();
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
        String tokenType = new LoginSessionManager(AddShopDetails.this).getUserDetailsFromSP().get(TOKEN_TYPE);
        String accessToken = new LoginSessionManager(AddShopDetails.this).getUserDetailsFromSP().get(ACCESS_TOKEN);


        header.put("Accept", "application/json");
        header.put("Authorization", tokenType + " " + accessToken);

        return header;
    }


}