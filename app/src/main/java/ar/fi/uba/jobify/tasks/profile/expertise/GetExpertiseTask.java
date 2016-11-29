package ar.fi.uba.jobify.tasks.profile.expertise;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfileExpertiseList;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class GetExpertiseTask extends AbstractTask<String,Void,ProfileExpertiseList,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetExpertiseTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected ProfileExpertiseList doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfileExpertiseList expertiseList = null;
        try {
            expertiseList = (ProfileExpertiseList) restClient.get("/users/"+email+"/profile/expertise?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return expertiseList;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfileExpertiseList.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfileExpertiseList expertiseList) {
        super.onPostExecute(expertiseList);
        if(expertiseList != null){
            ((PerfilRead) weakReference.get()).onProfileExpertiseSuccess(expertiseList);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener perfil expertise");
        }
    }

    public interface PerfilRead {
        public void onProfileExpertiseSuccess(ProfileExpertiseList expertiseList);
    }

}
