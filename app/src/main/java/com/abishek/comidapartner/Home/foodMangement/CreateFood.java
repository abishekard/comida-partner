package com.abishek.comidapartner.Home.foodMangement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;

import com.abishek.comidapartner.Home.adapter.SpinnerAdapter;
import com.abishek.comidapartner.R;

import java.util.ArrayList;

public class CreateFood extends AppCompatActivity {

    private Spinner choosePriceType,chooseFoodCategory,chooseVegNonVeg;
    private ArrayList<String> priceTypeList,foodCategoryList,vegNonVegList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food);

        choosePriceType = findViewById(R.id.choose_price_type);
        chooseFoodCategory = findViewById(R.id.food_category);
        chooseVegNonVeg= findViewById(R.id.veg_or_non_veg);

        priceTypeList = new ArrayList<>();
        priceTypeList.add("Choose Price Type");
        priceTypeList.add("Piece");
        priceTypeList.add("Plate");

        foodCategoryList = new ArrayList<>();
        foodCategoryList.add("Food Category");
        foodCategoryList.add("chicken");
        foodCategoryList.add("fish");
        foodCategoryList.add("panner");
        foodCategoryList.add("sweet");
        foodCategoryList.add("soup");
        foodCategoryList.add("snacks");
        foodCategoryList.add("desert");
        foodCategoryList.add("drinks");
        foodCategoryList.add("south Indian");
        foodCategoryList.add("pulse");
        foodCategoryList.add("rice");
        foodCategoryList.add("chappati");
        foodCategoryList.add("egg");
        foodCategoryList.add("mutton");

        vegNonVegList = new ArrayList<>();
        vegNonVegList.add("Veg OR Non-Veg");
        vegNonVegList.add("Veg");
        vegNonVegList.add("Non Veg");

        choosePriceType.setAdapter(new SpinnerAdapter(CreateFood.this,0,priceTypeList));
        chooseFoodCategory.setAdapter(new SpinnerAdapter(CreateFood.this,0,foodCategoryList));
        chooseVegNonVeg.setAdapter(new SpinnerAdapter(CreateFood.this,0,vegNonVegList));


    }
}