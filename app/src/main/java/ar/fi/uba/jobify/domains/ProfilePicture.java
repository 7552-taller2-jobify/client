package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfilePicture {

    private String picture;

    public ProfilePicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public static ProfilePicture fromJson(JSONObject json) {
        ProfilePicture summary = null;
        try {
            summary = new ProfilePicture(json.getString("picture"));
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfilePicture.", e);
        }
        return summary;
    }
}
