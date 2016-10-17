package ar.fi.uba.jobify.utils;

/**
 * Created by smpiano on 9/28/16.
 */
public class AppSettings {

    //private static final String SERVER_HOST = "https://jobify-api.herokuapp.com";
    //private static final String SERVER_HOST = "http://172.17.0.1:8000";
    private static final String SERVER_HOST = "https://jobify-7552-taller2.herokuapp.com";

    private static final int SERVER_TIMEOUT = 15000; //15seg
    private static final Double GPS_LAT = -34.563424;
    private static final Double GPS_LON = -58.463874;


    public static String getServerHost(){
        return SERVER_HOST;
    }

    public static int getServerTimeout() { return SERVER_TIMEOUT; }

    public static String getGpsLat() {
        return GPS_LAT.toString();
    }

    public static String getGpsLon() {
        return GPS_LON.toString();
    }
}
