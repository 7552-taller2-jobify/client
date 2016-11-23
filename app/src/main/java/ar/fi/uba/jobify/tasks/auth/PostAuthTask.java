package ar.fi.uba.jobify.tasks.auth;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.fi.uba.jobify.activities.LoginActivity;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.ShowMessage;

public class PostAuthTask extends AbstractTask<String,Void,Token,LoginActivity> {

    public PostAuthTask(LoginActivity activity) {
        super(activity);
    }

    @Override
    protected Token doInBackground(String... params) {
        String email = params[0];
        String pass = params[1];
        String body = "{\"email\": \""+email+"\",\"password\":\""+pass+"\"}";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        Token token = null;
        try {
            token = (Token) restClient.post("/users/login", body, headers);
        } catch (BusinessException e) {
            ShowMessage.showSnackbarSimpleMessage(weakReference.get().getCurrentFocus(), e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });

        }
        return token;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        Log.d("login json", json);
        JSONObject tokenJson = new JSONObject(json);
        return Token.fromJson(tokenJson);
    }

    @Override
    protected void onPostExecute(Token token) {
        super.onPostExecute(token);
        weakReference.get().onLoginSuccess(token);
    }

    public interface ResultLogin {
        public void onLoginSuccess(Token token);
    }
}
