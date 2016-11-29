package ar.fi.uba.jobify.tasks.search;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ar.fi.uba.jobify.adapters.PopProfessionalListAdapter;
import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.domains.PopProfessionalSearchResult;
import ar.fi.uba.jobify.domains.Professional;
import ar.fi.uba.jobify.domains.ProfessionalSearchResult;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class GetPopUsersTask extends AbstractTask<String,Void,PopProfessionalSearchResult,PopProfessionalListAdapter> {

    private List<Professional> contacts;
    private MyPreferences preferences;

    public GetPopUsersTask(PopProfessionalListAdapter adapter) {
        super(adapter);
        preferences = new MyPreferences(adapter.getContext());
    }

    @Override
    protected PopProfessionalSearchResult doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = preferences.get(ctx.getString(R.string.shared_pref_current_token),"");

        String urlString = "/users/pop?token="+token;
        PopProfessionalSearchResult professionalSearchResult = null;
        try{
            professionalSearchResult = (PopProfessionalSearchResult) restClient.get(urlString, withAuth(ctx));
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
        return PopProfessionalSearchResult.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(PopProfessionalSearchResult professionalSearchResult) {
        weakReference.get().addPopularProfessionals((professionalSearchResult !=null)? professionalSearchResult :new PopProfessionalSearchResult());
    }

    public interface ProfessionalListAggregator {
        public void addPopularProfessionals(PopProfessionalSearchResult professionalSearchResult);
    }

}
