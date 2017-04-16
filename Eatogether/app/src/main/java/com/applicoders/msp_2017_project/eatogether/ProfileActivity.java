package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applicoders.msp_2017_project.eatogether.AsyncTasks.GetUserDataTask;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import java.io.FileInputStream;
import java.util.HashMap;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_UPDATE;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Bio_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Email_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_First_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Gender_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Last_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Phone_PREF;

public class ProfileActivity extends AppCompatActivity {

    private GetUserDataTask mAuthTask = null;

    Context context;
    FloatingActionButton editProfileBtn;
    TextView profileName, profileDescription, profileEmail, profilePhone;
    ImageView profileImage, genderImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        mProfileView= findViewById(R.id.profile_layout);
        mProgressView = findViewById(R.id.profile_progress);
        showProgress(true);

        profileName = (TextView) findViewById(R.id.profile_name);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileDescription = (TextView) findViewById(R.id.profile_description);
        genderImage = (ImageView) findViewById(R.id.profile_genderImage);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profilePhone =(TextView) findViewById(R.id.profile_phone);
        editProfileBtn = (FloatingActionButton) findViewById(R.id.profile_edit);
        editProfileBtn.setVisibility(View.GONE);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newactivity = new Intent(context, EditProfileActivity.class);
                startActivity(newactivity);
            }
        });

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

    private void getUserData(){
        if (mAuthTask.instance != null) {
            return;
        }

        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        SharedPrefHandler.StorePref(this, TOKEN_PREF, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoicmFmYUBnbWFpbC5jb20iLCJpYXQiOjE0OTIyMjA0MzgsImV4cCI6MTUwMDg2MDQzOH0.fFhy7ukKS2Oq03o-t7YF6jT2mkSQY51JecEfwAI_APo");
        keyValuePair.put("token", TOKEN);
        mAuthTask = new GetUserDataTask(this, keyValuePair, "POST", SERVER_RESOURCE_UPDATE);
        mAuthTask.execute((Void) null);
    }

    public void populateData() {
        mAuthTask = null;
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
        showProgress(false);
        editProfileBtn.setVisibility(View.VISIBLE);
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
