package ar.fi.uba.jobify.utils;

import android.content.Context;

import com.google.gson.Gson;

import ar.fi.uba.jobify.domains.Professional;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class MyPreferenceHelper {

    private final Context context;

    public MyPreferenceHelper(Context context) {
        this.context = context;
    }

    public void saveSeller(Professional professional) {
        MyPreferences pref = new MyPreferences(context);
        pref.save(context.getString(R.string.shared_pref_current_seller), "");
        Gson gson = new Gson();
        String json = gson.toJson(professional);
        pref.save(context.getString(R.string.shared_pref_current_seller), json);
    }

    public Professional getSeller() {
        MyPreferences pref = new MyPreferences(context);
        Gson gson = new Gson();
        String json = pref.get(context.getString(R.string.shared_pref_current_seller), "");
        Professional professional = gson.fromJson(json, Professional.class);
        return professional;
    }
}
