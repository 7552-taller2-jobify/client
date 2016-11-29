package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ar.fi.uba.jobify.adapters.ContactListAdapter;
import ar.fi.uba.jobify.domains.Contact;
import ar.fi.uba.jobify.domains.ProfessionalSearchResult;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class GetMineContactListTask extends AbstractTask<String,Void,ProfessionalSearchResult,ContactListAdapter> {

    private final MyPreferences pref;
    private List<Contact> contacts;
    private MyPreferenceHelper helper;

    public GetMineContactListTask(ContactListAdapter adapter) {
        super(adapter);
        helper = new MyPreferenceHelper(adapter.getContext());
        pref = new MyPreferences(adapter.getContext());
    }

    @Override
    protected ProfessionalSearchResult doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/contacts?token="+token;
        ProfessionalSearchResult professionalSearchResult = null;
        try{
            professionalSearchResult = (ProfessionalSearchResult) restClient.get(urlString, withAuth(ctx));
        } catch (final ServerErrorException e) {
            weakReference.get().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getActivity().getApplicationContext(), e.getMessage());
                }
            });
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
        weakReference.get().addClients((professionalSearchResult !=null)? professionalSearchResult :new ProfessionalSearchResult());
    }

    public interface ClientsListAggregator {
        public void addClients(ProfessionalSearchResult professionalSearchResult);
    }

}
