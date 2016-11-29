package ar.fi.uba.jobify.tasks.profile.skills;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfileSkillList;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class PostSkillsTask extends AbstractTask<String,Void,String,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public PostSkillsTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String skills = params[0];
        String category = params[1];
        String body = "{\"every_skill\":{"+
                "\"skills\": \"" + skills + "\"," +
                "\"category\": \"" + category +
        "\"}}";

        try {
            restClient.post("/users/"+helper.getProfessional().getEmail()+"/profile/skills?token="+token, body, withAuth(ctx));
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
    protected void onPostExecute(String skillList) {
        super.onPostExecute(skillList);
        if(skillList != null){
            ((PerfilRead) weakReference.get()).onProfileSkillCreationSuccess();
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede crear perfil skills");
        }
    }

    public interface PerfilRead {
        public void onProfileSkillCreationSuccess();
    }

}
