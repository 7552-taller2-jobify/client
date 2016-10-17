package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ar.fi.uba.jobify.adapters.ContactListAdapter;
import ar.fi.uba.jobify.domains.Contact;
import ar.fi.uba.jobify.domains.ContactSearchResult;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.soldme.R;


public class GetContactListTask extends AbstractTask<String,Void,ContactSearchResult,ContactListAdapter> {

    private List<Contact> contacts;
    private MyPreferenceHelper helper;
    private String key;

    public GetContactListTask(ContactListAdapter adapter) {
        super(adapter);
        this.key = adapter.getContext().getString(R.string.shared_pref_current_seller);
        helper = new MyPreferenceHelper(adapter.getContext());
    }

    @Override
    protected ContactSearchResult doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String urlString = "";
        if (params.length == 0) {
            urlString = "/v1/contacts?limit=1000";
        } else if (params.length == 1) {
            urlString = "/v1/contacts?limit=10&offset=" + params[0];
        } else {
            String offset = params[0];
            String lat = params[1];
            String lon = params[2];
            urlString = "/v1/contacts?limit=10&offset="+offset+"&lat="+lat+"&lon="+lon+"&order=distance";
        }
        urlString+="&seller_id="+ helper.getSeller().getId();

        ContactSearchResult contactSearchResult = null;
        try{
            contactSearchResult = (ContactSearchResult) restClient.get(urlString, withAuth(ctx));
        } catch (final Exception e) {
            weakReference.get().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.showSnackbarSimpleMessage(weakReference.get().getActivity().getCurrentFocus(), e.getMessage());
                }
            });
        }
        return contactSearchResult;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        JSONObject clientsList = new JSONObject(json);
        return ContactSearchResult.fromJson(clientsList);
    }

    @Override
    protected void onPostExecute(ContactSearchResult contactSearchResult) {
        weakReference.get().addClients((contactSearchResult !=null)? contactSearchResult :new ContactSearchResult());
    }

    public interface ClientsListAggregator {
        public void addClients(ContactSearchResult contactSearchResult);
    }

}
