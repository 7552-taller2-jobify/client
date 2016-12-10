package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;

import java.util.Calendar;

import ar.fi.uba.jobify.activities.MyContactsActivity;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class PostContactRequestTask extends AbstractTask<String,Void,String,MyContactsActivity> {

    private final MyPreferences pref;
    private MyPreferenceHelper helper;
    private String otherEmail;

    public PostContactRequestTask(MyContactsActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        otherEmail = params[0];

        Calendar c = Calendar.getInstance();
        String dateStr = DateUtils.formatDate(c.getTime());

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/contact"+
                "?token=" + token +
                "&email=" + otherEmail +
                "&date=" + dateStr;
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
        weakReference.get().onContactRequestSuccess(otherEmail);
    }

    public interface ContactAggregator {
        public void onContactRequestSuccess(String contact);
    }

}
