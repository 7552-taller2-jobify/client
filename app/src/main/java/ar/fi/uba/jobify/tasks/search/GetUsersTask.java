package ar.fi.uba.jobify.tasks.search;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.domains.Professional;
import ar.fi.uba.jobify.domains.ProfessionalSearchResult;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class GetUsersTask extends AbstractTask<String,Void,ProfessionalSearchResult,ProfessionalListAdapter> {

    private List<Professional> contacts;
    private MyPreferences preferences;

    public GetUsersTask(ProfessionalListAdapter adapter) {
        super(adapter);
        preferences = new MyPreferences(adapter.getContext());
    }

    @Override
    protected ProfessionalSearchResult doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = preferences.get(ctx.getString(R.string.shared_pref_current_token),"");
        String urlString;
        if (params.length == 0) {
            urlString = "/users?token="+token;
        } else {
            String offset = params[0];
            String lat = params[1];
            String lon = params[2];
            String distance = params[3];
            String position = params[4];
            String skills = params[5];

            urlString = "/users?" +
                    "token="+token+
                    "&lat="+lat+
                    "&lon="+lon+
                    "&distance="+distance+
                    "&position="+position+
                    "&skills="+skills+
                    "&limit="+1000+
                    "&offset="+offset;
        }
        ProfessionalSearchResult professionalSearchResult = null;
        try{
            professionalSearchResult = (ProfessionalSearchResult) restClient.get(urlString, withAuth(ctx));
        } catch (final Exception e) {
            weakReference.get().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.showSnackbarSimpleMessage(weakReference.get().getActivity().getCurrentFocus(), e.getMessage());
                }
            });
        }
        return professionalSearchResult;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        JSONObject clientsList = new JSONObject(json);
        return ProfessionalSearchResult.fromJson(clientsList);
    }

    @Override
    protected void onPostExecute(ProfessionalSearchResult professionalSearchResult) {
        weakReference.get().addProfessionals((professionalSearchResult !=null)? professionalSearchResult :new ProfessionalSearchResult());
    }

    public interface ProfessionalListAggregator {
        public void addProfessionals(ProfessionalSearchResult professionalSearchResult);
    }

}
