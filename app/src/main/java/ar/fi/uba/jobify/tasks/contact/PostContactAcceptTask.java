package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;

import java.util.Calendar;

import ar.fi.uba.jobify.activities.MyContactsActivity;
import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class PostContactAcceptTask extends AbstractTask<String,Void,String,ProfessionalListAdapter> {

    private final MyPreferences pref;
    private MyPreferenceHelper helper;
    private String otherEmail;

    public PostContactAcceptTask(ProfessionalListAdapter adapter) {
        super(adapter);
        helper = new MyPreferenceHelper(adapter.getActivity().getApplicationContext());
        pref = new MyPreferences(adapter.getActivity().getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get().getContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        otherEmail = params[0];

        Calendar c = Calendar.getInstance();
        String dateStr = DateUtils.formatDate(c.getTime());

        String urlString = "/users/" + helper.getProfessional().getEmail() + "/accept"+
                "?token="+token+
                "&date="+dateStr+
                "&email="+otherEmail;
        try{
            restClient.post(urlString, null, withAuth(ctx));
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
        weakReference.get().onContactAcceptSuccess(otherEmail);
    }

    public interface ContactAggregator {
        public void onContactAcceptSuccess(String contact);
    }

}
