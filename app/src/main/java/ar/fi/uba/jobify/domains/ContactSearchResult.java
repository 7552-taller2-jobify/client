package ar.fi.uba.jobify.domains;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 10/2/16.
 */
public class ContactSearchResult {
    long offset;
    long total;
    List<Contact> contacts;

    public ContactSearchResult(){
        offset=0;
        total=0;
        contacts = new ArrayList<>();
    }

    public void addClient(Contact contact){
        contacts.add(contact);
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public long getOffset() {
        return offset;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public static ContactSearchResult fromJson(JSONObject json) {
        ContactSearchResult contactSearchResult = null;
        try {
            contactSearchResult = new ContactSearchResult();
            JSONObject pagingJSON = json.getJSONObject("paging");
            contactSearchResult.setTotal(pagingJSON.getLong("total"));
            contactSearchResult.setOffset(pagingJSON.getLong("offset"));
            JSONArray resultJSON = (JSONArray) json.get("results");
            for (int i = 0; i < resultJSON.length(); i++) {
                contactSearchResult.addClient(Contact.fromJson(resultJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ContactSearchResult",e);
        }
        return contactSearchResult;
    }
}
