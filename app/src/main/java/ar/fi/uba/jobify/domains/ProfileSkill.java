package ar.fi.uba.jobify.domains;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfileSkill {

    private List<String> skills;
    private String category;

    public ProfileSkill() {
        this.skills = new ArrayList<String>();
    }

    public List<String> getSkills() {
        return skills;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static ProfileSkill fromJson(JSONObject json) {
        ProfileSkill skill = null;
        try {
            skill = new ProfileSkill();
            JSONArray skills = json.getJSONArray("skills");
            for (int i = 0; i < skills.length(); i++) {
                skill.getSkills().add(skills.getString(i));
            }
            skill.setCategory(json.getString("category"));
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileSkill.", e);
        }
        return skill;
    }
}
