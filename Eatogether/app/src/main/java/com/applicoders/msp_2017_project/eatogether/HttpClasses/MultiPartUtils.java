package com.applicoders.msp_2017_project.eatogether.HttpClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rafay on 4/16/2017.
 */

public class MultiPartUtils {
    private static String boundary = "";
    private static final String LINE_FEED = "\r\n";
    private static HttpURLConnection httpConn;
    protected static String charset;
    protected static OutputStream outputStream;
    protected static PrintWriter writer;
    public static boolean cancel = false;
    protected static boolean last_item_is_file = false;
//        public static String LOG_TAG = "MultipartUtil";

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param _charset
     * @throws IOException
     */
    public static void prepareConnection(String requestURL, String _charset)
            throws IOException {
        charset = _charset;

        // creates a unique boundary based on time stamp
        boundary = "------WebKitFormBoundary";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Connection", "Keep-Alive");
//        httpConn.setRequestProperty("User-Agent", "Android submit");
        //addHeaderField("lang", AppConfig.getInstance().language);
        //addHeaderField("country", AppConfig.getInstance().country);
        //User user = AppConfig.getInstance().get_user();
        /*
        if (send_auth && user != null && user.authentication_token != null && !user.authentication_token.equalsIgnoreCase("")) {
            Log.i(LOG_TAG, "user token: "+user.authentication_token);
            addHeaderField("X-User-Email", user.email);
            addHeaderField("X-User-Token", user.authentication_token);
        } else {
            Log.i(LOG_TAG, "not auth / not logged in");
        }
        */
        // httpConn.setRequestProperty("Test", "Bonjour");
    }

    public static PrintWriter setup_writer() throws IOException {
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
        return writer;
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public static void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
//        writer.append("Content-Type: text/plain; charset=" + charset).append(
//                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
        last_item_is_file = false;
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public static void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
//        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while (!cancel && (bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        if (cancel) {
            writer.close();
            return;
        }

        writer.append(LINE_FEED);
        writer.flush();
        last_item_is_file = true;
    }

    public static void addFileStreamPart(String fieldName, String fileName, InputStream inputStream)
            throws IOException {

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while (!cancel && (bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.flush();
        inputStream.close();
        if (cancel) {
            writer.close();
            return;
        }

        writer.append(LINE_FEED);
        writer.flush();
        last_item_is_file = true;
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public static void addHeaderField(String name, String value) {
        httpConn.setRequestProperty(name, value);
        //writer.append(name + ": " + value).append(LINE_FEED).flush();
        //addFormField("headers["+name+"]", value);
        //writer.append(name + ": " + value).append(LINE_FEED).flush();
    }

    public static HttpURLConnection finish() throws IOException {

        if (last_item_is_file)
            writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        return httpConn;
    }
}