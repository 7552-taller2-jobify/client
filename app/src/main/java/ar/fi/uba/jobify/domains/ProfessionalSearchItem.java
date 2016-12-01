package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.utils.DateUtils;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfessionalSearchItem {

    private String name;
    private String lastName;
    private String email;
    private String avatar;
    private String thumbnail;
    private Date birthday;
    private String distance;
    private Integer votes;

    public ProfessionalSearchItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullName() {
        if ((!this.lastName.isEmpty()) && (!this.name.isEmpty())) {
            return this.lastName +", "+ this.name;
        } else {
            if (!this.name.isEmpty()){
                return this.name;
            } else {
                return this.lastName;
            }
        }
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public static ProfessionalSearchItem fromJson(JSONObject json) {
        ProfessionalSearchItem professional = null;
        try {
            professional = new ProfessionalSearchItem();

            professional.setName(json.getString("first_name"));
            professional.setLastName(json.getString("last_name"));

            professional.setEmail(json.getString("email"));
            if (json.has("avatar")) {
                professional.setAvatar(json.getString("picture"));
            }
            if (json.has("thumbnail")) {
                professional.setThumbnail(json.getString("thumbnail"));
            }
            if (json.has("birthday")) {
                professional.setBirthday(DateUtils.parseShortDateArg2(json.getString("birthday")));
            }
            if (json.has("distance")) {
                professional.setDistance(json.getString("distance"));
            }
            if (json.has("votes")) {
                professional.setVotes(json.getInt("votes"));
            }

        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfessionalSearchItem.", e);
        }
        return professional;
    }
}
