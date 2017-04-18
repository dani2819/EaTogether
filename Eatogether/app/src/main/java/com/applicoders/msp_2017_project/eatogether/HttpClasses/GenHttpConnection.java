package com.applicoders.msp_2017_project.eatogether.HttpClasses;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
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

/**
 * Created by rafay on 3/12/2017.
 */

public class GenHttpConnection {

    static String boundary = "------WebKitFormBoundary";

    public static String HttpCall(HashMap data, String CallType, String serverRes) throws IOException {
        return TextUtils.equals(CallType, "POST") ? PostCall(data, serverRes) : GetCall(data, serverRes);
    }

    public static String HttpCall(File sourceFile, String serverRes, String token) throws IOException {
        MultiPartUtils.prepareConnection(SERVER_HOST + serverRes, "UTF-8");
        MultiPartUtils.addHeaderField("x-access-token", token);
        MultiPartUtils.addHeaderField("Content-Type", "multipart/form-data; boundary=" + boundary);
        MultiPartUtils.setup_writer();
        MultiPartUtils.addFormField("token", token);
        MultiPartUtils.addFormField("bio", "112111111");
        MultiPartUtils.addFilePart("image", sourceFile);
        HttpURLConnection conn = MultiPartUtils.finish();
        Log.i("uploadFile", "HTTP Response is : " + conn.getResponseMessage() + ": " + conn.getResponseCode());
        InputStream is = conn.getInputStream();
        int len = Integer.parseInt(conn.getHeaderField("Content-Length"));
        return readStream(is, len);
    }

    private static String readStream(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];

        reader.read(buffer);
        return new String(buffer);
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(getPostDataString(KVPair));
            os.flush();
            os.close();
            Log.i("v", "Login HTTP response code: " + conn.getResponseCode());
            //is = conn.getInputStream();
            //int len = Integer.parseInt(conn.getHeaderField("Content-Length"));

            //response = readStream(is, len);
            int nextCharacter; // read() returns an int, we cast it to char later
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while(true){ // Infinite loop, can only be stopped by a "break" statement
                nextCharacter = reader.read(); // read() without parameters returns one character
                if(nextCharacter == -1) // A return value of -1 means that we reached the end
                    break;
                response += (char) nextCharacter; // The += operator appends the character to the end of the string
            }

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
        URL url = new URL(SERVER_HOST + serverResource+"?"+(getPostDataString(KVPair)));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.connect();
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
