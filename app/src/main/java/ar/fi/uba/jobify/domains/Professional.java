package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.FieldValidator;

/**
 * Created by smpiano on 9/28/16.
 */
public class Professional {

    private String name;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String avatar;
    private String thumbnail;
    private Date birthday;

    public Professional() {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public static Professional fromJson(JSONObject json) {
        Professional professional = null;
        try {
            professional = new Professional();

            professional.setName(json.getString("first_name"));
            professional.setLastName(json.getString("last_name"));

            professional.setEmail(json.getString("email"));

            if (json.has("avatar")) {
                professional.setAvatar(json.getString("avatar"));
            } else if (json.has("picture")) {
                professional.setAvatar(json.getString("picture"));
            }

            if (json.has("thumbnail")) {
                professional.setThumbnail(json.getString("thumbnail"));
            }
            if (json.has("birthday")) {
                professional.setBirthday(DateUtils.parseShortDateArg2(json.getString("birthday")));
            }

        } catch (JSONException e) {
            throw new BusinessException("Error parsing Professional.", e);
        }
        return professional;
    }
}
