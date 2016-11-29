package ar.fi.uba.jobify.domains;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfileContactList {

    private List<ProfileContact> contacts;

    public ProfileContactList() {
        this.contacts = new ArrayList<ProfileContact>();
    }

    public List<ProfileContact> getContacts() {
        return contacts;
    }

    public static ProfileContactList fromJson(JSONObject json) {
        ProfileContactList contactList = null;
        try {
            contactList = new ProfileContactList();
            JSONArray skillArray = json.getJSONArray("friends");
            for (int i = 0; i < skillArray.length(); i++) {
                contactList.getContacts().add(ProfileContact.fromJson(skillArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileSkillList.", e);
        }
        return contactList;
    }
}
