package ar.fi.uba.jobify.service;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.ShowMessage;

/**
 * Created by smpiano on 12/27/16.
 */
public class InverseGeocodeService {

    private static final String API_KEY = "AIzaSyBF0xDEYnQXhrQ-DEuO55HB1AEW-Sofl9g";
    private static final String LOCATION_TYPE = "ROOFTOP";
    private static final String RESULT_TYPE = "street_address";
    private static final String URI = "https://maps.googleapis.com/maps/api/geocode/json";
    private final String request;
    private final Activity activity;

    public InverseGeocodeService(Activity activity, String lat, String lon) {
        request = URI +
                "?latlng=" + lat + "," + lon +
                "&key=" + API_KEY +
                "&location_type=" + LOCATION_TYPE +
                "&result_type=" + RESULT_TYPE;
        this.activity=activity;
        (new InverseGeocodeTask()).execute();
    }


    private class InverseGeocodeTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            URL url = null;
            int responseStatus = 0;
            String errorMsg = null;
            Boolean isError = false;
            InputStream inputStream = null;
            BufferedReader reader = null;

            String formattedAddress = "";
            try {
                url = new URL(request);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(AppSettings.getServerTimeout());
                urlConnection.setRequestMethod("GET");

                //debug
                Log.d("Inverse Google Geocode","[curl -XGET '"+request+"']");

                //makes the connection
                urlConnection.connect();

                //reading the response
                responseStatus = urlConnection.getResponseCode();
                errorMsg = urlConnection.getResponseMessage();
                isError = responseStatus >= HttpURLConnection.HTTP_BAD_REQUEST;
                if (isError) {
                    inputStream = urlConnection.getErrorStream();
                } else {
                    inputStream = urlConnection.getInputStream();
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() != 0) {
                    JSONObject json = new JSONObject(buffer.toString());

                    //Parseando
                    formattedAddress = json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                }

            } catch (final Exception e) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        ShowMessage.toastMessage(activity.getApplicationContext(), e.getMessage());
                    }
                });
            }
            return formattedAddress;
        }

        protected void onPostExecute(String feed) {
            ((ProfileActivity) activity).onInverseCalculationSuccess(feed);
        }
    }

    public interface InverseGeocodeServiceResult {
        public void onInverseCalculationSuccess(String address);
    }

}
