package ar.fi.uba.jobify.tasks.profile.picture;

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
public class PutPictureTask extends AbstractTask<String,Void,String,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public PutPictureTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String picture = params[0];

        String body = "{"+
                "\"picture\": \"" + picture +
        "\"}";
        try {
            restClient.put("/users/"+helper.getProfessional().getEmail()+"/profile/picture?token="+token, body, withAuth(ctx));
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
    protected void onPostExecute(String picture) {
        super.onPostExecute(picture);
        if(picture != null){
            ((ProfileEdit) weakReference.get()).onProfilePictureModificationSuccess();
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede modificar el perfil picture");
        }
    }

    public interface ProfileEdit {
        public void onProfilePictureModificationSuccess();
    }

}
