package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.utils.FieldValidator;

/**
 * Created by smpiano on 9/28/16.
 */
public class Professional {



    /*
    *  "first_name": string,
    "last_name": string,
    "email": string,
    "birthday": string,
    "address": {
      "lat": number,
      "lon": number
    }
    * */
    private long id;
    private String name;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String avatar;
    private Date birthday;
    private Date dateCreated;
    private Date lastModified;

    public Professional(long id) {
        this.id= id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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

    public static Professional fromJson(JSONObject json) {
        Professional professional = null;
        try {
            professional = new Professional(json.getLong("id"));

            professional.setName(json.getString("first_name"));
            professional.setLastName(json.getString("last_name"));

            professional.setEmail(json.getString("email"));
            if (json.has("avatar")) {
                professional.setAvatar(json.getString("avatar"));
            }

            if (json.has("phone_number")) {
                professional.setPhoneNumber(json.getString("phone_number"));
            }


        } catch (JSONException e) {
            throw new BusinessException("Error parsing Professional.", e);
        }
        return professional;
    }
}
