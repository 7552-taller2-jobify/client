package ar.fi.uba.jobify.tasks.recomendation;

import android.content.Context;

import org.json.JSONException;

import ar.fi.uba.jobify.activities.MyContactsActivity;
import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class DeleteVoteTask extends AbstractTask<String,Void,String,ProfessionalListAdapter> {

    private final MyPreferences pref;
    private MyPreferenceHelper helper;
    private String otherEmail;

    public DeleteVoteTask(ProfessionalListAdapter adapter) {
        super(adapter);
        helper = new MyPreferenceHelper(adapter.getActivity().getApplicationContext());
        pref = new MyPreferences(adapter.getActivity().getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        otherEmail = params[0];

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/vote"+
                "?token="+token+
                "&email="+otherEmail;
        try{
            restClient.delete(urlString, null, withAuth(ctx));
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
        return "ok";
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return json;
    }

    @Override
    protected void onPostExecute(String str) {
        weakReference.get().onUnvoteSuccess(otherEmail);
    }

    public interface Recomendation {
        public void onUnvoteSuccess(String contact);
    }

}
