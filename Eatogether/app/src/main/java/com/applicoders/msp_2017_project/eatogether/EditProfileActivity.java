package com.applicoders.msp_2017_project.eatogether;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.nearby.messages.internal.Update;
import com.google.android.gms.vision.text.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_UPDATE;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Bio_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Gender_PREF;

public class EditProfileActivity extends AppCompatActivity {

    private UpdateUserDataTask mAuthTask = null;

    private static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    EditText descriptionText, newPass, retypePass;
    FloatingActionButton saveButton;
    ImageView profileImage, editImage, genderImage;
    Bitmap currentBitmap;
    Button changePassBtn;
    View changePassboxesLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
        profileImage = (ImageView) findViewById(R.id.edit_profile_image);
        editImage = (ImageView) findViewById(R.id.edit_profile_thumb);
        descriptionText = (EditText) findViewById(R.id.edit_profile_description);
        descriptionText.setText(SharedPrefHandler.getStoredPref(this, User_Bio_PREF));
        genderImage = (ImageView) findViewById(R.id.edit_profile_genderImage);
        genderImage.setBackground(null);
        if (SharedPrefHandler.getStoredPref(this, User_Gender_PREF).equals("1")) {
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
    }


    private void SaveChanges() throws IOException {

        currentBitmap = scaleDown(currentBitmap, 120, true);

        if(mAuthTask != null)
            return;
        File imageFile = null;
        if(currentBitmap != null){
            Log.d("Bitmap Status", "Not Null");
            imageFile = ImageToFile.convertImage(this, currentBitmap);
        }
        if(imageFile != null) {
            Log.d("File Status", "Not Null");
            TOKEN = SharedPrefHandler.getStoredPref(this, TOKEN_PREF);
            mAuthTask = new UpdateUserDataTask(this, imageFile, "POST", SERVER_RESOURCE_UPDATE, TOKEN);
            mAuthTask.execute((Void) null);
        }


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
        mAuthTask.instance = null;

        Toast.makeText(this, "Data was saved succesfully", Toast.LENGTH_LONG).show();

        Intent newActivity = new Intent(context, ProfileActivity.class);
        newActivity.putExtra("Description", descriptionText.getText().toString());
        String filename = createBitmapByte(currentBitmap);
        newActivity.putExtra("image", filename);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newActivity);
    }
}
