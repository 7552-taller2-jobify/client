package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfileExpertise {

    private String company;
    private String position;
    private String from;
    private String to;
    private String expertise;
    private String category;

    public ProfileExpertise(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static ProfileExpertise fromJson(JSONObject json) {
        ProfileExpertise expertise = null;
        try {
            expertise = new ProfileExpertise(json.getString("company"));
            expertise.setPosition(json.getString("position"));
            expertise.setFrom(json.getString("from"));
            expertise.setTo(json.getString("to"));
            expertise.setExpertise(json.getString("expertise"));
            expertise.setCategory(json.getString("category"));
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileExpertise.", e);
        }
        return expertise;
    }
}
