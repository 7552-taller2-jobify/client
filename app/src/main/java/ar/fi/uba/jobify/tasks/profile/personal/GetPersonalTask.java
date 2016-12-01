package ar.fi.uba.jobify.tasks.profile.personal;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfilePersonal;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class GetPersonalTask extends AbstractTask<String,Void,ProfilePersonal,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetPersonalTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected ProfilePersonal doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfilePersonal personal = null;
        try {
            personal = (ProfilePersonal) restClient.get("/users/"+email+"/profile/personal?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return personal;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfilePersonal.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfilePersonal personal) {
        super.onPostExecute(personal);
        if(personal != null){
            ((ProfileRead) weakReference.get()).onProfilePersonalSuccess(personal);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener perfil personal");
        }
    }

    public interface ProfileRead {
        public void onProfilePersonalSuccess(ProfilePersonal personal);
    }

}
