package com.abishek.comidapartner.Home.deliveryPartner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.HomePage;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_GET_DELIVERY_PARTNER;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_REMOVE_DELIVERY_PARTNER;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

public class DeliveryPartnerAdapter extends RecyclerView.Adapter<DeliveryPartnerAdapter.DeliveryPartnerHolder> {

    private String TAG = "DeliveryPartnerAdapter";
    private List<DeliveryPartnerModel> partnerList;
    private Context context;

    public DeliveryPartnerAdapter(List<DeliveryPartnerModel> partnerList, Context context) {
        this.partnerList = partnerList;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryPartnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_partner_recycler_item, parent, false);
        return new DeliveryPartnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryPartnerHolder holder, int position) {

        holder.nameView.setText(partnerList.get(position).getName());
        holder.mobileView.setText(partnerList.get(position).getMobile());
        if(!partnerList.get(position).getProfileImage().equals("null"))
            Picasso.get().load(BASE_IMAGE+partnerList.get(position).getProfileImage()).into(holder.profileImageView);
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLogOutConfirmationDialog(partnerList.get(position).getId(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return partnerList.size();
    }

    public class DeliveryPartnerHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView mobileView;
        private TextView btnRemove;
        private ImageView profileImageView;

        public DeliveryPartnerHolder(@NonNull View v) {
            super(v);

            nameView = v.findViewById(R.id.name);
            mobileView = v.findViewById(R.id.mobile);
            btnRemove = v.findViewById(R.id.btn_remove);
            profileImageView = v.findViewById(R.id.profile_image);
        }
    }

    public void removeDeliveryPartner(String deliveryPartnerId,int position)
    {

        Log.e(TAG, "fetchAllProductList : called");

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String URL = BASE_REMOVE_DELIVERY_PARTNER;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");

                    if(status==200)
                    {
                        Toast.makeText(context,"delivery partner removed",Toast.LENGTH_SHORT).show();
                        partnerList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,partnerList.size());
                    }

                    progressDialog.dismiss();



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"server problem",Toast.LENGTH_SHORT).show();

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

                String tokenType = new LoginSessionManager(context).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(context).getUserDetailsFromSP().get(ACCESS_TOKEN);

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);
                Log.e(TAG,"Authorization  "+tokenType + " " + accessToken);


                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> params=new HashMap<>();
                params.put("delivery_partner_id",deliveryPartnerId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    void openLogOutConfirmationDialog(String deliveryPartnerId,int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to Remove Your Delivery Partner ?");
        builder.setTitle("Confirm");

        builder.setPositiveButton("Yes", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                removeDeliveryPartner(deliveryPartnerId,position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
