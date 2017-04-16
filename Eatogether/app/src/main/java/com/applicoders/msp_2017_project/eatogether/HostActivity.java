package com.applicoders.msp_2017_project.eatogether;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_HOST;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;


public class HostActivity extends AppCompatActivity implements PlaceSelectionListener {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private EditText dinnerTitle, numberOfGuests, description;
    private int year, month, day;
    private Button submit;
    private String LatLng;
    private PlaceAutocompleteFragment autocompleteFragment;
    private HostActivity.HostSignUpTask mAuthTask = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        try {
            initViews();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getAndPostData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        //showDate(year, month+1, day);
    }
    @Override
    public void onPlaceSelected(Place place) {
        //Toast.makeText(this, place.getLatLng().toString(), Toast.LENGTH_LONG).show();
        LatLng = place.getLatLng().toString();
    }

    @Override
    public void onError(Status status) {
        Log.i("DEBUG", "ERROR");
    }

    private void getAndPostData() throws ParseException {
        String dinner_title = dinnerTitle.getText().toString();
        String number_of_guests = numberOfGuests.getText().toString();
        String des = description.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaStr = year+"-"+month+"-"+day+" 10:49:29.10000";
        LatLng = (LatLng.replace(","," ")).substring(10,LatLng.length()-1);

        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        //Post this to server
        try {
            keyValuePair.put("token", TOKEN);
            keyValuePair.put("title", dinner_title);
            keyValuePair.put("noofguest", number_of_guests);
            keyValuePair.put("description", des);
            keyValuePair.put("datetime", fechaStr);
            keyValuePair.put("location", LatLng);
        }
        catch (Exception e){

        }
        mAuthTask = new HostActivity.HostSignUpTask(keyValuePair, "POST", SERVER_RESOURCE_HOST);
        mAuthTask.execute();
        Log.i("loc:",LatLng);
    }

    private void initViews() throws ParseException {
        dinnerTitle = (EditText) findViewById(R.id.dinnerTitle);
        numberOfGuests = (EditText) findViewById(R.id.numberOfGuests);
        description = (EditText) findViewById(R.id.description);
        submit = (Button) findViewById(R.id.submit);


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    year = arg1;
                    month = arg2 + 1;
                    day = arg3;
                    //showDate(arg1, arg2+1, arg3);
                }
            };


    public class HostSignUpTask extends AsyncTask<Void, Void, String> {
        private final HashMap KVP;
        private final String CallType;
        private final String ServerResource;
        public HostSignUpTask(HashMap<String, String> _KVP, String _Calltype, String _serverResource) {
            KVP = _KVP;
            CallType = _Calltype;
            ServerResource = _serverResource;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return GenHttpConnection.HttpCall(KVP, CallType, ServerResource);
            } catch (Exception e) {
                return "{\"error\":true}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("A", "Backend response: " + result);
            /* mAuthTask = null;
            showProgress(false);
            try{
                JSONObject jsonObj = new JSONObject(result);
                if (jsonObj.has("error")) {
                    throw new Exception();
                }

                if(jsonObj.getBoolean("success")){
                    TOKEN = jsonObj.getString("message");
                    Toast.makeText(getApplicationContext(), "Token: " + TOKEN, Toast.LENGTH_LONG).show();
                    Log.e("Token: ", TOKEN.toString());
                    mSignUpFormView.setVisibility(View.GONE);
                    // TODO: Store token and Start New Activity.
                }


            }
            catch (Exception e){
                Log.e("Exception", e.toString());
            }*/
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}
