package com.abishek.comidapartner.Home.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.fragment.model.OrderFoodModel;
import com.abishek.comidapartner.R;

import java.util.List;

public class OrderDetailFoodAdapter extends RecyclerView.Adapter<OrderDetailFoodAdapter.OrderDetailViewHolder> {

    private List<OrderFoodModel> foodList;
    private Context context;

    public OrderDetailFoodAdapter(List<OrderFoodModel> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_list_design2,parent,false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {

        holder.foodNameView.setText(foodList.get(position).getItemName());
        holder.quantityView.setText(foodList.get(position).getQuantity());
        int total = Integer.parseInt(foodList.get(position).getQuantity())*Integer.parseInt(foodList.get(position).getPrice());
        int discount = Integer.parseInt(foodList.get(position).getQuantity())*Integer.parseInt(foodList.get(position).getDiscount());
        holder.totalView.setText(total+"");
        holder.discountView.setText(discount+"");

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder{

        private TextView foodNameView,quantityView,discountView,totalView;

        public OrderDetailViewHolder(@NonNull View v) {
            super(v);

            foodNameView = v.findViewById(R.id.food_name);
            quantityView = v.findViewById(R.id.quantity);
            discountView = v.findViewById(R.id.discount);
            totalView = v.findViewById(R.id.total);
        }
    }
}
