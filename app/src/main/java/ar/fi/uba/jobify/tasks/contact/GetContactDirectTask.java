package ar.fi.uba.jobify.tasks.contact;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.domains.Contact;
import ar.fi.uba.jobify.tasks.AbstractTask;
import ar.fi.uba.jobify.utils.ShowMessage;

/**
 * Created by smpiano on 9/28/16.
 */

public class GetContactDirectTask extends AbstractTask<String,Void,Contact,AppCompatActivity> {

    public GetContactDirectTask(AppCompatActivity validation) {
        super(validation);
    }

    public Contact getClient(String clientId) {
        Context ctx = weakReference.get().getApplicationContext();
        Contact contact = null;
        try {
            contact = (Contact) restClient.get("/v1/clients/"+clientId, withAuth(ctx));
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
    protected Contact doInBackground(String... strings) {
        if (strings.length == 1) {
            return this.getClient(strings[0]); // paso el idClient
        }
        return null;
    }

    @Override
    protected void onPostExecute(Contact contact) {
        if(contact != null){
            ((ClientDirectReceiver) weakReference.get()).updateClientDirect(contact);
        }
    }

    public interface ClientDirectReceiver {
        public void updateClientDirect(Contact contact);
    }
}
