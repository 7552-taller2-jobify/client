package ar.fi.uba.jobify.tasks.profile.recommendations;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfileOthersRecommendations;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class GetOthersRecommendationsTask extends AbstractTask<String,Void,ProfileOthersRecommendations,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetOthersRecommendationsTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected ProfileOthersRecommendations doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfileOthersRecommendations profileOthersRecomendations = null;
        try {
            profileOthersRecomendations = (ProfileOthersRecommendations) restClient.get("/users/"+email+"/profile/others_recommendations?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return profileOthersRecomendations;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfileOthersRecommendations.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfileOthersRecommendations profileOthersRecomendations) {
        super.onPostExecute(profileOthersRecomendations);
        if(profileOthersRecomendations != null){
            ((ProfileRead) weakReference.get()).onProfileOthersRecomendationsSuccess(profileOthersRecomendations);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener perfil others_recommendations");
        }
    }

    public interface ProfileRead {
        public void onProfileOthersRecomendationsSuccess(ProfileOthersRecommendations profileOthersRecomendations);
    }

}
