package com.abishek.comidapartner.Home.fragment.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.fragment.model.MyOrderModel;
import com.abishek.comidapartner.Home.fragment.OrderDetail;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_DISPATCH;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_QUEUE;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;


public class OrderAdapter3 extends RecyclerView.Adapter<OrderAdapter3.OrderViewHolder> {

    private List<MyOrderModel> orderList;
    private Context context;
    private String from;
    private final String TAG="OrderAdapter2";
    private ProgressDialog progressDialog;

    public OrderAdapter3(List<MyOrderModel> orderList, Context context, String from) {
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_list_design4,parent,false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {


        holder.orderIdView.setText("#"+orderList.get(position).getOrderId());
        holder.dateView.setText(orderList.get(position).getCreateAt());
        holder.priceView.setText(orderList.get(position).getTotalPrice());
        holder.addressView.setText(orderList.get(position).getDeliveredAddress());
        Picasso.get().load(BASE_IMAGE+orderList.get(position).getImage()).into(holder.foodImageView);


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, OrderDetail.class).putExtra("from", from)
                        .putExtra("order_id",orderList.get(position).getOrderId()));
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


        public OrderViewHolder(@NonNull View v) {
            super(v);
            orderIdView = v.findViewById(R.id.order_id);
            dateView = v.findViewById(R.id.date);
            addressView = v.findViewById(R.id.delivered_address);
            priceView = v.findViewById(R.id.total);
            foodImageView = v.findViewById(R.id.food_image);
            card = v.findViewById(R.id.card);

        }
    }




}
