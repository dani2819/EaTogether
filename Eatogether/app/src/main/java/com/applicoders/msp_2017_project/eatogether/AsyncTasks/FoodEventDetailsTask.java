package com.applicoders.msp_2017_project.eatogether.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.applicoders.msp_2017_project.eatogether.FoodEventActivity;
import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;
import com.applicoders.msp_2017_project.eatogether.ProfileActivity;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.applicoders.msp_2017_project.eatogether.Constants.User_Bio_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Email_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_First_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Gender_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Last_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Phone_PREF;

/**
 * Created by rafay on 4/16/2017.
 */

public class FoodEventDetailsTask extends AsyncTask<Void, Void, String> {
    public static FoodEventDetailsTask instance;
    private final HashMap KVP;
    private final String CallType;
    private final String ServerResource;
    private FoodEventActivity callingActivity;

    public FoodEventDetailsTask(FoodEventActivity _callingActivity, HashMap _KVP, String _CallType, String _serverResource) {
        instance = this;
        KVP = _KVP;
        CallType = _CallType;
        ServerResource = _serverResource;
        this.callingActivity = _callingActivity;
    }

    @Override
    protected String doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            return GenHttpConnection.HttpCall(KVP, CallType, ServerResource);
        } catch (Exception e) {
            return "{\"error\":true}";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("A", "Backend response: " + result);
        instance = null;

        try {
            JSONObject jsonObj = new JSONObject(result);
            if (jsonObj.has("error")) {
                throw new Exception();
            }
            if (jsonObj.getBoolean("success")) {
                JSONObject data = jsonObj.getJSONObject("data");
                String firstName = data.get("firstname").toString();
                String lastName = data.get("lastname").toString();
                String description = data.get("description").toString();
                String title = data.get("title").toString();
                String location = data.get("location").toString();
                String dateTime = data.get("datetime").toString();
                String numOfGuests = data.get("noofguest").toString();
                String hostID = data.get("userid").toString();
                String joinedGuests = data.get("joining").toString();
                Boolean isHost = data.getBoolean("ishost");
                Boolean canJoin = data.getBoolean("canjoin");
                Boolean hasJoined = data.getBoolean("hasjoined");


                HashMap<String, Object> dataToSend = new HashMap<String, Object>();
                dataToSend.put("firstname", firstName);
                dataToSend.put("lastname", lastName);
                dataToSend.put("desciption", description);
                dataToSend.put("title", title);
                dataToSend.put("location", location);
                dataToSend.put("dateTime", dateTime);
                dataToSend.put("numOfGuests", numOfGuests);
                dataToSend.put("hostID", hostID);
                dataToSend.put("joinedGuests", joinedGuests);
                dataToSend.put("isHost", isHost);
                dataToSend.put("canJoin", canJoin);
                dataToSend.put("hasjoined", hasJoined);

                HashMap<String, HashMap<String, String>> guestListToSend = new HashMap();
                if(isHost) {
                    // TODO Get data from Json array and populate guests list
                    JSONArray guestsList = data.getJSONArray("guests");
                    for (int i = 0; i < guestsList.length(); i++) {
                        JSONObject record = guestsList.getJSONObject(i);
                        HashMap<String, String> recordData = new HashMap();
                        recordData.put("firstname", record.get("firstname").toString());
                        recordData.put("lastname", record.get("lastname").toString());
                        recordData.put("userID", record.get("uid").toString());
                        recordData.put("joined", record.get("joined").toString());
                        guestListToSend.put("Guest" + i, recordData);
                    }
                }

                if(callingActivity != null) {
                    callingActivity.dataRecieved(dataToSend);
                    if(isHost){
                        callingActivity.populateGuestList(guestListToSend);
                    }
                }
            } else {
                Toast.makeText(callingActivity, jsonObj.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            Log.v("Error", e.toString());
        }
    }

    @Override
    protected void onCancelled() {
        instance = null;
    }
}
