package ar.fi.uba.jobify.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public abstract class AbstractTask<Params,Process,Return,Reference> extends AsyncTask<Params,Process,Return>  implements RestClient.ResponseParse {

    protected final RestClient restClient;

    protected WeakReference<Reference> weakReference;

    public AbstractTask(Reference ref) {
        weakReference = new WeakReference<Reference>(ref);
        restClient = new RestClient(this);
    }

    protected final boolean isOnline(Context ctx) {
        boolean isOnline = RestClient.isOnline(ctx);
        if (!isOnline) {
            ShowMessage.toastMessage(ctx, "Estás desconectado!");
        }
        return isOnline;
    }

    protected final Map<String, String> withAuth(Context ctx, Map<String, String> headers) {
        MyPreferences pref = new MyPreferences(ctx);
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        if (!token.isEmpty()) {
            if (headers == null) headers = new HashMap<String, String>();
            headers.put("authorization",token);
        } else {
            Log.e("auth_error_empty", "El token es vacio!");
        }
        return headers;
    }

    protected final Map<String, String> withAuth(Context ctx) {
        return withAuth(ctx, null);
    }

}
