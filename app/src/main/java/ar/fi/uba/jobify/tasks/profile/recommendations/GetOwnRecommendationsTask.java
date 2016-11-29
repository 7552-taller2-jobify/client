package ar.fi.uba.jobify.tasks.profile.recommendations;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfileOwnRecommendations;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class GetOwnRecommendationsTask extends AbstractTask<String,Void,ProfileOwnRecommendations,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetOwnRecommendationsTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected ProfileOwnRecommendations doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfileOwnRecommendations profileOwnRecomendations = null;
        try {
            profileOwnRecomendations = (ProfileOwnRecommendations) restClient.get("/users/"+email+"/profile/own_recommendations?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return profileOwnRecomendations;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfileOwnRecommendations.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfileOwnRecommendations profileOwnRecomendations) {
        super.onPostExecute(profileOwnRecomendations);
        if(profileOwnRecomendations != null){
            ((PerfilRead) weakReference.get()).onProfileOwnRecomendationsSuccess(profileOwnRecomendations);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener perfil own_recommendations");
        }
    }

    public interface PerfilRead {
        public void onProfileOwnRecomendationsSuccess(ProfileOwnRecommendations profileOwnRecomendations);
    }

}
