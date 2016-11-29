package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 10/3/16.
 */
public class Register {
    private String registration;

    public Register(String registration) {
        this.registration = registration;
    }

    public static Register fromJson(JSONObject json) {
        try {
            return new Register(json.getString("registration"));
        } catch (JSONException e) {
            throw new BusinessException("Error parsing Register.", e);
        }
    }
}
