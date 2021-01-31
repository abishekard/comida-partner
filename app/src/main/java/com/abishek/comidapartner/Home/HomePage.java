package com.abishek.comidapartner.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abishek.comidapartner.Home.foodMangement.FoodMangement;
import com.abishek.comidapartner.Home.profile.ProfilePage;
import com.abishek.comidapartner.Home.sales.Sales;
import com.abishek.comidapartner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private ImageView botNavProfile,botNavFoodManagement,botNavSale,botNavMenu;
    private FloatingActionButton fabHome;
    private DrawerLayout drawerLayout;
    private TextView navProfile,navFoodManagement,navSales,navNotifications,navFaq,navAboutUs,navLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        inItUi();
        setUpNavigationDrawer();
    }




    public void inItUi()
    {
        botNavProfile= findViewById(R.id.bot_nav_profile);
        botNavFoodManagement = findViewById(R.id.bot_nav_food_management);
        botNavSale = findViewById(R.id.bot_nav_sales);
        botNavMenu = findViewById(R.id.bot_nav_menu);
        fabHome = findViewById(R.id.home);
        drawerLayout = findViewById(R.id.drawer_layout);

        botNavProfile.setOnClickListener(this);
        botNavFoodManagement.setOnClickListener(this);
        botNavSale.setOnClickListener(this);
        botNavMenu.setOnClickListener(this);
    }




    public void setBottomIcon(ImageView imageView) {
     //   navHome.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(HomePage.this, R.color.icon_unselect)));
        //  navHome.setBackgroundColor(ContextCompat.getColor(HomePage.this,R.color.icon_unselect));
        //  navHome.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavProfile.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavSale.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavFoodManagement.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavMenu.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);

        imageView.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    }
    public void setBottomIcon()
    {
        botNavProfile.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavSale.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavFoodManagement.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavMenu.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bot_nav_profile:setBottomIcon(botNavProfile);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                           startActivity(new Intent(HomePage.this, ProfilePage.class));
                           setBottomIcon();
                    }
                },300);
                break;
            case R.id.bot_nav_food_management: setBottomIcon(botNavFoodManagement);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomePage.this, FoodMangement.class));
                        setBottomIcon();
                    }
                },300);
                break;
            case R.id.bot_nav_sales:setBottomIcon(botNavSale);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomePage.this, Sales.class));
                        setBottomIcon();
                    }
                },300);
                break;
            case R.id.bot_nav_menu:setBottomIcon(botNavMenu);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.openDrawer(GravityCompat.START);
                        setBottomIcon();
                    }
                },200);
                break;
        }
    }


    public void setUpNavigationDrawer()
    {
        navProfile = findViewById(R.id.my_profile);
        navFoodManagement = findViewById(R.id.food_management);
        navSales = findViewById(R.id.sales);
        navNotifications = findViewById(R.id.notifications);
        navFaq = findViewById(R.id.faqs);
        navAboutUs = findViewById(R.id.about_us);
        navLogout = findViewById(R.id.logout);

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, ProfilePage.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        navFoodManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, FoodMangement.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        navSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, Sales.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        navNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        navFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        navAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        navLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}