package com.abishek.comidapartner.Home.foodMangement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.abishek.comidapartner.Home.foodMangement.CategoryModel;
import com.abishek.comidapartner.R;

import java.util.ArrayList;

public class ProductParentAdapter extends RecyclerView.Adapter<ProductParentAdapter.ProductParentHolder> {

    private ArrayList<CategoryModel> categoryList;
    private Context context;


    public ProductParentAdapter(ArrayList<CategoryModel> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;

    }

    @NonNull
    @Override
    public ProductParentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_food_management_parent,parent,false);
        return new ProductParentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductParentHolder holder, int position) {

        ProductChildAdapter productChildAdapter = new ProductChildAdapter(categoryList.get(position).getFoodList(),context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        holder.childRecycler.setLayoutManager(linearLayoutManager);
        holder.childRecycler.setAdapter(productChildAdapter);
        holder.categoryName.setText(categoryList.get(position).getCategoryName());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ProductParentHolder extends RecyclerView.ViewHolder{

        private RecyclerView childRecycler;
        private TextView categoryName;

        public ProductParentHolder(@NonNull View v) {
            super(v);

            childRecycler = v.findViewById(R.id.category_food_recycler);
            categoryName = v.findViewById(R.id.category_name);
        }
    }
}
