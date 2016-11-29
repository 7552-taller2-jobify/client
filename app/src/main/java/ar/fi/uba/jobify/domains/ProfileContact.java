package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.utils.DateUtils;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfileContact {

    private String firstName;
    private String lastName;
    private String email;
    private String thumbnail;
    private Integer votes;

    public ProfileContact() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public static ProfileContact fromJson(JSONObject json) {
        ProfileContact professional = null;
        try {
            professional = new ProfileContact();
            professional.setFirstName(json.getString("first_name"));
            professional.setLastName(json.getString("last_name"));
            professional.setEmail(json.getString("email"));
            professional.setVotes(json.getInt("votes"));
            if (json.has("thumbnail")) {
                professional.setThumbnail(json.getString("thumbnail"));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing Professional.", e);
        }
        return professional;
    }
}
