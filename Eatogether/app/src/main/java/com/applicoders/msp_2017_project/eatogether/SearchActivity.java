package com.applicoders.msp_2017_project.eatogether;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.os.Build.VERSION_CODES.M;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_NEARBY;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;

public class SearchActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final int REQUEST_LOCATION = 1480;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng mCurrentLocation;
    private SearchActivity.NearbyNeighborsTask mAuthTask=null;
    private View mProgressView;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        list = (ListView)findViewById(R.id.list);
        mProgressView = findViewById(R.id.signup_progress);
        // show The Image in a ImageView
        //new DownloadImageTask((ImageView) findViewById(R.id.imgView))
        //  .execute("https://cdn.pixabay.com/photo/2016/03/28/12/35/cat-1285634_960_720.png");




        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= arr[+position];
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });*/

        initializeListeners();
        mayRequestLocation();
    }

    private void initializeListeners() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .build();
        }
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }


    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT < M ||
                (ContextCompat.checkSelfPermission(SearchActivity.this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(SearchActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

        }
        else {
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeListeners();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)  {
        try {
            Location lastLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(lastLoc == null){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else{
                handleNewLocation(lastLoc);
            }

        }
        catch (SecurityException e){

        }

    }

    private void handleNewLocation(Location location) {
        showProgress(true);
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        String LatLng;
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng = mCurrentLocation.toString();
        LatLng = (LatLng.replace(","," ")).substring(10,LatLng.length()-1);
        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        //Post this to server

        try {
            keyValuePair.put("token", TOKEN);
            keyValuePair.put("location", LatLng);
            Log.i("CHECK;", keyValuePair.toString());
        }
        catch (Exception e){
            Log.i("ERROR:", "key value pairs");
        }
        mAuthTask = new SearchActivity.NearbyNeighborsTask(keyValuePair, "GET", SERVER_RESOURCE_NEARBY, this);
        mAuthTask.execute();
        //Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("DEBUG", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public class NearbyNeighborsTask extends AsyncTask<Void, Void, String> {
        private final HashMap KVP;
        private final String CallType;
        private final String ServerResource;
        private final Activity searchActivity;
        public NearbyNeighborsTask(HashMap<String, String> _KVP, String _Calltype, String _serverResource, Activity _searchActivity) {
            KVP = _KVP;
            CallType = _Calltype;
            ServerResource = _serverResource;
            searchActivity = _searchActivity;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                return GenHttpConnection.HttpCall(KVP, CallType, ServerResource);
            } catch (Exception e) {
                return "{\"error\":true}";
            }
        }

        /*
        @Override
        protected  void onPreExecute(){
            showProgress(true);
        }*/

        @Override
        protected void onPostExecute(String result) {
            showProgress(false);
            Log.i("A", "Backend response: " + result);
            mAuthTask = null;
            try{
                JSONObject jsonObj = new JSONObject(result);
                if (jsonObj.has("error")) {
                    throw new Exception();
                }

                if(jsonObj.getBoolean("success"))
                {
                    TOKEN = jsonObj.getString("message");
                    ArrayList itemnames = new ArrayList();
                    ArrayList locs = new ArrayList();
                    ArrayList guests = new ArrayList();
                    ArrayList ids = new ArrayList();
                    ArrayList dates = new ArrayList();
                    ArrayList times = new ArrayList();
                    JSONArray jsonMainArr = jsonObj.getJSONArray("data");

                    for (int i = 0; i < jsonMainArr.length(); ++i) {
                        JSONObject rec = jsonMainArr.getJSONObject(i);
                        itemnames.add(rec.getString("title"));
                        String tempLocation = rec.getString("location");
                        String[] splited = tempLocation.split("\\s+");
                        String newLocation = getAddress(Double.parseDouble(splited[0]), Double.parseDouble(splited[1]));
                        String datetime = rec.getString("datetime");
                        String date_only = datetime.substring(0,10);
                        dates.add(date_only);
                        locs.add(newLocation);
                        guests.add(rec.getString("noofguest"));
                        ids.add(rec.getString("id"));
                        Log.i("JSON PARSER:", rec.getString("id") );
                    }

                    String[] dishes_titles = (String[]) itemnames.toArray(new String[itemnames.size()]);
                    String[] locationArray = (String[]) locs.toArray(new String[locs.size()]);
                    String[] guest = (String[]) guests.toArray(new String[guests.size()]);
                    String[] food_ids = (String[]) ids.toArray(new String[ids.size()]);
                    String[] dateArray = (String[]) dates.toArray(new String[dates.size()]);
                    populateListView(food_ids, dishes_titles, locationArray, guest, dateArray);


                }
            }
            catch (Exception e){
                Log.e("Exception", e.toString());
            }
        }

        protected void populateListView(final String[] food_ids, String[] dishes_titles, String[] location_array, String[] guests, String[] dates) throws IOException {

            CustomListAdapter adapter=new CustomListAdapter(searchActivity, dishes_titles, location_array, guests, dates);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String foodID= food_ids[+position];
                Toast.makeText(getApplicationContext(), foodID, Toast.LENGTH_SHORT).show();

                Intent newActivity = new Intent(getApplicationContext(), FoodEventActivity.class);
                newActivity.putExtra("foodID", foodID);
                startActivity(newActivity);
            }
        });


        }

        public String getAddress(double lat, double lng) throws IOException {

            Geocoder geocoder = new Geocoder(searchActivity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);

            Log.i("Location Name:", cityName+" "+stateName+" "+countryName);

            return cityName+" "+stateName+" "+countryName;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
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

            list.setVisibility(show ? View.GONE : View.VISIBLE);
            list.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    list.setVisibility(show ? View.GONE : View.VISIBLE);
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
            list.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}


