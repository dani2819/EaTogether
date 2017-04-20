package com.applicoders.msp_2017_project.eatogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //find text view numbers
        Button searchButton = (Button) findViewById(R.id.searchNearby);
        Button hostButton = (Button) findViewById(R.id.host);
        Button stats = (Button) findViewById(R.id.stats);
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

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(HomeActivity.this, StatsActivity.class);
                startActivity(k);
            }
        });
    }
}
