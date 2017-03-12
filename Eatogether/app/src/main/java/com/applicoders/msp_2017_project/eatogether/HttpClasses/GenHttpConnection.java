package com.applicoders.msp_2017_project.eatogether.HttpClasses;

import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_HOST;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_PORT;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_LOGIN;
import static com.applicoders.msp_2017_project.eatogether.Constants.SERVER_RESOURCE_SIGNUP;

/**
 * Created by rafay on 3/12/2017.
 */

public class GenHttpConnection {

    public static String HttpCall(HashMap data, String CallType, String serverRes) throws IOException{
        return TextUtils.equals(CallType, "POST")? PostCall(data, serverRes) : GetCall(data, serverRes);
    }

    private static String readStream(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];

        reader.read(buffer);
        return new String(buffer);
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static String PostCall(HashMap KVPair, String serverResource) throws IOException {
        InputStream is = null;
        String response = "";
        URL url = new URL(SERVER_HOST + serverResource);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.connect();

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(getPostDataString(KVPair));
            os.flush();
            os.close();
            Log.i("v", "Login HTTP response code: " + conn.getResponseCode());
            is = conn.getInputStream();
            int len = Integer.parseInt(conn.getHeaderField("Content-Length"));

            response = readStream(is, len);

        } finally {
            conn.disconnect();
            if (is != null) {
                is.close();
            }
            return response;
        }
    }


    private static String GetCall(HashMap KVPair, String serverResource) throws IOException {
        InputStream is = null;
        String response = "";
        URL url = new URL(SERVER_HOST + serverResource);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            //conn.setChunkedStreamingMode(0);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.connect();

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(getPostDataString(KVPair));
            os.flush();
            os.close();
            Log.i("v", "Login HTTP response code: " + conn.getResponseCode());
            is = conn.getInputStream();
            int len = Integer.parseInt(conn.getHeaderField("Content-Length"));

            response = readStream(is, len);

        } finally {
            conn.disconnect();
            if (is != null) {
                is.close();
            }
            return response;
        }
    }
}
