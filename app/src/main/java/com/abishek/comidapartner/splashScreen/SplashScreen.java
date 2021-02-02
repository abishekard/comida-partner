package com.abishek.comidapartner.splashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.abishek.comidapartner.Home.HomePage;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.loginAndSignUp.Login;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, HomePage.class));
                finish();
            }
        },2000);
    }
}