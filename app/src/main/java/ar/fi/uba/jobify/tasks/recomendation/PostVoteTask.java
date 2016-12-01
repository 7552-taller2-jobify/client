package ar.fi.uba.jobify.tasks.recomendation;

import android.content.Context;

import org.json.JSONException;

import ar.fi.uba.jobify.activities.MyContactsActivity;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class PostVoteTask extends AbstractTask<String,Void,String,MyContactsActivity> {

    private final MyPreferences pref;
    private MyPreferenceHelper helper;

    public PostVoteTask(MyContactsActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String otherEmail = params[0];

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/vote"+
                "?token="+token+
                "&email="+otherEmail;
        try{
            restClient.post(urlString, null, withAuth(ctx));
        } catch (final ServerErrorException e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.showSnackbarSimpleMessage(weakReference.get().getCurrentFocus(), e.getMessage());
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
        weakReference.get().onVoteSuccess();
    }

    public interface Recomendation {
        public void onVoteSuccess();
    }

}
