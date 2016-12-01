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
public class ProfileContactsResult {
    List<ProfessionalSearchItem> professionals;

    public ProfileContactsResult(){
        professionals = new ArrayList<ProfessionalSearchItem>();
    }

    public void addProfessional(ProfessionalSearchItem professional){
        professionals.add(professional);
    }

    public List<ProfessionalSearchItem> getProfessionals() {
        return professionals;
    }

    public static ProfileContactsResult fromJson(JSONObject json) {
        ProfileContactsResult profileContactsResult = null;
        try {
            profileContactsResult = new ProfileContactsResult();
            JSONArray resultJSON = (JSONArray) json.get("friends");
            for (int i = 0; i < resultJSON.length(); i++) {
                profileContactsResult.addProfessional(ProfessionalSearchItem.fromJson(resultJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileContactsResult",e);
        }
        return profileContactsResult;
    }
}
