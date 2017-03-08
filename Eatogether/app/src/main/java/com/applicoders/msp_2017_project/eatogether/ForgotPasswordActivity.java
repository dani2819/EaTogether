package com.applicoders.msp_2017_project.eatogether;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by rafay on 3/8/2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    private View mProgressView;
    private View mForgetPassFormView;
    EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    private void initViews() {
        mEmail = (EditText) findViewById(R.id.email_forgot_pass);
        mProgressView = findViewById(R.id.forgot_pass_progress);
        mForgetPassFormView = findViewById(R.id.forgot_pass_form);
    }

}
