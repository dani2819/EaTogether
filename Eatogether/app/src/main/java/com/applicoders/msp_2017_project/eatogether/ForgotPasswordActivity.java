package com.applicoders.msp_2017_project.eatogether;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by rafay on 3/8/2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews() {
        mEmail = (EditText) findViewById(R.id.email_forgot_pass);
    }

}
