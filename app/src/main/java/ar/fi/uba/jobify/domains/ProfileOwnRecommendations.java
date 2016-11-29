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
public class ProfileOwnRecommendations {

    private List<String> recommendations;

    public ProfileOwnRecommendations() {
        this.recommendations = new ArrayList<String>();
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public static ProfileOwnRecommendations fromJson(JSONObject json) {
        ProfileOwnRecommendations profileOwnRecomendations = null;
        try {
            profileOwnRecomendations = new ProfileOwnRecommendations();
            JSONArray ownRecommendations = json.getJSONArray("own_recommendations");
            for (int i = 0; i < ownRecommendations.length(); i++) {
                profileOwnRecomendations.getRecommendations().add(ownRecommendations.getString(i));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileOwnRecommendations.", e);
        }
        return profileOwnRecomendations;
    }
}
