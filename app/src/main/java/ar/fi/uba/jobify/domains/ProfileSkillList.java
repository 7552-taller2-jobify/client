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
public class ProfileSkillList {

    private List<ProfileSkill> skills;

    public ProfileSkillList() {
        this.skills = new ArrayList<ProfileSkill>();
    }

    public List<ProfileSkill> getSkills() {
        return skills;
    }

    public static ProfileSkillList fromJson(JSONObject json) {
        ProfileSkillList skillList = null;
        try {
            skillList = new ProfileSkillList();
            JSONArray skillArray = json.getJSONArray("every_skill");
            for (int i = 0; i < skillArray.length(); i++) {
                skillList.getSkills().add(ProfileSkill.fromJson(skillArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfileSkillList.", e);
        }
        return skillList;
    }
}
