package com.abishek.comidapartner.Home.fragment.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.deliveryPartner.DeliveryPartner;
import com.abishek.comidapartner.Home.deliveryPartner.DeliveryPartnerAdapter;
import com.abishek.comidapartner.Home.deliveryPartner.DeliveryPartnerModel;
import com.abishek.comidapartner.Home.fragment.model.MyOrderModel;
import com.abishek.comidapartner.Home.fragment.OrderDetail;
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
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_DISPATCH;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_QUEUE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.getDate;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.getTime;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;


public class OrderAdapter2 extends RecyclerView.Adapter<OrderAdapter2.OrderViewHolder> {

    private List<MyOrderModel> orderList;
    private Context context;
    private String from;
    private final String TAG="OrderAdapter2";
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    public OrderAdapter2(List<MyOrderModel> orderList, Context context, String from) {
        this.orderList = orderList;
        this.context = context;
        this.from=from;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_list_design3,parent,false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        String from1=from;
        if(orderList.get(position).getStatus().equals("3"))
        {
            holder.btnDispatch.setVisibility(View.GONE);
            from1="history";
        }
        String dateTime[] = orderList.get(position).getCreateAt().split(" ");
        String formattedDateTime = getDate(dateTime[0])+"  "+getTime(dateTime[1]);

        holder.orderIdView.setText("#"+orderList.get(position).getOrderId());
        holder.dateView.setText(formattedDateTime);
        holder.priceView.setText(orderList.get(position).getTotalPrice());
        holder.addressView.setText(orderList.get(position).getDeliveredAddress());
        Picasso.get().load(BASE_IMAGE+orderList.get(position).getImage()).into(holder.foodImageView);

        String finalFrom = from1;
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, OrderDetail.class).putExtra("from", finalFrom)
                        .putExtra("order_id",orderList.get(position).getOrderId()));
            }
        });

        holder.btnDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // changeOrderStatus(position,orderList.get(position).getOrderId());
                openDeliveryPartnerDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        private TextView orderIdView,dateView,addressView,priceView;
        private ImageView foodImageView;
        private LinearLayout card;
        private Button btnDispatch;

        public OrderViewHolder(@NonNull View v) {
            super(v);
            orderIdView = v.findViewById(R.id.order_id);
            dateView = v.findViewById(R.id.date);
            addressView = v.findViewById(R.id.delivered_address);
            priceView = v.findViewById(R.id.total);
            foodImageView = v.findViewById(R.id.food_image);
            card = v.findViewById(R.id.card);
            btnDispatch = v.findViewById(R.id.btn_dispatch);
        }
    }



    public void changeOrderStatus(int position,String orderId,String dpId) {
        String URL = BASE_ORDER_DISPATCH;
        progressDialog.show();

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

                        Toast.makeText(context,"order Dispatched",Toast.LENGTH_SHORT).show();
                        orderList.get(position).setStatus("3");
                        notifyDataSetChanged();
                        progressDialog.dismiss();

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

                progressDialog.dismiss();
                Toast.makeText(context, "server problem", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(context).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(context).getUserDetailsFromSP().get(ACCESS_TOKEN);

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);

                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("order_id",orderId);
                params.put("delivery_partner_id",dpId);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }

    private void openDeliveryPartnerDialog(int position) {



        alertDialog = new AlertDialog.Builder(context,
                R.style.setting_dialog).create();

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.delivery_partner_selecttion_dialog, null);

        alertDialog.setView(view);

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        fetchDeliveryPartner(view,position);

        alertDialog.show();
    }

    public void fetchDeliveryPartner(View view,int position) {


        Log.e(TAG, "fetchAllProductList : called");
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        String userId = new LoginSessionManager(context).getUserDetailsFromSP().get("user_id");
        final String URL = BASE_GET_DELIVERY_PARTNER;
        List<DeliveryPartnerModel> deliveryPartnerList = new ArrayList<>();

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
                        Toast.makeText(context,"no delivery partner registered",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        alertDialog.dismiss();
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

                    setDataToView(deliveryPartnerList,view,progressBar,position);



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"server problem",Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
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
                params.put("id",userId);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }

    private void setDataToView(List<DeliveryPartnerModel> deliveryPartnerList,View view,ProgressBar progressBar,int position) {

        RecyclerView deliveryPartnerRecycler = view.findViewById(R.id.delivery_partner_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        DeliveryPartnerAdapter1 adapter = new DeliveryPartnerAdapter1(deliveryPartnerList,context, new PartnerSelectionListener() {
            @Override
            public void selectedPosition(int index,String deliveryPartnerId) {
                Log.e(TAG,"........"+deliveryPartnerId);
                alertDialog.dismiss();
                changeOrderStatus(position,orderList.get(position).getOrderId(),deliveryPartnerId);
            }
        });
        deliveryPartnerRecycler.setLayoutManager(linearLayoutManager);
        deliveryPartnerRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }

    public interface PartnerSelectionListener{
        void selectedPosition(int position,String deliveryPartnerId);
    }

}
