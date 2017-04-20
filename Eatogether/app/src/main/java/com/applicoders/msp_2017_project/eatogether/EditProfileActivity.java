package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applicoders.msp_2017_project.eatogether.AsyncTasks.UpdateUserDataTask;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.ImagePicker;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.ImageToFile;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_UPDATE;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;

public class EditProfileActivity extends AppCompatActivity {

    private UpdateUserDataTask updataImageTask = null;
    private UpdateUserDataTask updateTextTasl = null;

    private static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    private TextView userName;
    EditText descriptionText, newPass, retypePass;
    FloatingActionButton saveButton;
    ImageView profileImage, editImage, genderImage;
    Bitmap currentBitmap;
    Button changePassBtn;
    View changePassboxesLayout;
    Context context;
    boolean wasBioChanged = false;
    boolean wasPasswordChanged = false;
    boolean allUpdated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TOKEN = SharedPrefHandler.getStoredPref(this, TOKEN_PREF);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;

        mProfileView= findViewById(R.id.edit_profile_layoutToHide);
        mProgressView = findViewById(R.id.edit_profile_progress);

        String profName = getIntent().getStringExtra("profileName");
        String profBio = getIntent().getStringExtra("profileBio");
        String profGender = getIntent().getStringExtra("profileGender");


        userName = (TextView) findViewById(R.id.edit_profile_name);
        userName.setText(profName);
        profileImage = (ImageView) findViewById(R.id.edit_profile_image);
        editImage = (ImageView) findViewById(R.id.edit_profile_thumb);
        descriptionText = (EditText) findViewById(R.id.edit_profile_description);
        descriptionText.setText(profBio);
        genderImage = (ImageView) findViewById(R.id.edit_profile_genderImage);
        genderImage.setBackground(null);
        if (TextUtils.equals(profGender, "1")) {
            genderImage.setImageResource(R.drawable.male_icon);
        } else {
            genderImage.setImageResource(R.drawable.female_icon);
        }

        newPass = (EditText) findViewById(R.id.edit_password);
        retypePass = (EditText) findViewById(R.id.edit_repassword);

        changePassboxesLayout = (LinearLayout) findViewById(R.id.edit_changepass_boxes_layout);
        changePassBtn = (Button) findViewById(R.id.edit_change_passbtn);
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassBtn.setVisibility(View.GONE);
                changePassboxesLayout.setVisibility(View.VISIBLE);
                newPass.requestFocus();
            }
        });


        saveButton = (FloatingActionButton) findViewById(R.id.edit_profile_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SaveChanges();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });

        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("descroTextchanged", "True");
                wasBioChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("descroTextchanged", "True");
                wasPasswordChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void SaveChanges() throws IOException {

        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        try {

            if(wasPasswordChanged){
                if(!isPasswordValid(newPass.getText().toString())){
                    newPass.setError(getString(R.string.error_invalid_password));
                    newPass.requestFocus();
                    return;
                }
                if(!TextUtils.equals(retypePass.getText().toString(), newPass.getText().toString())){
                    retypePass.setError(getString(R.string.error_password_unmatch));
                    retypePass.requestFocus();
                    return;
                }
            }

            if (updataImageTask != null || updateTextTasl != null)
                return;
            File imageFile = null;
            if (currentBitmap != null) {
                Log.d("Bitmap Status", "Not Null");
                currentBitmap = scaleDown(currentBitmap, 120, true);
                imageFile = ImageToFile.convertImage(this, currentBitmap);
                if (imageFile != null) {
                    Log.d("File Status", "Not Null");
                    updataImageTask = new UpdateUserDataTask(this, imageFile, null,"POST", SERVER_RESOURCE_UPDATE, TOKEN);
                    updataImageTask.execute((Void) null);
                }
            }

            if (wasBioChanged){
                Log.d("Update Bio", descriptionText.getText().toString());
                keyValuePair.put("token", TOKEN);
                keyValuePair.put("bio", descriptionText.getText().toString());
                updateTextTasl = new UpdateUserDataTask(this, null, keyValuePair, "POST", SERVER_RESOURCE_UPDATE, TOKEN);
                updateTextTasl.execute((Void) null);
            } else if (wasPasswordChanged){
                keyValuePair.put("token", TOKEN);
                keyValuePair.put("password", newPass.getText().toString());
                updateTextTasl = new UpdateUserDataTask(this, null, keyValuePair, "POST", SERVER_RESOURCE_UPDATE, TOKEN);
                updateTextTasl.execute((Void) null);
            } else if (wasBioChanged && wasPasswordChanged){
                keyValuePair.put("token", TOKEN);
                keyValuePair.put("bio", descriptionText.getText().toString());
                keyValuePair.put("password", newPass.getText().toString());
                updateTextTasl = new UpdateUserDataTask(this, null, keyValuePair, "POST", SERVER_RESOURCE_UPDATE, TOKEN);
                updateTextTasl.execute((Void) null);
            }
        } catch (Exception e){
            Log.d("Save Change: ", e.getMessage());
        } finally {
            allUpdated = true;
        }


    }

    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                currentBitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                profileImage.setImageBitmap(currentBitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void changeImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    private String createBitmapByte(Bitmap bmp){
        try {
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            bmp.recycle();

            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void dataUpdatedSuccessfully() {
        // TODO: data saved succesfull go back to activity.
        updataImageTask.instance = null;
        currentBitmap = null;
        if (allUpdated) {
            allUpdated = false;
            //updataImageTask.instance = null;
            updateTextTasl.instance = null;
            Toast.makeText(this, "Data was saved succesfully", Toast.LENGTH_LONG).show();
            Intent newActivity = new Intent(context, ProfileActivity.class);
            newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newActivity);
        }
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
