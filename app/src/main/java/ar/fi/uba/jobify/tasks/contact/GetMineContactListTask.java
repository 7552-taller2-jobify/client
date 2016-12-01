package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.adapters.ContactListAdapter;
import ar.fi.uba.jobify.domains.ProfileContactsResult;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class GetMineContactListTask extends AbstractTask<String,Void,ProfileContactsResult,ContactListAdapter> {

    private final MyPreferences pref;
    private MyPreferenceHelper helper;

    public GetMineContactListTask(ContactListAdapter adapter) {
        super(adapter);
        helper = new MyPreferenceHelper(adapter.getContext());
        pref = new MyPreferences(adapter.getContext());
    }

    @Override
    protected ProfileContactsResult doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/profile/contacts?token="+token;
        ProfileContactsResult profileContactsResult = null;
        try{
            profileContactsResult = (ProfileContactsResult) restClient.get(urlString, withAuth(ctx));
        } catch (final Exception e) {
            weakReference.get().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.showSnackbarSimpleMessage(weakReference.get().getActivity().getCurrentFocus(), e.getMessage());
                }
            });
        }
        return profileContactsResult;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        JSONObject clientsList = new JSONObject(json);
        return ProfileContactsResult.fromJson(clientsList);
    }

    @Override
    protected void onPostExecute(ProfileContactsResult profileContactsResult) {
        weakReference.get().addClients((profileContactsResult !=null)? profileContactsResult :new ProfileContactsResult());
    }

    public interface ClientsListAggregator {
        public void addClients(ProfileContactsResult profileContactsResult);
    }

}
