package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfileContactList;
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
public class GetContactsTask extends AbstractTask<String,Void,ProfileContactList,ProfileActivity> {

    private final MyPreferenceHelper helper;
    private final MyPreferences pref;

    public GetContactsTask(ProfileActivity activity) {
        super(activity);
        helper = new MyPreferenceHelper(activity.getApplicationContext());
        pref = new MyPreferences(activity.getApplicationContext());
    }

    @Override
    protected ProfileContactList doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String token = pref.get(ctx.getString(R.string.shared_pref_current_token),"");
        String email = params[0];
        ProfileContactList contactList = null;
        try {
            contactList = (ProfileContactList) restClient.get("/users/"+email+"/profile/contacts?token="+token, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (final Exception e) {
            weakReference.get().runOnUiThread(new Runnable() {
                public void run() {
                    ShowMessage.toastMessage(weakReference.get().getApplicationContext(), e.getMessage());
                }
            });
        }
        return contactList;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        return ProfileContactList.fromJson(new JSONObject(json));
    }

    @Override
    protected void onPostExecute(ProfileContactList contactList) {
        super.onPostExecute(contactList);
        if(contactList != null){
            ((PerfilRead) weakReference.get()).onProfileContactListSuccess(contactList);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener perfil contacts");
        }
    }

    public interface PerfilRead {
        public void onProfileContactListSuccess(ProfileContactList contactList);
    }

}
