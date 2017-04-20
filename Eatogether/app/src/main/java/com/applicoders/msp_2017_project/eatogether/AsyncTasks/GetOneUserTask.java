package com.applicoders.msp_2017_project.eatogether.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;
import com.applicoders.msp_2017_project.eatogether.Interfaces.TaskDone;
import com.applicoders.msp_2017_project.eatogether.ProfileActivity;
import com.applicoders.msp_2017_project.eatogether.UtilityClasses.SharedPrefHandler;

import org.json.JSONObject;

import java.util.HashMap;

import static com.applicoders.msp_2017_project.eatogether.Constants.CONTEXT;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Bio_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Email_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_First_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Gender_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Last_Name_PREF;
import static com.applicoders.msp_2017_project.eatogether.Constants.User_Phone_PREF;

/**
 * Created by rafay on 4/19/2017.
 */

public class GetOneUserTask extends AsyncTask<Void, Void, String> {

    public static GetOneUserTask instance;
    private final HashMap KVP;
    private final String CallType;
    private final String ServerResource;
    private TaskDone callingActivity;

    public GetOneUserTask(TaskDone _callingActivity, HashMap _KVP, String _CallType, String _serverResource) {
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
//                String email = data.get("email").toString();
                String phone = data.get("phone").toString();
                String gender = data.get("gender").toString();
                String bio = "No Description Available";
                if(data.has("bio")){
                    bio = data.get("bio").toString();
                }
                SharedPrefHandler.StorePref(CONTEXT, User_First_Name_PREF, firstName);
                SharedPrefHandler.StorePref(CONTEXT, User_Last_Name_PREF, lastName);
                SharedPrefHandler.StorePref(CONTEXT, User_Phone_PREF, phone);
                SharedPrefHandler.StorePref(CONTEXT, User_Gender_PREF, gender);
                SharedPrefHandler.StorePref(CONTEXT, User_Bio_PREF, bio);

                Log.d("Get One User: ", "Data retrieved");

                if(callingActivity != null)
                    callingActivity.TaskCompleted();
            } else {
                Toast.makeText((Context) callingActivity, jsonObj.getString("message"), Toast.LENGTH_LONG).show();
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
