package com.applicoders.msp_2017_project.eatogether;

import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
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

import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
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

    ListView list;
    String[] itemname ={
            "Mexican Pasta",
            "Pakistani Butter Karahi",
            "Italaian Cheese Burger",
            "Finnish Lora",
            "Indian Chutyapa",
            "Delicious Gobhi",
            "Aaloo Palak",
            "Khawaab Geena",
            "Mexican Pasta",
            "Pakistani Butter Karahi",
            "Italaian Cheese Burger",
            "Finnish Lora",
            "Indian Chutyapa",
            "Delicious Gobhi",
            "Aaloo Palak",
            "Khawaab Geena"
    };
    String[] loc ={
            "Ruohalati 23, Helsinki",
            "Otavantie 3, Helsinki",
            "Servin Maijan Tie 12, Espoo",
            "Luuvantie 1, Espoo",
            "Ruohalati 23, Helsinki",
            "Otavantie 3, Helsinki",
            "Servin Maijan Tie 12, Espoo",
            "Luuvantie 1, Espoo",
            "Ruohalati 23, Helsinki",
            "Otavantie 3, Helsinki",
            "Servin Maijan Tie 12, Espoo",
            "Luuvantie 1, Espoo",
            "Ruohalati 23, Helsinki",
            "Otavantie 3, Helsinki",
            "Servin Maijan Tie 12, Espoo",
            "Luuvantie 1, Espoo"
    };


    Integer[] imgid = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic2,
            R.drawable.pic1
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // show The Image in a ImageView
        //new DownloadImageTask((ImageView) findViewById(R.id.imgView))
        //  .execute("https://cdn.pixabay.com/photo/2016/03/28/12/35/cat-1285634_960_720.png");
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid, loc);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= itemname[+position];
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
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
        mAuthTask = new SearchActivity.NearbyNeighborsTask(keyValuePair, "GET", SERVER_RESOURCE_NEARBY);
        mAuthTask.execute();
        Toast.makeText(this, mCurrentLocation.toString(), Toast.LENGTH_SHORT).show();
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
        public NearbyNeighborsTask(HashMap<String, String> _KVP, String _Calltype, String _serverResource) {
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

