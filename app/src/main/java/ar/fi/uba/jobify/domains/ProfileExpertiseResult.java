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
public class ProfileExpertiseResult {
    List<ProfileExpertise> expertises;

    public ProfileExpertiseResult(){
        expertises = new ArrayList<ProfileExpertise>();
    }

    public void addProfessional(ProfileExpertise professional){
        expertises.add(professional);
    }

    public List<ProfileExpertise> getExpertises() {
        return expertises;
    }

    public static ProfileExpertiseResult fromJson(JSONObject json) {
        ProfileExpertiseResult profileContactsResult = null;
        try {
            profileContactsResult = new ProfileExpertiseResult();
            JSONArray resultJSON = (JSONArray) json.get("expertises");
            for (int i = 0; i < resultJSON.length(); i++) {
                profileContactsResult.addProfessional(ProfileExpertise.fromJson(resultJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileExpertiseResult",e);
        }
        return profileContactsResult;
    }
}
