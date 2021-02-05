package com.abishek.comidapartner.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abishek.comidapartner.Home.adapter.TabAdapter;
import com.abishek.comidapartner.Home.foodMangement.FoodManagement;
import com.abishek.comidapartner.Home.profile.ProfilePage;
import com.abishek.comidapartner.Home.sales.Sales;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.aboutUs.AboutUs;
import com.abishek.comidapartner.aboutUs.Faq;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.loginAndSignUp.AddNewAddress;
import com.abishek.comidapartner.loginAndSignUp.AddShopDetails;
import com.abishek.comidapartner.loginAndSignUp.Login;
import com.abishek.comidapartner.notification.NotificationHomePage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "HomePage";

    private ImageView botNavProfile, botNavFoodManagement, botNavSale, botNavMenu,navProfileImage;
    private FloatingActionButton fabHome;
    private DrawerLayout drawerLayout;
    private TextView navProfile, navFoodManagement, navSales, navNotifications, navFaq, navAboutUs, navLogout;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoginSessionManager loginSessionManager;
    private HashMap<String,String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginSessionManager = new LoginSessionManager(HomePage.this);
        user = loginSessionManager.getUserDetailsFromSP();
        if(!loginSessionManager.isLoggedIn())
        {
            startActivity(new Intent(HomePage.this, Login.class));
            finish();
            return;
        }
        if(user.get("shop_name").equals("null"))
        {
            startActivity(new Intent(HomePage.this, AddShopDetails.class));
            finish();
            return;
        }
        if(user.get("address").equals("null"))
        {
            startActivity(new Intent(HomePage.this, AddNewAddress.class));
            finish();
            return;
        }
        Log.e(TAG,user.get("shop_name")+"  "+user.get("address"));


        launchHome();

    }

    public void launchHome()
    {

        setContentView(R.layout.activity_home_page);

        inItUi();
        setUpNavigationDrawer();
    }

    public void inItUi() {
        botNavProfile = findViewById(R.id.bot_nav_profile);
        botNavFoodManagement = findViewById(R.id.bot_nav_food_management);
        botNavSale = findViewById(R.id.bot_nav_sales);
        botNavMenu = findViewById(R.id.bot_nav_menu);
        fabHome = findViewById(R.id.home);
        drawerLayout = findViewById(R.id.drawer_layout);


        botNavProfile.setOnClickListener(this);
        botNavFoodManagement.setOnClickListener(this);
        botNavSale.setOnClickListener(this);
        botNavMenu.setOnClickListener(this);
        setTabLayout();
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

    public void setBottomIcon() {
        botNavProfile.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavSale.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavFoodManagement.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
        botNavMenu.setColorFilter(ContextCompat.getColor(this, R.color.icon_unselect), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bot_nav_profile:
                setBottomIcon(botNavProfile);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomePage.this, ProfilePage.class));
                        setBottomIcon();
                    }
                }, 300);
                break;
            case R.id.bot_nav_food_management:
                setBottomIcon(botNavFoodManagement);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomePage.this, FoodManagement.class));
                        setBottomIcon();
                    }
                }, 300);
                break;
            case R.id.bot_nav_sales:
                setBottomIcon(botNavSale);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(HomePage.this, Sales.class));
                        setBottomIcon();
                    }
                }, 300);
                break;
            case R.id.bot_nav_menu:
                setBottomIcon(botNavMenu);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.openDrawer(GravityCompat.START);
                        setBottomIcon();
                    }
                }, 200);
                break;
        }
    }


    public void setUpNavigationDrawer() {
        navProfile = findViewById(R.id.my_profile);
        navFoodManagement = findViewById(R.id.food_management);
        navSales = findViewById(R.id.sales);
        navNotifications = findViewById(R.id.notifications);
        navFaq = findViewById(R.id.faqs);
        navAboutUs = findViewById(R.id.about_us);
        navLogout = findViewById(R.id.logout);
        navProfileImage = findViewById(R.id.nav_profile_image);

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
                startActivity(new Intent(HomePage.this, FoodManagement.class));
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
                startActivity(new Intent(HomePage.this, NotificationHomePage.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        navFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, Faq.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        navAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, AboutUs.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        navLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        String profileImage = new LoginSessionManager(HomePage.this).getUserDetailsFromSP().get("profile_image");
        if(!profileImage.equals("null"))
            Picasso.get().load(BASE_IMAGE+profileImage).into(navProfileImage);
    }

    public void setTabLayout() {

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("New Order"));
        tabLayout.addTab(tabLayout.newTab().setText("In Progress"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));


        final TabAdapter adapter = new TabAdapter(HomePage.this, getSupportFragmentManager(),
                tabLayout.getTabCount(), viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

     //  setTabLayout();
        Log.e(TAG,".........onResume");
    }
}