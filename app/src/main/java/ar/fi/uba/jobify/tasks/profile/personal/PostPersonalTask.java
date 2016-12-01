package ar.fi.uba.jobify.tasks.profile.personal;

import android.content.Context;

import org.json.JSONException;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class PostPersonalTask extends AbstractTask<String,Void,String,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public PostPersonalTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");

        String firstName = params[0];
        String lastName = params[1];
        String birthday = params[2];
        String gender = params[3];
        String lat = params[4];
        String lon = params[5];

        String body = "{"+
            "\"first_name\": \"" + firstName + "\"," +
            "\"last_name\": \"" + lastName + "\"," +
            "\"birthday\": \"" + birthday + "\"," +
            "\"gender\": \"" + gender + "\"," +
            "\"address\": {" +
                "\"lat\": \"" + lat + "\"," +
                "\"lon\": \"" + lon + "\"" +
            "}," +
            "\"city\": \"" + "BORRAME"+
        "\"}"; // TODO smpiano borrame!

        try {
            restClient.post("/users/"+helper.getProfessional().getEmail()+"/profile/personal?token="+token, body, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
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
    protected void onPostExecute(String personal) {
        super.onPostExecute(personal);
        if(personal != null){
            ((ProfileCreate) weakReference.get()).onProfilePersonalCreationSuccess();
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede crear perfil personal");
        }
    }

    public interface ProfileCreate {
        public void onProfilePersonalCreationSuccess();
    }

}
