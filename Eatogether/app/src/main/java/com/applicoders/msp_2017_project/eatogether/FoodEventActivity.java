package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applicoders.msp_2017_project.eatogether.AsyncTasks.FoodEventDetailsTask;
import com.applicoders.msp_2017_project.eatogether.AsyncTasks.GetOneUserTask;
import com.applicoders.msp_2017_project.eatogether.AsyncTasks.JoinUserTask;
import com.applicoders.msp_2017_project.eatogether.Interfaces.TaskDone;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.applicoders.msp_2017_project.eatogether.Constants.CONTEXT;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_FOOD_EVENT;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_GET_ONE_USER;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_JOIN;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_UNJOIN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Bio_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_First_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Last_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Phone_PREF;

public class FoodEventActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, TaskDone {


    private GoogleMap googleMap;
    private FoodEventDetailsTask mAuthTask = null;
    private JoinUserTask joinUserTask = null;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    public String FoodID;
    String hostID;

    SupportMapFragment mapFragment;

    TextView Title, Host, Description, GuestsText, LocationText, DateText, TimeText;
    ImageView HostImage, FoodImage;
    Button RequestJoinBtn;
    View JoinReqLayout, MapHolder, GuestListHolder, GuestLayouttoHide;
    Boolean isHost, canJoin, hasJoined;
    LatLng markerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_event);
        CONTEXT = getApplicationContext();

        mProfileView = findViewById(R.id.food_layout_main);
        mProgressView = findViewById(R.id.food_progress);

        JoinReqLayout = (View) findViewById(R.id.food_btn_layout);
        MapHolder = (View) findViewById(R.id.food_map_holder);
        GuestListHolder = (View) findViewById(R.id.food_guestList_holder);
        GuestLayouttoHide = (View) findViewById(R.id.food_guestLayout_tohide);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //Collapsing Toolbar Code
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.CollapsingToolbarLayout1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TOKEN = SharedPrefHandler.getStoredPref(this, TOKEN_PREF);
        //SharedPrefHandler.StorePref(this, TOKEN_PREF, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoicmFmYUBnbWFpbC5jb20iLCJpYXQiOjE0OTIzNjIyMjEsImV4cCI6MTUwMTAwMjIyMX0.Hz4toqlz6kWQrQ0KPtR7LuD_Kbtm_esANksaT97HdpM");//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYXptYWt0ckBnbWFpbC5jb20iLCJpYXQiOjE0OTI1NDA5NzQsImV4cCI6MTUwMTE4MDk3NH0.8DMdaLSJIdpZ2hmBZJkgRM2lSlBF5t2fs9bLtMwblas");
        //TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoicmFmYUBnbWFpbC5jb20iLCJpYXQiOjE0OTIzNjIyMjEsImV4cCI6MTUwMTAwMjIyMX0.Hz4toqlz6kWQrQ0KPtR7LuD_Kbtm_esANksaT97HdpM";
        //FoodID = getIntent().getStringExtra("foodID");
        FoodID = "16";

        Title = (TextView) findViewById(R.id.food_title);
        Host = (TextView) findViewById(R.id.food_host);
        Description = (TextView) findViewById(R.id.food_description);
        GuestsText = (TextView) findViewById(R.id.food_guests);
        LocationText = (TextView) findViewById(R.id.food_location);
        DateText = (TextView) findViewById(R.id.food_date);
        TimeText = (TextView) findViewById(R.id.food_time);
        HostImage = (ImageView) findViewById(R.id.food_host_image);
        FoodImage = (ImageView) findViewById(R.id.food_top_image);
        RequestJoinBtn = (Button) findViewById(R.id.food_request_join);

        LoadData();

    }

    private boolean wasPaused = false;
    @Override
    public void onPause(){
        super.onPause();
        wasPaused = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(wasPaused) {
            wasPaused = false;
            LoadData();
        }
    }

    private void LoadData(){
        showProgress(true);
        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        keyValuePair.put("token", TOKEN);
        keyValuePair.put("id", FoodID);
        mAuthTask = new FoodEventDetailsTask(this, keyValuePair, "GET", SERVER_RESOURCE_FOOD_EVENT);
        mAuthTask.execute((Void) null);
    }

    private void GotoHostPage(){
        try {
            Log.i("isSelf: ", "" + isHost);
            Log.i("USERId: ", "" + hostID);
            Intent newActivity = new Intent(this, ProfileActivity.class);
            newActivity.putExtra("isSelf", isHost);
            newActivity.putExtra("UserID", hostID);
            startActivity(newActivity);
        } catch (Exception e){
            Log.d("Exception: ", e.getMessage());
        }
    }

    public void dataRecieved(HashMap data) throws IOException {
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
        mAuthTask= null;
        showProgress(false);
        hostID = data.get("hostID").toString();
        isHost = (Boolean) data.get("isHost");
        canJoin = (Boolean) data.get("canJoin");
        hasJoined = (Boolean) data.get("hasjoined");
        if (hasJoined) {
            RequestJoinBtn.setText("Unjoin");
        } else {
            RequestJoinBtn.setText("Join");
        }
        if (isHost) {
            Host.setText("You");
            HostImage.setVisibility(View.GONE);
            JoinReqLayout.setVisibility(View.GONE);
            MapHolder.setVisibility(View.GONE);
            GuestLayouttoHide.setVisibility(View.VISIBLE);

        } else {
            JoinReqLayout.setVisibility(View.VISIBLE);
            MapHolder.setVisibility(View.VISIBLE);
            GuestLayouttoHide.setVisibility(View.GONE);
            mapFragment.getMapAsync(this);
        }


        Title.setText(data.get("title").toString());
        Host.setText(data.get("firstname").toString() + " " + data.get("lastname").toString());
        Description.setText(data.get("desciption").toString());
        GuestsText.setText(data.get("joinedGuests").toString() + "/" + data.get("numOfGuests"));

        Double lat = Double.parseDouble(data.get("location").toString().split(" ")[0]);
        Double lng = Double.parseDouble(data.get("location").toString().split(" ")[1]);
        markerLocation = new LatLng(lat, lng);
        LocationText.setText(getAddress(lat, lng));


        String date = data.get("dateTime").toString().split("T")[0];
        String year = date.split("-")[0];
        int month = Integer.parseInt(date.split("-")[1]);
        String day = date.split("-")[2];

        DateText.setText(day + " " + getMonth(month) + " " + year);


        String TempS = data.get("dateTime").toString().split("T")[1];
        String Time = new StringBuilder().append(TempS.split(":")[0]).append(":").append(TempS.split(":")[1]).toString();
        TimeText.setText(Time);
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public String getAddress(double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);

        Log.i("Location Name:", cityName + " " + stateName + " " + countryName);

        return cityName + " " + stateName + " " + countryName;
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

    @Override
    public void onMapReady(GoogleMap mMap) {
        Log.d("Map READy", "READY");
        googleMap = mMap;
        MoveMapCamera();
        // For showing a move to my location button
        //googleMap.setMyLocationEnabled(true);
    }

    private void MoveMapCamera() {
        // For dropping a marker at a point on the Map
        googleMap.addMarker(new MarkerOptions().position(markerLocation).title("Marker Title").snippet("Marker Description"));
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(markerLocation).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void JoinTaskDone(String dataString) {
//        if(TextUtils.equals(dataString, "removed")){
//            hasJoined = "false";
//            RequestJoinBtn.setText("Join");
//        } else if(TextUtils.equals(dataString, "Joined")){
//            hasJoined = "true";
//            RequestJoinBtn.setText("Unjoin");
//        }
        joinUserTask.instance = null;
        RequestJoinBtn.bringToFront();
        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        keyValuePair.put("token", TOKEN);
        keyValuePair.put("id", FoodID);
        mAuthTask = new FoodEventDetailsTask(this, keyValuePair, "GET", SERVER_RESOURCE_FOOD_EVENT);
        mAuthTask.execute((Void) null);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.food_request_join:
                Log.d("Btn Clicked", "True");
                HashMap<String, String> keyValuePair = new HashMap<String, String>();
                keyValuePair.put("token", TOKEN);
                keyValuePair.put("id", FoodID);
                if (hasJoined) {
                    joinUserTask = new JoinUserTask(this, keyValuePair, "POST", SERVER_RESOURCE_UNJOIN);
                } else {
                    joinUserTask = new JoinUserTask(this, keyValuePair, "POST", SERVER_RESOURCE_JOIN);
                }
                joinUserTask.execute((Void) null);
                showProgress(true);
                break;

            case R.id.food_host:
                GotoHostPage();
                break;

            case R.id.food_host_image:
                GotoHostPage();
                break;


        }
    }

    private HashMap<String, HashMap<String, String>> GuestList;
    public String GuestID, FullDate;
    public void populateGuestList(HashMap guestList) {
        GuestList = guestList;
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayoutMain = (LinearLayout) findViewById(R.id.food_guestList_holder);
        linearLayoutMain.removeAllViews();

        Log.d("Populating Guests", "true");

        for(int i = 0; i < guestList.size(); i++){
            HashMap<String, String> guest = GuestList.get("Guest"+i);
            String firstName = guest.get("firstname").toString();
            String joinDate = guest.get("joined").toString();
            String day = joinDate.split("T")[0].split("-")[2];
            String month = joinDate.split("T")[0].split("-")[1];
            String year = joinDate.split("T")[0].split("-")[0];
            String dateTodis = new StringBuilder("Joined: ").append(day).append("/").append(month).append("/").append(year).toString();
            View recordView = layoutInflater.inflate(R.layout.guests_layout, linearLayoutMain, false);
            ((TextView)recordView.findViewById(R.id.guest_first_name)).setText(firstName);
            ((TextView)recordView.findViewById(R.id.guest_join_date)).setText(dateTodis);
            recordView.findViewById(R.id.guest_main_holder).setTag(i);
            linearLayoutMain.addView(recordView);
        }
    }

    public void onGuestItemClick(View view){
        int tag = (int) view.getTag();
//        Toast.makeText(this, "Guest Item: " + tag, Toast.LENGTH_LONG).show();
        HashMap<String, String> guest = GuestList.get("Guest"+tag);
        GuestID = guest.get("userID").toString();
        String joinDate = guest.get("joined").toString();
        String day = joinDate.split("T")[0].split("-")[2];
        String month = joinDate.split("T")[0].split("-")[1];
        String year = joinDate.split("T")[0].split("-")[0];
        String dateTodis = new StringBuilder("Joined: ").append(day).append("/").append(month).append("/").append(year).toString();
        Toast.makeText(this, "Guest Joined: " + dateTodis, Toast.LENGTH_LONG).show();
        FullDate = dateTodis;
        //TODO: Open a fragment to show the guest details
        FragmentManager fm = getSupportFragmentManager();
        MyDialogFragment dialogFragment = new MyDialogFragment ();
        dialogFragment.show(fm, "Guest Details");
    }

    @Override
    public void TaskCompleted() {

    }

    @Override
    public void FoodEventTaskDone(String Data) {
        JoinTaskDone(Data);
    }

    public static class MyDialogFragment extends DialogFragment implements TaskDone{

        private GetOneUserTask getOneUserTask = null;
        private MyDialogFragment DF = null;
        View rootView;
        FoodEventActivity parentActivty;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.guest_detail_layout, container, false);
            getDialog().setTitle("Guest Details");
            DF = this;
            ImageView dismiss = (ImageView) rootView.findViewById(R.id.dismiss);
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            Button RemoveGuest = (Button) rootView.findViewById(R.id.guest_remove);
            RemoveGuest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TextView) rootView.findViewById(R.id.guest_full_name)).setText("Azeem Akhter");
                }
            });
            GetGuestData();
            return rootView;
        }



        private void GetGuestData(){
            if(getOneUserTask != null){
                return;
            }
            parentActivty = (FoodEventActivity) getActivity();
            HashMap<String, String> keyValuePair = new HashMap<String, String>();
            keyValuePair.put("token", TOKEN);
            keyValuePair.put("id", parentActivty.GuestID);
            getOneUserTask = new GetOneUserTask(DF, keyValuePair, "GET", SERVER_RESOURCE_GET_ONE_USER);
            getOneUserTask.execute((Void) null);
        }

        @Override
        public void TaskCompleted() {
            populateDialogData();
        }

        @Override
        public void FoodEventTaskDone(String Data) {

        }

        private void populateDialogData() {
            String fullName = new StringBuilder().append(SharedPrefHandler.getStoredPref(parentActivty, User_First_Name_PREF))
                    .append(" ").append(SharedPrefHandler.getStoredPref(parentActivty, User_Last_Name_PREF)).toString();
            ((TextView) rootView.findViewById(R.id.guest_full_name)).setText(fullName);
            ((TextView) rootView.findViewById(R.id.guest_full_JoinDate)).setText(parentActivty.FullDate);
            ((TextView) rootView.findViewById(R.id.guest_bio)).setText(SharedPrefHandler.getStoredPref(parentActivty, User_Bio_PREF));
            ((TextView) rootView.findViewById(R.id.guest_phone)).setText(SharedPrefHandler.getStoredPref(parentActivty, User_Phone_PREF));

        }
    }
}
