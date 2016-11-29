package ar.fi.uba.jobify.tasks.auth;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.fi.uba.jobify.activities.LoginActivity;
import ar.fi.uba.jobify.domains.ForgotPassword;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.ShowMessage;

public class GetForgotPasswordTask extends AbstractTask<String,Void,ForgotPassword,LoginActivity> {

    public GetForgotPasswordTask(LoginActivity activity) {
        super(activity);
    }

    @Override
    protected ForgotPassword doInBackground(String... params) {
        String email = params[0];
        ForgotPassword fp = null;
        try {
            fp = (ForgotPassword) restClient.get("/users/"+email+"/recovery_pass");
        } catch (BusinessException e) {
            ShowMessage.showSnackbarSimpleMessage(weakReference.get().getCurrentFocus(), e.getMessage());
        } catch (final ServerErrorException e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        } catch (Exception e) {
            ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
        }
        return fp;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        Log.d("ForgotPassword json", json);
        return ForgotPassword.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ForgotPassword forgotPassword) {
        super.onPostExecute(forgotPassword);
        weakReference.get().onForgotPasswordSuccess(forgotPassword);
    }

    public interface ResultForgotPassword {
        public void onForgotPasswordSuccess(ForgotPassword forgotPassword);
    }
}
