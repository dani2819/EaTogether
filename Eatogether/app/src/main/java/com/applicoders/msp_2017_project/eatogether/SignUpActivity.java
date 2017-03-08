package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_HOST;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_PORT;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_LOGIN;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_LOGOUT;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_SIGNUP;


/**
 * Created by rafay on 3/7/2017.
 */

public class SignUpActivity extends AppCompatActivity {

    private View mProgressView;
    private View mSignUpFormView;
    private EditText mfirstName, mlastName, mEmailAddress, mPassword, mReEnterPass, mTelephone;
    private String mGender = "";
    private Button mSignUpBtn;
    private UserSignUpTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Button CLicked", "Trueeee");
                attemptSignUp();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_male:
                if (checked) {
                    Toast.makeText(this, "Male Button Clicked", Toast.LENGTH_LONG).show();
                    mGender = "Male";
                    break;
                }
            case R.id.rb_female:
                if (checked) {
                    Toast.makeText(this, "Female Button Clicked", Toast.LENGTH_LONG).show();
                    mGender = "Female";
                    break;
                }
        }
    }

    private void attemptSignUp() {
        if (mAuthTask != null) {
            return;
        }

        JSONObject json;
        // Reset errors.
        mfirstName.setError(null);
        mlastName.setError(null);
        mEmailAddress.setError(null);
        mPassword.setError(null);
        mReEnterPass.setError(null);
        mTelephone.setError(null);

        // Store values at the time of the login attempt.
        String FirstName = mfirstName.getText().toString();
        String LastName = mlastName.getText().toString();
        String email = mEmailAddress.getText().toString();
        String password = mPassword.getText().toString();
        String Telephone = mTelephone.getText().toString();

        Toast.makeText(this, FirstName + ", " + LastName + ", " + email + ", " + password + ", " + Telephone, Toast.LENGTH_LONG).show();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(FirstName)){
            mfirstName.setError(getString(R.string.error_field_required));
            focusView = mfirstName;
            cancel = true;
        }

        if(TextUtils.isEmpty(LastName)){
            mlastName.setError(getString(R.string.error_field_required));
            focusView = mlastName;
            cancel = true;
        }

        if(TextUtils.isEmpty(Telephone)){
            mTelephone.setError(getString(R.string.error_field_required));
            focusView = mTelephone;
            cancel = true;
        }

        if(TextUtils.isEmpty(mGender)){
            Toast.makeText(this, "Select Gender", Toast.LENGTH_LONG).show();
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        if(TextUtils.isEmpty(mReEnterPass.getText().toString())){
            mReEnterPass.setError(getString(R.string.error_field_required));
            focusView = mReEnterPass;
            cancel = true;
        }

        if(!TextUtils.equals(mReEnterPass.getText().toString(), password.toString())){
            mReEnterPass.setError(getString(R.string.error_password_unmatch));
            focusView = mReEnterPass;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailAddress.setError(getString(R.string.error_field_required));
            focusView = mEmailAddress;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailAddress.setError(getString(R.string.error_invalid_email));
            focusView = mEmailAddress;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            try {
                json = new JSONObject();
                json.put("Firstname", FirstName);
                json.put("Lastname", LastName);
                json.put("Email", email);
                json.put("Telephone", Telephone);
                json.put("Gender", mGender);
                Log.v("Json ", json.toString());
            }
            catch (JSONException e){

            }

            mAuthTask = new SignUpActivity.UserSignUpTask(FirstName, LastName, Telephone, email, password, mGender);
            mAuthTask.execute((Void) null);
        }

    }

    private boolean isEmailValid(String email) {
        if(email.contains("@")){
            return true;
        }
        return false;
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void initViews(){
        mfirstName = (EditText) findViewById(R.id.first_name);
        mlastName = (EditText) findViewById(R.id.last_name);
        mEmailAddress = (EditText) findViewById(R.id.email_signup);
        mPassword = (EditText) findViewById(R.id.password_signup);
        mReEnterPass = (EditText) findViewById(R.id.repassword_signup);
        mTelephone = (EditText) findViewById(R.id.signup_tel_num);
        mSignUpBtn = (Button) findViewById(R.id.btn_signup);
        mProgressView = findViewById(R.id.signup_progress);
        mSignUpFormView = findViewById(R.id.signup_form);
    }


    protected String readStream(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];

        reader.read(buffer);
        return new String(buffer);
    }

    protected String httpLogin(String username, String password) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL("http", SERVER_HOST, SERVER_PORT, SERVER_RESOURCE_LOGIN);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write("username=" + username + "&password=" + password);
            os.flush();
            os.close();

            Log.i("v", "Login HTTP response code: " + conn.getResponseCode());

            is = conn.getInputStream();
            int len = Integer.parseInt(conn.getHeaderField("Content-Length"));

            return readStream(is, len);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    //Async Task for SignUp
    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {
        private final String FirstName;
        private final String LastName;
        private final String Telephone;
        private final String Email;
        private final String Password;
        private final String Gender;

        UserSignUpTask(String Fname, String Lname, String Tphone, String email, String password, String gender) {
            FirstName = Fname;
            LastName = Lname;
            Telephone = Tphone;
            Email = email;
            Password = password;
            Gender = gender;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return false;
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPassword.setError(getString(R.string.error_incorrect_password));
                mPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
