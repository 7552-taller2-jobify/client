package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.domains.ProfessionalFriendsResult;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.domains.ProfessionalSearchResult;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class GetMineContactListTask extends AbstractTask<String,Void,ProfessionalFriendsResult,ProfessionalListAdapter> {

    private final MyPreferences pref;
    private MyPreferenceHelper helper;

    public GetMineContactListTask(ProfessionalListAdapter adapter) {
        super(adapter);
        helper = new MyPreferenceHelper(adapter.getContext());
        pref = new MyPreferences(adapter.getContext());
    }

    @Override
    protected ProfessionalFriendsResult doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/profile/contacts?token="+token;
        ProfessionalFriendsResult professionalFriendsResult = null;
        try{
            professionalFriendsResult = (ProfessionalFriendsResult) restClient.get(urlString, withAuth(ctx));
        } catch (final Exception e) {
            weakReference.get().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.showSnackbarSimpleMessage(weakReference.get().getActivity().getCurrentFocus(), e.getMessage());
                }
            });
        }
        return professionalFriendsResult;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        JSONObject clientsList = new JSONObject(json);
        return ProfessionalFriendsResult.fromJson(clientsList);
    }

    @Override
    protected void onPostExecute(ProfessionalFriendsResult professionalFriendsResult) {
        weakReference.get().addFriends((professionalFriendsResult !=null)? professionalFriendsResult :new ProfessionalFriendsResult());
    }

    public interface ProfessionalListAggregator {
        public void addFriends(ProfessionalFriendsResult professionalFriendsResult);
    }

}
