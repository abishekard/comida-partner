package com.abishek.comidapartner.loginAndSignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.abishek.comidapartner.R;

public class AddShopDetails extends AppCompatActivity {

    private Button btnProceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_details);
        btnProceed = findViewById(R.id.proceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddShopDetails.this,AddNewAddress.class));
            }
        });
    }
}