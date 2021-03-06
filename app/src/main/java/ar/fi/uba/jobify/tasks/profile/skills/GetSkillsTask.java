package ar.fi.uba.jobify.tasks.profile.skills;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.adapters.SkillListAdapter;
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
public class GetSkillsTask extends AbstractTask<String,Void,ProfileSkillList,SkillListAdapter> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetSkillsTask(SkillListAdapter adapter) {
        super(adapter);
        helper = new MyPreferenceHelper(adapter.getActivity().getApplicationContext());
        pref = new MyPreferences(adapter.getActivity().getApplicationContext());
    }

    @Override
    protected ProfileSkillList doInBackground(String... params) {
        Context ctx = weakReference.get().getActivity().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfileSkillList skillList = null;
        try {
            skillList = (ProfileSkillList) restClient.get("/users/"+email+"/profile/skills?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().getActivity().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getActivity().getApplicationContext(), e.getMessage());
                }
            });
        }
        return skillList;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfileSkillList.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfileSkillList skillList) {
        super.onPostExecute(skillList);
        if(skillList != null){
            ((ProfileRead) weakReference.get()).onProfileSkillSuccess(skillList);
        } else{
            weakReference.get().getActivity().showSnackbarSimpleMessage("No se puede obtener perfil skills");
        }
    }

    public interface ProfileRead {
        public void onProfileSkillSuccess(ProfileSkillList skillList);
    }

}
