package com.abishek.comidapartner.Home.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.abishek.comidapartner.R;

public class ProfilePage extends AppCompatActivity {

    private TextView btnStoreDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        btnStoreDetail = findViewById(R.id.store_detail);

        btnStoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this,StoreDetailPage.class));
            }
        });
    }
}