package ar.fi.uba.jobify.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfileSkill;
import ar.fi.uba.jobify.domains.ProfileSkillList;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.profile.skills.GetSkillsTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;

public class SkillListAdapter extends ArrayAdapter<ProfileSkill> implements GetSkillsTask.ProfileRead, Serializable {

    private final String professionalId;
    private long total;
    private long offset;
    private boolean fetching;
    private Activity activity;
    private final MyPreferences pref;
    private final MyPreferenceHelper helper;

    public SkillListAdapter(Activity activity, Context context, int resource,
                            List<ProfileSkill> profileSkills, String professionalId) {
        super(context, resource, profileSkills);
        this.activity = activity;
        total=1;
        offset=0;
        fetching=false;
        pref = new MyPreferences(getContext());
        helper = new MyPreferenceHelper(getContext());
        this.professionalId = professionalId;
    }

    public ProfileActivity getActivity() {
        return (ProfileActivity) activity;
    }

    public ProfileSkillList getSkills() {
        ProfileSkillList result = new ProfileSkillList();
        for(int i = 0; i < getCount(); i++) {
            result.getSkills().add(getItem(i));
        }
        return result;
    }

    public void refresh(){
        this.clear();
        offset=0;
        total=1;
        fetchMore();
    }

    public void fetchMore(){
        if(offset<total && !fetching){
            fetching=true;
            solveTask();
        }
    }

    private void solveTask() {
        if (RestClient.isOnline(getContext())) {
            (new GetSkillsTask(this)).execute(professionalId, String.valueOf(offset));
        }
    }

    @Override
    public void onProfileSkillSuccess(ProfileSkillList profileSkillList) {
        if(profileSkillList != null) {
            //this.clear();
            this.addAll(profileSkillList.getSkills());
            this.offset = this.getCount();
            this.total = profileSkillList.getSkills().size();
            fetching = false;

            if (total == 0) {
                getActivity().onSkillEmpty();
            } else {
                getActivity().onSkillNotEmpty();
            }
        }else{
            Log.w(this.getClass().getCanonicalName(), "Algo salio mal agregando las skills.");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileSkill profileSkill = this.getItem(position);

        // Pido mas datos cuando estoy por scolling el ultimo que veo. :S
        ViewHolder holder;
        if(position == this.getCount()-1){
            fetchMore();
        }

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_skill_item, null);

            holder = new ViewHolder();
            holder.category= (TextView) convertView.findViewById(R.id.skill_row_category);
            holder.skills = (TextView) convertView.findViewById(R.id.skill_row_skills);
            convertView.setTag(holder);

            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.category.setText(isContentValid(profileSkill.getCategory()));
        holder.skills.setText(isContentValid(android.text.TextUtils.join(", ", profileSkill.getSkills())));

        return convertView;
    }

    private static class ViewHolder {
        public TextView category;
        public TextView skills;
    }

    public interface SkillRead {
        public void onSkillEmpty();
        public void onSkillNotEmpty();
    }
}
