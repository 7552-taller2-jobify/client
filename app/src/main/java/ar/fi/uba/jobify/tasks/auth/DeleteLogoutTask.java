package ar.fi.uba.jobify.tasks.auth;

import org.json.JSONException;

import ar.fi.uba.jobify.activities.MainActivity;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.ShowMessage;

public class DeleteLogoutTask extends AbstractTask<String,Void,String,MainActivity> {

    public DeleteLogoutTask(MainActivity activity) {
        super(activity);
    }

    @Override
    protected String doInBackground(String... params) {
        String email = params[0];
        String token = params[1];
        String result = "Fail";
        try {
            restClient.delete("/users/" + email + "/logout?token=" + token, null, null);
            result = "Ok";
        } catch (BusinessException e) {
            ShowMessage.showSnackbarSimpleMessage(weakReference.get().getCurrentFocus(), e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return result;
    }

    @Override
    public Object readResponse(String result) throws JSONException {
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        weakReference.get().onLogoutSuccess();
    }

    public interface Logout {
        public void onLogoutSuccess();
    }
}
