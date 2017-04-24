package com.applicoders.msp_2017_project.eatogether;

import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_STATS;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;

public class FirstFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private  FirstFragment.StatisticsTask mAuthTask=null;
    TextView testLabel;
    View view;
    ListView list;

    // newInstance constructor for creating fragment with arguments
    public static FirstFragment newInstance(int page, String title) {
        FirstFragment fragmentFirst = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_first_fragment, container, false);
        testLabel = (TextView) view.findViewById(R.id.testLabel);
        list = (ListView) view.findViewById(R.id.upComingHostList);

        //testLabel.setText(page + " -- " + title);
        prepareData();
        return view;
    }

    protected void prepareData(){
        HashMap<String, String> keyValuePair = new HashMap<String, String>();
        try {

            keyValuePair.put("token", TOKEN);
        }
        catch (Exception e){
        }

        mAuthTask = new FirstFragment.StatisticsTask(keyValuePair, "POST", SERVER_RESOURCE_STATS, this);
        mAuthTask.execute();
    }

    public class StatisticsTask extends AsyncTask<Void, Void, String> {
        private final HashMap KVP;
        private final String CallType;
        private final String ServerResource;
        private final FirstFragment pd;


        public StatisticsTask(HashMap<String, String> _KVP, String _Calltype, String _serverResource, FirstFragment _pd) {
            KVP = _KVP;
            CallType = _Calltype;
            ServerResource = _serverResource;
            pd = _pd;
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

                    Log.i("DEBUG::","Completed");
                    //deeeeeeee
                    if(page==0){
                        populateUpcomingHost(jsonObj);
                    }
                    if(page==1){
                        populatePreviousHost(jsonObj);
                    }




                }
            }
            catch (Exception e){
                Log.e("Exception", e.toString());
            }
        }

        public void populateUpcomingHost(JSONObject jsonObj) throws JSONException, IOException {
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

            //create view here
            FragListAdapter adapter= new FragListAdapter((StatsMainActivity)getActivity(), dishes_titles, locationArray, dateArray);

            list.setAdapter(adapter);
        }

        public void populatePreviousHost(JSONObject jsonObj) throws JSONException, IOException {
            ArrayList itemnames = new ArrayList();
            ArrayList locs = new ArrayList();
            ArrayList ids = new ArrayList();
            ArrayList dates = new ArrayList();
            JSONObject jsonData = (jsonObj.getJSONObject("data")).getJSONObject("host");
            JSONArray jsonMainArr = jsonData.getJSONArray("old");
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
            //create view here
            FragListAdapter2 adapter= new FragListAdapter2((StatsMainActivity)getActivity(), dishes_titles, locationArray, dateArray);

            list.setAdapter(adapter);


        }
        public String getAddress(double lat, double lng) throws IOException {
            Geocoder geocoder = new Geocoder((StatsMainActivity)getActivity(), Locale.getDefault());
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
