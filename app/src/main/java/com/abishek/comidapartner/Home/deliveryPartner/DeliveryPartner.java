package com.abishek.comidapartner.Home.deliveryPartner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.androidnetworking.model.Progress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ADD_DELIVERY_PARTNER;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_GET_DELIVERY_PARTNER;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_PROFILE_SHOW;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class DeliveryPartner extends AppCompatActivity {

    private static final String TAG ="DeliveryPartner" ;
    private FloatingActionButton addDeliveryPartner;
    private String userId;
    private ProgressBar progressBar;
    private List<DeliveryPartnerModel> deliveryPartnerList;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_partner);

        getUserInfo();

        addDeliveryPartner = findViewById(R.id.add_delivery_partner);
        progressBar = findViewById(R.id.progress_bar);
        addDeliveryPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMobileNumber();
            }
        });
        fetchDeliveryPartner();

        progressDialog = new ProgressDialog(DeliveryPartner.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    private void getMobileNumber() {

        final AlertDialog alertDialog;

        alertDialog = new AlertDialog.Builder(DeliveryPartner.this,
                R.style.setting_dialog).create();

        LayoutInflater inflater = LayoutInflater.from(DeliveryPartner.this);
        final View view = inflater.inflate(R.layout.add_delivery_partner, null);

        alertDialog.setView(view);

       // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        EditText edtMobile = view.findViewById(R.id.mobile);
        Button btnAdd = view.findViewById(R.id.btn_add);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = edtMobile.getText().toString();
                Log.e(TAG,mobile);

                if(mobile.equals("")||edtMobile.length()<10) {
                    Toast.makeText(DeliveryPartner.this, "invalid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDeliveryPartner(mobile);
                alertDialog.dismiss();

            }
        });

      //  alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();
    }


    public void fetchDeliveryPartner() {


        Log.e(TAG, "fetchAllProductList : called");

        final String URL = BASE_GET_DELIVERY_PARTNER;
        deliveryPartnerList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");

                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()==0)
                    {
                        Toast.makeText(DeliveryPartner.this,"no delivery partner registered",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject childJson = data.getJSONObject(i);
                        String name=childJson.getString("name");
                        String mobile=childJson.getString("mobile");
                        String aadharNumber=childJson.getString("aadhar_number");
                        String profileImage=childJson.getString("profile_image");
                        String id = childJson.getString("id");

                        deliveryPartnerList.add(new DeliveryPartnerModel(name,id,mobile,aadharNumber,profileImage));

                    }

                    setDataToView();



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DeliveryPartner.this,"server problem",Toast.LENGTH_SHORT).show();

                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, error.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(DeliveryPartner.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(DeliveryPartner.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);
                Log.e(TAG,"Authorization  "+tokenType + " " + accessToken);


                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> params=new HashMap<>();
                params.put("id",userId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(DeliveryPartner.this).addToRequestQueue(stringRequest);


    }

    private void setDataToView() {

        RecyclerView deliveryPartnerRecycler = findViewById(R.id.delivery_partner_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DeliveryPartner.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        DeliveryPartnerAdapter adapter = new DeliveryPartnerAdapter(deliveryPartnerList,DeliveryPartner.this);
        deliveryPartnerRecycler.setLayoutManager(linearLayoutManager);
        deliveryPartnerRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }

    public void getUserInfo() {
        LoginSessionManager loginSessionManager = new LoginSessionManager(DeliveryPartner.this);
        HashMap<String, String> user = loginSessionManager.getUserDetailsFromSP();

        userId = user.get("user_id");
        Log.e(TAG, "......profileImage: " + user.get("profile_image"));
    }



    public void addDeliveryPartner(String mobile) {


        Log.e(TAG, "fetchAllProductList : called");

        progressDialog.show();
        final String URL = BASE_ADD_DELIVERY_PARTNER;
        deliveryPartnerList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");


                    if(status==200)
                    {
                        Toast.makeText(DeliveryPartner.this,"delivery partner added",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        fetchDeliveryPartner();
                        return;
                    }
                    if(status==350)
                    {
                        Toast.makeText(DeliveryPartner.this,"delivery partner not found",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DeliveryPartner.this,"server problem",Toast.LENGTH_SHORT).show();

                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, error.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(DeliveryPartner.this).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(DeliveryPartner.this).getUserDetailsFromSP().get(ACCESS_TOKEN);

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);
                Log.e(TAG,"Authorization  "+tokenType + " " + accessToken);


                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> params=new HashMap<>();
                params.put("mobile",mobile);
                params.put("partner_id",userId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(DeliveryPartner.this).addToRequestQueue(stringRequest);


    }

}