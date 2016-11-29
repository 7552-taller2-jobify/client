package ar.fi.uba.jobify.tasks.profile.expertise;

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
public class DeleteExpertisePositionTask extends AbstractTask<String,Void,String,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public DeleteExpertisePositionTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String positionOlder = params[0];

        String uri = "/users/"+helper.getProfessional().getEmail()+"/profile/expertise/position/"+positionOlder+"?token="+token;

        try {
            restClient.delete(uri, null, withAuth(ctx));
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
    protected void onPostExecute(String expertiseList) {
        super.onPostExecute(expertiseList);
        if(expertiseList != null){
            ((PerfilRead) weakReference.get()).onProfileExpertisePositionDestroySuccess();
        } else {
            weakReference.get().showSnackbarSimpleMessage("No se puede eliminar perfil expertise position");
        }
    }

    public interface PerfilRead {
        public void onProfileExpertisePositionDestroySuccess();
    }

}
