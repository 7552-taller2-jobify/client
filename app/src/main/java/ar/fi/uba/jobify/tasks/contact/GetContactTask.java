package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.activities.ContactActivity;
import ar.fi.uba.jobify.domains.Contact;
import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.ShowMessage;

/**
 * Created by smpiano on 9/28/16.
 */
public class GetContactTask extends AbstractTask<String,Void,Contact,ContactActivity> {

    public GetContactTask(ContactActivity activity) {
        super(activity);
    }

    @Override
    protected Contact doInBackground(String... params) {
        Context ctx = weakReference.get().getApplicationContext();
        String clientId = params[0];
        Contact contact = null;
        try {
            contact = (Contact) restClient.get("/v1/clients/"+clientId, withAuth(ctx));
        } catch (BusinessException e) {
            weakReference.get().showSnackbarSimpleMessage(e.getMessage());
        } catch (Exception e) {
            ShowMessage.toastMessage(ctx, e.getMessage());
        }
        return contact;
    }

    @Override
    public Object readResponse(String json) throws JSONException {
        JSONObject clientJson = new JSONObject(json);
        return Contact.fromJson(clientJson);
    }

    @Override
    protected void onPostExecute(Contact contact) {
        super.onPostExecute(contact);
        if(contact != null){
            ((ClientReceiver) weakReference.get()).updateClientInformation(contact);
        } else{
            weakReference.get().showSnackbarSimpleMessage("No se puede obtener info del cliente");
        }
    }

    public interface ClientReceiver {
        public void updateClientInformation(Contact contact);
    }

}
