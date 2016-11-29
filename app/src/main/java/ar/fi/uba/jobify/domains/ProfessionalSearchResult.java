package ar.fi.uba.jobify.domains;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ar.fi.uba.jobify.exceptions.BusinessException;

/**
 * Created by smpiano on 10/2/16.
 */
public class ProfessionalSearchResult {
    long offset;
    long total;
    List<ProfessionalSearchItem> professionals;

    public ProfessionalSearchResult(){
        offset=0;
        total=0;
        professionals = new ArrayList<ProfessionalSearchItem>();
    }

    public void addProfessional(ProfessionalSearchItem professional){
        professionals.add(professional);
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public long getOffset() {
        return offset;
    }

    public List<ProfessionalSearchItem> getProfessionals() {
        return professionals;
    }

    public static ProfessionalSearchResult fromJson(JSONObject json) {
        ProfessionalSearchResult professionalSearchResult = null;
        try {
            professionalSearchResult = new ProfessionalSearchResult();
            JSONObject pagingJSON = json.getJSONObject("paging");
            professionalSearchResult.setTotal(pagingJSON.getLong("total"));
            professionalSearchResult.setOffset(pagingJSON.getLong("offset"));
            JSONArray resultJSON = (JSONArray) json.get("results");
            for (int i = 0; i < resultJSON.length(); i++) {
                professionalSearchResult.addProfessional(ProfessionalSearchItem.fromJson(resultJSON.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new BusinessException("Error parsing ProfessionalSearchResult",e);
        }
        return professionalSearchResult;
    }
}
