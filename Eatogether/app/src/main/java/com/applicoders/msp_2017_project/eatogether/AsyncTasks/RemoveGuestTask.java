package com.applicoders.msp_2017_project.eatogether.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.applicoders.msp_2017_project.eatogether.HttpClasses.GenHttpConnection;
import com.applicoders.msp_2017_project.eatogether.Interfaces.RemoveGuestTaskDone;
import com.applicoders.msp_2017_project.eatogether.Interfaces.TaskDone;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by rafay on 4/22/2017.
 */

public class RemoveGuestTask extends AsyncTask<Void, Void, String> {
    public static RemoveGuestTask instance;
    private final HashMap KVP;
    private final String CallType;
    private final String ServerResource;
    private RemoveGuestTaskDone callingActivity;

    public RemoveGuestTask(RemoveGuestTaskDone _callingActivity, HashMap _KVP, String _CallType, String _serverResource) {
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
                Log.d("Join Task: ", "has error" );
                throw new Exception();
            }
            if (jsonObj.getBoolean("success")) {
                Log.d("Join Task: ", "is success" );
                String data = jsonObj.getString("data");

                if(callingActivity != null){
                    callingActivity.onGuestRemoved();
                }
            } else {
                Log.d("Join Task: ", "has failed" );
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
