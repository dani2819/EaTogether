package com.applicoders.msp_2017_project.eatogether;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_STATS;
import static com.applicoders.msp_2017_project.eatogether.Constants.TOKEN;

public class StatsMainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    public class MyPagerAdapter extends FragmentPagerAdapter {
        public String d_t;
        private  MyPagerAdapter.StatisticsTask mAuthTask=null;
        private int NUM_ITEMS = 2;


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        
        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public android.app.Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    prepareData();
                    return FirstFragment.newInstance(0, this.d_t);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return FirstFragment.newInstance(1, "Page # 2");
                default:
                    return null;
            }
        }
        protected void prepareData(){
            HashMap<String, String> keyValuePair = new HashMap<String, String>();
            try {
                keyValuePair.put("token", TOKEN);
            }
            catch (Exception e){
            }

            mAuthTask = new MyPagerAdapter.StatisticsTask(keyValuePair, "POST", SERVER_RESOURCE_STATS, this);
            mAuthTask.execute();
        }

        public class StatisticsTask extends AsyncTask<Void, Void, String> {
            private final HashMap KVP;
            private final String CallType;
            private final String ServerResource;
            private final MyPagerAdapter pd;


            public StatisticsTask(HashMap<String, String> _KVP, String _Calltype, String _serverResource, MyPagerAdapter _pd) {
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
                        ArrayList itemnames = new ArrayList();
                        //ArrayList locs = new ArrayList();
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
                            //String newLocation = getAddress(Double.parseDouble(splited[0]), Double.parseDouble(splited[1]));
                            String datetime = rec.getString("datetime");
                            String date_only = datetime.substring(0,10);
                            dates.add(date_only);
                            //locs.add(newLocation);
                            ids.add(rec.getString("id"));
                        }

                        String[] dishes_titles = (String[]) itemnames.toArray(new String[itemnames.size()]);
                        //String[] locationArray = (String[]) locs.toArray(new String[locs.size()]);
                        String[] food_ids = (String[]) ids.toArray(new String[ids.size()]);
                        String[] dateArray = (String[]) dates.toArray(new String[dates.size()]);
                        pd.d_t = "its done";
                    }
                }
                catch (Exception e){
                    Log.e("Exception", e.toString());
                }
            }
        /*public String getAddress(double lat, double lng) throws IOException {
            Geocoder geocoder = new Geocoder(statsActivity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);

            Log.i("Location Name:", cityName+" "+stateName+" "+countryName);

            return cityName+" "+stateName+" "+countryName;
        }*/

            @Override
            protected void onCancelled() {
                mAuthTask = null;
                //showProgress(false);
            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "Upcoming Hosted Meals";
                case 1:
                    return "Previous Hostings";
                default:
                    return null;
            }

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_main);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getFragmentManager());
        vpPager.setAdapter(adapterViewPager);
    }

}
