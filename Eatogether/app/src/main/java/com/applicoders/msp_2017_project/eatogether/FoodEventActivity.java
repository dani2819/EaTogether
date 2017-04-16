package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applicoders.msp_2017_project.eatogether.AsyncTasks.FoodEventDetailsTask;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.vision.text.Text;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_FOOD_EVENT;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_UPDATE;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;

public class FoodEventActivity extends AppCompatActivity {

    private FoodEventDetailsTask mAuthTask = null;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;


    TextView Title, Host, Description, GuestsText, LocationText, DateText, TimeText;
    ImageView HostImage, FoodImage;
    Button RequestJoinBtn;
    boolean isHost, canJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_event);

        mProfileView= findViewById(R.id.food_layout_main);
        mProgressView = findViewById(R.id.food_progress);

        //Collapsing Toolbar Code
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.CollapsingToolbarLayout1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Title = (TextView) findViewById(R.id.food_title);
        Host= (TextView) findViewById(R.id.food_host);
        Description = (TextView) findViewById(R.id.food_description);
        GuestsText = (TextView) findViewById(R.id.food_guests);
        LocationText = (TextView) findViewById(R.id.food_location);
        DateText = (TextView) findViewById(R.id.food_date);
        TimeText = (TextView) findViewById(R.id.food_time);
        HostImage = (ImageView) findViewById(R.id.food_host_image);
        FoodImage = (ImageView) findViewById(R.id.food_top_image);
        RequestJoinBtn = (Button) findViewById(R.id.food_request_join);
        RequestJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Request Join in the Event
            }
        });

        String FoodID = "8";

        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        keyValuePair.put("token", TOKEN);
        keyValuePair.put("id", FoodID);
        mAuthTask = new FoodEventDetailsTask(this, keyValuePair, "GET", SERVER_RESOURCE_FOOD_EVENT);
        mAuthTask.execute((Void) null);
        //showProgress(true);

    }

    public void dataRecieved(HashMap data) throws IOException{
//        dataToSend.put("firstname", firstName);
//        dataToSend.put("lastname", lastName);
//        dataToSend.put("email", email);
//        dataToSend.put("desciption", description);
//        dataToSend.put("title", title);
//        dataToSend.put("location", location);
//        dataToSend.put("dateTime", dateTime);
//        dataToSend.put("numOfGuests", numOfGuests);
//        dataToSend.put("hostID", hostID);
//        dataToSend.put("joinedGuests", joinedGuests);
//        dataToSend.put("isHost", isHost);
//        dataToSend.put("canJoin", canJoin);
        mAuthTask.instance = null;
        showProgress(false);
        Title.setText(data.get("title").toString());
        Host.setText(data.get("firstname").toString() + " " + data.get("lastname").toString());
        Description.setText(data.get("desciption").toString());
        GuestsText.setText(data.get("joinedGuests").toString() + "/" + data.get("numOfGuests"));

        Double lat = Double.parseDouble(data.get("location").toString().split(" ")[0]);
        Double lng = Double.parseDouble(data.get("location").toString().split(" ")[1]);
        LocationText.setText(getAddress(lat, lng));

        String date = data.get("dateTime").toString().split("T")[0];
        String year = date.split("-")[0];
        int month = Integer.parseInt(date.split("-")[1]);
        String day = date.split("-")[2];

        String Time = data.get("dateTime").toString().split("T")[1].split(".")[0];

        DateText.setText(day + " " + getMonth(month) + " " + year);
        TimeText.setText(Time);

        isHost = Boolean.parseBoolean(data.get("isHost").toString());
        canJoin = Boolean.parseBoolean(data.get("canJoin").toString());

        if(isHost){
            Host.setText("You");
            HostImage.setVisibility(View.GONE);
        }

        if(!canJoin){
            RequestJoinBtn.setVisibility(View.GONE);
        }

    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
    public String getAddress(double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);

        Log.i("Location Name:", cityName+" "+stateName+" "+countryName);

        return cityName+" "+stateName+" "+countryName;
    }

    View mProfileView;
    View mProgressView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
