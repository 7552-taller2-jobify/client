package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.utils.DateUtils;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfilePersonal {

    private String firstName;
    private String lastName;
    private String email;
    private Date birthday;
    private String gender;
    private String city;

    public ProfilePersonal() {
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

    public String getFullName() {
        if ((!this.lastName.isEmpty()) && (!this.firstName.isEmpty())) {
            return this.lastName + ", " + this.firstName;
        } else {
            if (!this.firstName.isEmpty()) {
                return this.firstName;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static ProfilePersonal fromJson(JSONObject json) {
        ProfilePersonal professional = null;
        try {
            professional = new ProfilePersonal();

            professional.setFirstName(json.getString("first_name"));
            professional.setLastName(json.getString("last_name"));
            professional.setEmail(json.getString("email"));
            if (json.has("birthday")) {
                professional.setBirthday(DateUtils.parseShortDateArg2(json.getString("birthday")));
            }
            professional.setGender(json.getString("gender"));
            if (json.has("city")) {
                professional.setCity(json.getString("city"));
            }

        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfilePersonal.", e);
        }
        return professional;
    }
}
