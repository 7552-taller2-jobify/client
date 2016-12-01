package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class ForgotPassword {
    private String pass;

    public ForgotPassword(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public static ForgotPassword fromJson(JSONObject json) {
        try {
            ForgotPassword forgotPassword = new ForgotPassword(json.getString("password"));
            return forgotPassword;
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ForgotPassword.", e);
        }
    }
}
