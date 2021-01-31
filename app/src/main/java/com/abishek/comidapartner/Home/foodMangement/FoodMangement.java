package com.abishek.comidapartner.Home.foodMangement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.abishek.comidapartner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FoodMangement extends AppCompatActivity {

    private FloatingActionButton createFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_mangement);

        createFood = findViewById(R.id.create_food);
        createFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodMangement.this,CreateFood.class));
            }
        });
    }
}