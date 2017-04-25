package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.applicoders.msp_2017_project.eatogether.AsyncTasks.GetOneUserTask;
import com.applicoders.msp_2017_project.eatogether.AsyncTasks.GetUserDataTask;
import com.applicoders.msp_2017_project.eatogether.Interfaces.TaskDone;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import java.util.HashMap;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_GET_ONE_USER;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_UPDATE;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Bio_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Email_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_First_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Gender_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Last_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Phone_PREF;


public class ProfileActivity extends AppCompatActivity implements TaskDone, View.OnClickListener {

    private GetUserDataTask mAuthTask = null;
    private GetOneUserTask getOneUserTask = null;

    private Boolean isSelf = false;
    private String userID = "";

    Context context;
    FloatingActionButton editProfileBtn;
    TextView profileName, profileDescription, profileEmail, profilePhone;
    ImageView profileImage, genderImage;
    Button LogoutButton, ShowStatsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        TOKEN = SharedPrefHandler.getStoredPref(this, TOKEN_PREF);

        mProfileView= findViewById(R.id.profile_layout);
        mProgressView = findViewById(R.id.profile_progress);
        showProgress(true);

        profileName = (TextView) findViewById(R.id.profile_name);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileDescription = (TextView) findViewById(R.id.profile_description);
        genderImage = (ImageView) findViewById(R.id.profile_genderImage);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profilePhone =(TextView) findViewById(R.id.profile_phone);
        LogoutButton = (Button) findViewById(R.id.profile_logout);
        ShowStatsButton = (Button) findViewById(R.id.profile_show_stats);
        editProfileBtn = (FloatingActionButton) findViewById(R.id.profile_edit);
        editProfileBtn.setVisibility(View.GONE);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newactivity = new Intent(context, EditProfileActivity.class);
                newactivity.putExtra("profileName", profileName.getText());
                newactivity.putExtra("profileBio", profileDescription.getText());
                newactivity.putExtra("profileGender", SharedPrefHandler.getStoredPref(getApplicationContext(), User_Gender_PREF));
                startActivity(newactivity);
            }
        });


        isSelf = getIntent().getBooleanExtra("isSelf", false);
        getIntent().removeExtra("isSelf");
        if(!isSelf) {
            userID = getIntent().getStringExtra("UserID");
            getIntent().removeExtra("UserID");
        }

        getUserData();
//        Bitmap bmp = null;
//        String filename = getIntent().getStringExtra("image");
//        String description = getIntent().getStringExtra("Description");
//        if(!TextUtils.isEmpty(description)){
//            profileDescription.setText(description);
//        }
//        if(!TextUtils.isEmpty(filename)) {
//            try {
//                FileInputStream is = this.openFileInput(filename);
//                bmp = BitmapFactory.decodeStream(is);
//                is.close();
//
//                profileImage.setImageBitmap(bmp);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
            getUserData();
        }
    }

    private void getUserData(){
        if (mAuthTask.instance != null) {
            return;
        } else if (getOneUserTask != null){
            return;
        }

        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        //SharedPrefHandler.StorePref(this, TOKEN_PREF, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYXptYWt0ckBnbWFpbC5jb20iLCJpYXQiOjE0OTI1NDA5NzQsImV4cCI6MTUwMTE4MDk3NH0.8DMdaLSJIdpZ2hmBZJkgRM2lSlBF5t2fs9bLtMwblas");
        Log.d("TOKEN PROFILE", TOKEN);
        keyValuePair.put("token", TOKEN);
        if(isSelf) {
            mAuthTask = new GetUserDataTask(this, keyValuePair, "POST", SERVER_RESOURCE_UPDATE);
            mAuthTask.execute((Void) null);
        } else {
            keyValuePair.put("id", userID);
            getOneUserTask = new GetOneUserTask(this, keyValuePair, "GET", SERVER_RESOURCE_GET_ONE_USER);
            getOneUserTask.execute((Void) null);
        }

        new DownloadImageTask(profileImage).execute("https://media.nngroup.com/media/people/photos/IMG_2366-copy-400x400.jpg.400x400_q95_autocrop_crop_upscale.jpg");
    }

    public void populateData() {
        mAuthTask = null;
        getOneUserTask = null;
        String fullName = SharedPrefHandler.getStoredPref(this, User_First_Name_PREF) + " " + SharedPrefHandler.getStoredPref(this, User_Last_Name_PREF);
        profileName.setText(fullName);
        profileEmail.setText(SharedPrefHandler.getStoredPref(this, User_Email_PREF));
        profileDescription.setText(SharedPrefHandler.getStoredPref(this, User_Bio_PREF));
        profilePhone.setText(SharedPrefHandler.getStoredPref(this, User_Phone_PREF));
        genderImage.setBackground(null);
        if (SharedPrefHandler.getStoredPref(this, User_Gender_PREF).equals("1")) {
            genderImage.setImageResource(R.drawable.male_icon);
        } else {
            genderImage.setImageResource(R.drawable.female_icon);
        }

        if(!isSelf){
            profileEmail.setVisibility(View.GONE);
            profilePhone.setVisibility(View.GONE);
            editProfileBtn.setVisibility(View.GONE);
            LogoutButton.setVisibility(View.GONE);
            ShowStatsButton.setVisibility(View.GONE);
        } else {
            profileEmail.setVisibility(View.VISIBLE);
            profilePhone.setVisibility(View.VISIBLE);
            editProfileBtn.setVisibility(View.VISIBLE);
            LogoutButton.setVisibility(View.VISIBLE);
            ShowStatsButton.setVisibility(View.VISIBLE);
        }

        showProgress(false);
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
    public void TaskCompleted() {
        populateData();
    }

    @Override
    public void FoodEventTaskDone(String Data) {

    }

    private void Logout(){
        TOKEN = "";
        SharedPrefHandler.StorePref(this, TOKEN_PREF, "");
        Intent newActivity = new Intent(this, LoginActivity.class);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newActivity);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profile_logout:
                Logout();
                break;

            case R.id.profile_show_stats:
                // TODO: SHOW STATS
                Intent newActivity = new Intent(this, StatsMainActivity.class);
                startActivity(newActivity);
                break;
        }
    }
}
