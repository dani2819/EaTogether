package com.applicoders.msp_2017_project.eatogether;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TOKEN = SharedPrefHandler.getStoredPref(this, TOKEN_PREF);
        //find text view numbers
        FrameLayout searchButton = (FrameLayout) findViewById(R.id.home_search_nearby);
        FrameLayout hostButton = (FrameLayout) findViewById(R.id.home_host);
        FrameLayout profileButton = (FrameLayout) findViewById(R.id.home_profile);
        //set onclick listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });

        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(HomeActivity.this, HostActivity.class);
                startActivity(j);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(HomeActivity.this, ProfileActivity.class);
                k.putExtra("isSelf", true);
                startActivity(k);
            }
        });
    }
}
