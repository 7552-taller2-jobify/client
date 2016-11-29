package ar.fi.uba.jobify.tasks.auth;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.fi.uba.jobify.activities.LoginActivity;
import ar.fi.uba.jobify.activities.RegistryActivity;
import ar.fi.uba.jobify.domains.Register;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.exceptions.ServerErrorException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.ShowMessage;

public class PostRegistryTask extends AbstractTask<String,Void,Register,RegistryActivity> {

    public PostRegistryTask(RegistryActivity activity) {
        super(activity);
    }

    @Override
    protected Register doInBackground(String... params) {
        String email = params[0];
        String password = params[1];
        String firstName = params[2];
        String lastName = params[3];
        String gender = params[4];
        String birthday = params[5];
        String lat = params[6];
        String lon = params[7];
        String deviceId = params[8];

        String body = "{\"email\": \"" + email + "\"," +
                "\"password\": \"" + password + "\"," +
                "\"device_id\": \""  + deviceId + "\"," +
                "\"first_name\": \"" + firstName + "\"," +
                "\"last_name\": \"" + lastName + "\"," +
                "\"gender\": \"" + gender + "\"," +
                "\"birthday\": \"" + birthday + "\"," +
                "\"address\": {" +
                "\"lat\": \"" + lat + "\"," +
                "\"lon\": \"" + lon + "\"" +
                "}," +
                "\"city\": \"" + "BORRAME" + "\"}"; // TODO smpiano borrame!
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        Register register = null;
        try {
            register = (Register) restClient.post("/users/register", body, headers);
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
        return register;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        Log.d("register json", json);
        JSONObject tokenJson = new JSONObject(json);
        return Register.fromJson(tokenJson);
    }

    @Override
    protected void onPostExecute(Register register) {
        super.onPostExecute(register);
        weakReference.get().onRegistrySuccess(register);
    }

    public interface ResultRegistry {
        public void onRegistrySuccess(Register register);
    }
}
