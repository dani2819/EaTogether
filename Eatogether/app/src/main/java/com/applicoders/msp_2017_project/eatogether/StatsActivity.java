 package com.applicoders.msp_2017_project.eatogether;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_STATS;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;

public class StatsActivity extends AppCompatActivity {
    ListView list_upcoming_host;
    private  StatsActivity.StatisticsTask mAuthTask=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //prepare data for upcoming hosts to populate first list
        prepareData();
    }

    protected void prepareData(){
        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        try {
            keyValuePair.put("token", TOKEN);
        }
        catch (Exception e){
        }

        mAuthTask = new StatsActivity.StatisticsTask(keyValuePair, "POST", SERVER_RESOURCE_STATS, this);
        mAuthTask.execute();
    }

    public class StatisticsTask  extends AsyncTask<Void, Void, String>{
        private final HashMap KVP;
        private final String CallType;
        private final String ServerResource;
        private final Activity statsActivity;

        public StatisticsTask(HashMap<String, String> _KVP, String _Calltype, String _serverResource,  Activity _statsActivity) {
            KVP = _KVP;
            CallType = _Calltype;
            ServerResource = _serverResource;
            statsActivity = _statsActivity;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return GenHttpConnection.HttpCall(KVP, CallType, ServerResource);
            } catch (Exception e) {
                return "{\"error\":true}";
            }
        }

        protected void onPostExecute(String result) {
            Log.i("RESULTS:",result);
            mAuthTask = null;
            try{
                JSONObject jsonObj = new JSONObject(result);
                if (jsonObj.has("error")) {
                    throw new Exception();
                }

                if(jsonObj.getBoolean("success"))
                {
                    ArrayList itemnames = new ArrayList();
                    ArrayList locs = new ArrayList();
                    ArrayList ids = new ArrayList();
                    ArrayList dates = new ArrayList();
                    JSONObject jsonData = (jsonObj.getJSONObject("data")).getJSONObject("host");
                    JSONArray jsonMainArr = jsonData.getJSONArray("upcoming");
                    Log.i("JSON OBJ DATA:",jsonData.toString());
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
                        ids.add(rec.getString("id"));
                    }

                    String[] dishes_titles = (String[]) itemnames.toArray(new String[itemnames.size()]);
                    String[] locationArray = (String[]) locs.toArray(new String[locs.size()]);
                    String[] food_ids = (String[]) ids.toArray(new String[ids.size()]);
                    String[] dateArray = (String[]) dates.toArray(new String[dates.size()]);
                    populateListView(food_ids, dishes_titles, locationArray, dateArray, statsActivity);


                }
            }
            catch (Exception e){
                Log.e("Exception", e.toString());
            }

        }

        protected void populateListView(final String[] food_ids, String[] dishes_titles, String[] location_array, String[] dates, Activity statsActivity) throws IOException {
            StatsListAdapter adapter=new StatsListAdapter(statsActivity, dishes_titles, location_array, dates);
            list_upcoming_host = (ListView)findViewById(R.id.upComingHostList);
            list_upcoming_host.setAdapter(adapter);

            list_upcoming_host.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    String foodID= food_ids[+position];
                    Toast.makeText(getApplicationContext(), foodID, Toast.LENGTH_SHORT).show();
                    //Create new intent here and start activity by passing variable "foodID" (Id is in string, you can convert it into integer)


                }
            });


        }

        public String getAddress(double lat, double lng) throws IOException {
            Geocoder geocoder = new Geocoder(statsActivity, Locale.getDefault());
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

}
