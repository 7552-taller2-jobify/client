package ar.fi.uba.jobify.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.domains.ProfileContactsResult;
import ar.fi.uba.jobify.domains.ProfileExpertise;
import ar.fi.uba.jobify.domains.ProfileExpertiseList;
import ar.fi.uba.jobify.domains.ProfileExpertiseResult;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.GetMineContactListTask;
import ar.fi.uba.jobify.tasks.profile.expertise.GetExpertiseTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.CircleTransform;
import ar.fi.uba.jobify.utils.LocationHelper;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;

public class ExpertiseListAdapter extends ArrayAdapter<ProfileExpertise> implements GetExpertiseTask.ProfileRead, Serializable {

    private long total;
    private long offset;
    private boolean fetching;
    private Activity activity;
    private final MyPreferences pref;
    private final MyPreferenceHelper helper;

    public ExpertiseListAdapter(Activity activity, Context context, int resource,
                                List<ProfileExpertise> expertises) {
        super(context, resource, expertises);
        this.activity = activity;
        total=1;
        offset=0;
        fetching=false;
        pref = new MyPreferences(getContext());
        helper = new MyPreferenceHelper(getContext());
    }

    public ProfileActivity getActivity() {
        return (ProfileActivity) activity;
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
            (new GetExpertiseTask(this)).execute(helper.getProfessional().getEmail(), String.valueOf(offset));
        }
    }

    @Override
    public void onProfileExpertiseSuccess(ProfileExpertiseList profileExpertiseResult) {
        if(profileExpertiseResult != null) {
            //this.clear();
            this.addAll(profileExpertiseResult.getExpertises());
            this.offset = this.getCount();
            this.total = profileExpertiseResult.getExpertises().size();
            fetching = false;

            if (total == 0) {
                getActivity().onExpertisesEmpty();
            }
        }else{
            Log.w(this.getClass().getCanonicalName(), "Algo salio mal agregando las experiencias.");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileExpertise expertise = this.getItem(position);

        // Pido mas datos cuando estoy por scolling el ultimo que veo. :S
        ViewHolder holder;
        if(position == this.getCount()-1){
            fetchMore();
        }

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_expertise_item, null);

            holder = new ViewHolder();
            holder.company = (TextView) convertView.findViewById(R.id.expertise_row_company);
            holder.position = (TextView) convertView.findViewById(R.id.expertise_row_position);
            holder.from = (TextView) convertView.findViewById(R.id.expertise_row_from);
            holder.to = (TextView) convertView.findViewById(R.id.expertise_row_to);
            holder.expertise = (TextView) convertView.findViewById(R.id.expertise_row_expertise);
            holder.category = (TextView) convertView.findViewById(R.id.expertise_row_category);
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

        holder.company.setText(isContentValid(expertise.getCompany()));
        holder.position.setText(isContentValid(expertise.getPosition()));
        holder.from.setText("|-> "+isContentValid(expertise.getFrom()));
        holder.to.setText("<-| "+isContentValid(expertise.getTo()));
        holder.expertise.setText(isContentValid(expertise.getExpertise()));
        holder.category.setText(isContentValid(expertise.getCategory()));

        return convertView;
    }

    private static class ViewHolder {
        public TextView company;
        public TextView position;
        public TextView from;
        public TextView to;
        public TextView expertise;
        public TextView category;
    }

    public interface ExpertisesRead {
        public void onExpertisesEmpty();
    }
}
