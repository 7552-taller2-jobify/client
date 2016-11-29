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
public class ProfileExpertiseList {

    private List<ProfileExpertise> expertises;

    public ProfileExpertiseList() {
        expertises = new ArrayList<ProfileExpertise>();
    }

    public List<ProfileExpertise> getExpertises() {
        return expertises;
    }

    public static ProfileExpertiseList fromJson(JSONObject json) {
        ProfileExpertiseList expertiseList;
        try {
            expertiseList = new ProfileExpertiseList();
            JSONArray array = json.getJSONArray("expertises");
            for (int i = 0; i < array.length(); i++) {
                expertiseList.getExpertises().add(ProfileExpertise.fromJson(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileExpertiseList.", e);
        }
        return expertiseList;
    }
}
