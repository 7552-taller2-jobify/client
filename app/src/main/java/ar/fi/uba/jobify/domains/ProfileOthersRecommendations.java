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
public class ProfileOthersRecommendations {

    private List<String> recommendations;

    public ProfileOthersRecommendations() {
        this.recommendations = new ArrayList<String>();
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public static ProfileOthersRecommendations fromJson(JSONObject json) {
        ProfileOthersRecommendations profileOthersRecomendations = null;
        try {
            profileOthersRecomendations = new ProfileOthersRecommendations();
            JSONArray ownRecommendations = json.getJSONArray("others_recommendations");
            for (int i = 0; i < ownRecommendations.length(); i++) {
                profileOthersRecomendations.getRecommendations().add(ownRecommendations.getString(i));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileOthersRecommendations.", e);
        }
        return profileOthersRecomendations;
    }
}
