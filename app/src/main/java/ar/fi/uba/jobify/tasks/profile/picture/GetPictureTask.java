package ar.fi.uba.jobify.tasks.profile.picture;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfilePicture;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class GetPictureTask extends AbstractTask<String,Void,ProfilePicture,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetPictureTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected ProfilePicture doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfilePicture picture = null;
        try {
            picture = (ProfilePicture) restClient.get("/users/"+email+"/profile/picture?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return picture;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfilePicture.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfilePicture picture) {
        super.onPostExecute(picture);
        if(picture != null){
            ((PerfilRead) weakReference.get()).onProfilePictureSuccess(picture);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener perfil picture");
        }
    }

    public interface PerfilRead {
        public void onProfilePictureSuccess(ProfilePicture picture);
    }

}
