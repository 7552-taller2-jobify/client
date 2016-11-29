package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfileSummary {

    private String summary;

    public ProfileSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public static ProfileSummary fromJson(JSONObject json) {
        ProfileSummary summary = null;
        try {
            summary = new ProfileSummary(json.getString("summary"));
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileSummary.", e);
        }
        return summary;
    }
}
