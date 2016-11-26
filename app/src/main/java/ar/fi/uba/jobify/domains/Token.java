package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class Token {
    private String token;
    private Professional professional;

    public Token(String token) {
        this.token = token;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public String getToken() {
        return token;
    }

    public static Token fromJson(JSONObject json) {
        try {
            Token token = new Token(json.getJSONObject("metadata").getString("token"));
            token.setProfessional(Professional.fromJson(json.getJSONObject("profile")));
            return token;
        } catch (JSONException e) {
            throw new BusinessException("Error parsing Token.", e);
        }
    }
}
