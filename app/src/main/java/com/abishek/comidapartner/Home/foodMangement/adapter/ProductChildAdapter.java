package com.abishek.comidapartner.Home.foodMangement.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.foodMangement.FoodModel;
import com.abishek.comidapartner.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;


public class ProductChildAdapter extends RecyclerView.Adapter<ProductChildAdapter.ProductChildHolder> {

    private List<FoodModel> foodList, cartFoodList;
    private Context context;

    private String TAG = "productChildAdapter";

    public ProductChildAdapter(ArrayList<FoodModel> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;




    }

    @NonNull
    @Override
    public ProductChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_food_management, parent, false);


        return new ProductChildHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductChildHolder holder, int position) {



        holder.foodNameView.setText(foodList.get(position).getFoodName());
        holder.priceView.setText("₹" + foodList.get(position).getPrice() + "/" + foodList.get(position).getPrice_type());
        Picasso.get().load(BASE_IMAGE + foodList.get(position).getFoodImage()).into(holder.foodImageView);
        holder.discountView.setText(foodList.get(position).getDiscount());
        if(foodList.get(position).getVegNonVeg().toLowerCase().equals("veg"))
        {
            holder.vegNonVeg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_veg));
        }
        if(foodList.get(position).getVegNonVeg().toLowerCase().equals("non veg"))
        {
            holder.vegNonVeg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_non_veg));
        }
        if(foodList.get(position).getInStock()==1)
        {
            holder.inStockView.setChecked(true);
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


    public static class ProductChildHolder extends RecyclerView.ViewHolder {

        private TextView foodNameView;
        private TextView priceView,btnEdit,btnDelete,discountView;;
        private ImageView foodImageView,vegNonVeg;
        private SwitchCompat inStockView;


        public ProductChildHolder(@NonNull View v) {
            super(v);

            foodNameView = v.findViewById(R.id.food_name);
            priceView = v.findViewById(R.id.price);
            foodImageView = v.findViewById(R.id.food_image);
            inStockView = v.findViewById(R.id.in_stock);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
            discountView = v.findViewById(R.id.discount_view);
            vegNonVeg = v.findViewById(R.id.veg_or_non_veg);

        }
    }


}
