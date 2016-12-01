package ar.fi.uba.jobify.utils;

/**
 * Created by smpiano on 9/28/16.
 */
public class AppSettings {

    //private static final String IP_DEFAULT = "192.168.22.10";
    private static final String IP_DEFAULT = "10.1.195.74";
    private static String SERVER_HOST = null;
    private static String HOST = null;

    private static final int SERVER_TIMEOUT = 5000; //5seg
    private static final Double GPS_LAT = -34.563424;
    private static final Double GPS_LON = -58.463874;


    public static String getServerHost(){
        if (SERVER_HOST == null) setServerHost(IP_DEFAULT);
        return SERVER_HOST;
    }

    public static void setServerHost(String host){
        HOST = host;
        SERVER_HOST = "http://" + host + ":8000";
    }

    public static int getServerTimeout() { return SERVER_TIMEOUT; }

    public static String getGpsLat() {
        return GPS_LAT.toString();
    }

    public static String getGpsLon() {
        return GPS_LON.toString();
    }

    public static String getHost() {
        if (SERVER_HOST == null) setServerHost(IP_DEFAULT);
        return HOST;
    }
}
