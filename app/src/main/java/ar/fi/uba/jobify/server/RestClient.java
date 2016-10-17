package ar.fi.uba.jobify.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import ar.fi.uba.jobify.exceptions.ApiCallException;
import ar.fi.uba.jobify.exceptions.ErrorMatcher;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.ShowMessage;

/**
 * Created by smpiano on 9/28/16.
 */
public class RestClient {

    private final ResponseParse parser;

    public RestClient(ResponseParse parser) {
        this.parser = parser;
    }

    public Object get(String endpoint) throws RuntimeException{
        return connect("GET", endpoint, null, null);
    }

    public Object get(String endpoint, Map<String, String> headers) throws RuntimeException{
        return connect("GET", endpoint, null, headers);
    }

    public Object post(String endpoint, String body, Map<String, String> headers) throws RuntimeException{
        return connect("POST", endpoint, body, headers);
    }

    public Object put(String endpoint, String body, Map<String, String> headers) throws RuntimeException{
        return connect("PUT", endpoint, body, headers);
    }

    public Object delete(String endpoint, String body, Map<String, String> headers) throws RuntimeException{
        return connect("DELETE", endpoint, body, headers);
    }

    private Object connect(String method, String endpoint,String body, Map<String,String> headers) throws RuntimeException{
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String json;
        Object expectedReturn = null;
        URL url = null;
        int responseStatus = 0;
        InputStream inputStream = null;
        String errorMsg = null;
        Boolean isError = false;
        try {
            url = new URL(AppSettings.getServerHost() + endpoint);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (headers != null) {
                for(String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }
            urlConnection.setConnectTimeout(AppSettings.getServerTimeout());
            //writing the request
            if (method == "POST" || method == "PUT" || method=="DELETE") {
                urlConnection.setRequestMethod(method);
                if (body != null) {
                    byte[] outputInBytes = body.getBytes("UTF-8");
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(outputInBytes);
                    os.close();
                }
            } else if (method == "GET") {
                urlConnection.setRequestMethod(method);
            } else {
                throw new ApiCallException(url.getPath());
            }

            //debug
            Log.d("rest_client","["+getCurl(method,url,body,headers)+"]");

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

            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return expectedReturn;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                return expectedReturn;
            }
            json = buffer.toString();
            try {
                if (isError) {
                    readErrorResponse(json, urlConnection.getResponseCode());
                } else {
                    expectedReturn = parser.readResponse(json);
                }
            } catch (JSONException e) {
                Log.e("parse_response_error", "Error al leer el response de " + url.getPath(), e);
            }
        } catch (SocketTimeoutException e) {
            throw new ServerErrorException("El server no pudo responder antes del timeout ["+AppSettings.getServerTimeout()+"]");
        } catch (IOException e) {
            throw new ServerErrorException(method,url,body,headers,responseStatus,errorMsg);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("server_stream_error", "Error closing stream", e);
                }
            }
        }
        return expectedReturn;
    }

    private void readErrorResponse(String json, Integer status) throws ServerErrorException {
        String errorKey = "";
        String errorValue = "";
        try {
            if (json.startsWith("<!DOCTYPE html>")) throw new ServerErrorException("Se cayó el server");
            JSONObject errorObj = new JSONObject(json);
            JSONObject error = errorObj.getJSONObject("error");
            errorKey = error.getString("code");
            errorValue = error.getString("message");
            throw ErrorMatcher.valueOf(errorKey).getThrowable(errorValue,status);
        } catch (JSONException e) {
            throw new ServerErrorException("Error parseando server errorJson",e);
        } catch (IllegalArgumentException e) {
            throw ErrorMatcher.DEFAULT_ERROR.getThrowable(errorValue, status);
        }
    }


    // ------ COSAS BIZZARRAS MIAS
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!isOnline) {
            ShowMessage.toastMessage(ctx, "Estás desconectado!");
        }
        return isOnline;

    }

    public static String getCurl(String method, URL url, String body, Map<String, String> headers) {
        return "curl -X" + method + " '" + AppSettings.getServerHost() + getUrl(url)+ "' " + getBody(body) + getHeaders(headers);
    }

    private static String getUrl(URL url) {
        String res = url.getPath();
        String q = url.getQuery();
        if (q!=null) res+="?"+q;
        return res;
    }

    private static String getHeaders(Map<String, String> headers) {
        String headersStr="";
        if (headers != null){
            for(String k : headers.keySet()){
                headersStr+="-H '"+k+":"+headers.get(k)+"' ";
            }
        }
        return headersStr;
    }
    private static String getBody(String body){
        String bodyStr = "";
        if (body != null) {
            bodyStr = "-d '" + body + "' ";
        }
        return bodyStr;
    }

    public interface ResponseParse {
        public Object readResponse(String json) throws JSONException;
    }
}
