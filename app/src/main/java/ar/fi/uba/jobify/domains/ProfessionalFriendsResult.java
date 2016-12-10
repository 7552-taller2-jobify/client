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
public class ProfessionalFriendsResult {
    List<ProfessionalSearchItem> friends;

    public ProfessionalFriendsResult(){
        friends = new ArrayList<ProfessionalSearchItem>();
    }

    public void addProfessional(ProfessionalSearchItem professional){
        friends.add(professional);
    }

    public List<ProfessionalSearchItem> getFriends() {
        return friends;
    }

    public static ProfessionalFriendsResult fromJson(JSONObject json) {
        ProfessionalFriendsResult professionalSearchResult = null;
        try {
            professionalSearchResult = new ProfessionalFriendsResult();
            JSONArray resultJSON = (JSONArray) json.get(json.has("friends") ? "friends" : "solicitudes");
            for (int i = 0; i < resultJSON.length(); i++) {
                professionalSearchResult.addProfessional(ProfessionalSearchItem.fromJson(resultJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfessionalFriendsResult",e);
        }
        return professionalSearchResult;
    }
}
