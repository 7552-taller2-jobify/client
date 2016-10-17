package ar.fi.uba.jobify.exceptions;

import android.util.Log;

import java.net.URL;
import java.util.Map;

import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.utils.AppSettings;

/**
 * Created by smpiano on 9/28/16.
 */
public class ServerErrorException extends RuntimeException {

    private ServerErrorException(String msg, Boolean logError) {
        super(msg);
        if (logError) Log.e("server_io_error", msg, this);
    }

    public ServerErrorException(String method, URL url, String body, Map<String, String> headers, Integer status, String msg) {
        this("Error en servidor, request ["+ RestClient.getCurl(method, url, body, headers)+"] -> response ["+status+" - "+msg+"]", true);
    }

    public ServerErrorException(String method, URL url, String body, Map<String, String> headers) {
        this("Error en servidor [" + RestClient.getCurl(method,url,body,headers) + "]", true);
    }

    public ServerErrorException(URL url, Integer status) {
        this("Error en servidor [" + AppSettings.getServerHost() + url.getPath() + "] status [" + status + "]", true);
    }

    public ServerErrorException(String msg) {
        this(msg, true);
    }

    public ServerErrorException(String msg, Throwable exception) {
        super(msg, exception);
    }
}
