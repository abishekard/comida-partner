package com.abishek.comidapartner.splashScreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
;

import com.abishek.comidapartner.Home.HomePage;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.loginAndSignUp.Login;

import java.util.ArrayList;
import java.util.List;


public class onBoarding extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private List<Fragment> mFragmentList;
    private TextView tv_next, tv_skip;
    private ViewPager viewPager;
    private TextView dot1,dot2,dot3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*boolean shown = new LoginSessionManager(onBoarding.this).onBoardingShown();

        if(!shown)
        {
            startActivity(new Intent(onBoarding.this, HomePage.class));
            finish();
            return;
        }*/


        // Next two lines help in getting rid off status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);





        setContentView(R.layout.activity_on_boarding);
        new LoginSessionManager(onBoarding.this).setOnBoardingShown();

        mFragmentList = new ArrayList<>();

        mFragmentList.add(new SCPage1());
        mFragmentList.add(new SCPage2());
        mFragmentList.add(new SCPage3());

        viewPager = findViewById(R.id.viewpager_onBoarding);
        viewPager.addOnPageChangeListener(this);

        SCFragmentPagerAdapter adapter = new SCFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(adapter);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        dot1.setBackground(getResources().getDrawable(R.drawable.indicator_selected));

        tv_next = findViewById(R.id.next);
        tv_skip = findViewById(R.id.skip);

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = viewPager.getCurrentItem();
                if(id == 2){
                    startActivity(new Intent(onBoarding.this, Login.class));
                    finish();
                }
                else{
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }

            }
        });


        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(onBoarding.this,Login.class));
                finish();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPageSelected(final int position) {

        switch (position){

            case 0 :
                dot1.setBackground(getResources().getDrawable(R.drawable.indicator_selected));
                dot2.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot3.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));

                tv_next.setText("Next");
                tv_skip.setVisibility(View.VISIBLE);
                break;

            case 1:
                dot1.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot2.setBackground(getResources().getDrawable(R.drawable.indicator_selected));
                dot3.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                tv_next.setText("Next");
                tv_skip.setVisibility(View.VISIBLE);
                break;

            case 2:
                dot1.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot2.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot3.setBackground(getResources().getDrawable(R.drawable.indicator_selected));
                tv_next.setText("Next");
                tv_next.setText("Start");
                tv_skip.setVisibility(View.INVISIBLE);
                break;

        }
    }
    @Override
    public void onPageScrollStateChanged(int state) { }




}