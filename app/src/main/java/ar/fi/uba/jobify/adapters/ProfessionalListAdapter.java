package ar.fi.uba.jobify.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ar.fi.uba.jobify.domains.ProfessionalFriendsResult;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.GetContactPendingTask;
import ar.fi.uba.jobify.tasks.contact.GetMineContactListTask;
import ar.fi.uba.jobify.utils.CircleTransform;
import ar.fi.uba.jobify.utils.MyPreferences;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;

public class ProfessionalListAdapter extends ArrayAdapter<ProfessionalSearchItem>
        implements GetContactPendingTask.ContactAggregator,
        GetMineContactListTask.ProfessionalListAggregator {

    private static final int MY_FRIENDS = 0;
    private int request;
    private Location loc;
    private long total;
    private long offset;
    private boolean fetching;
    private Activity activity;
    private MyPreferences pref = new MyPreferences(getContext());
    private boolean defaultRequest = true;

    public ProfessionalListAdapter(Activity activity, Context context, int resource,
                                   List<ProfessionalSearchItem> professionals, int request) {
        super(context, resource, professionals);
        this.activity = activity;
        total=1;
        offset=0;
        fetching=false;
        this.request = request;
    }

    public Activity getActivity() {
        return activity;
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
            if (request == MY_FRIENDS) {
                GetMineContactListTask friends = new GetMineContactListTask(this);
                friends.execute();
            } else {
                GetContactPendingTask listProfessionals = new GetContactPendingTask(this);
                listProfessionals.execute();
            }
        }
    }

    @Override
    public void addFriends(ProfessionalFriendsResult professionalFriendsResult) {
        if(professionalFriendsResult !=null) {
            //this.clear();
            this.addAll(professionalFriendsResult.getFriends());
            this.offset = this.getCount();
            this.total = professionalFriendsResult.getFriends().size();
            fetching = false;
        }else{
            Log.w(this.getClass().getCanonicalName(), "Something when wrong getting friends.");
        }
    }

    @Override
    public void onContactPendingSuccess(ProfessionalFriendsResult professionalFriendsResult) {
        addFriends(professionalFriendsResult);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfessionalSearchItem professional = this.getItem(position);
        ViewHolder holder;
        if(position == this.getCount()-1){
            fetchMore();
        }

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_professional_item, null);

            holder = new ViewHolder();
            holder.professional_votes = (TextView) convertView.findViewById(R.id.client_row_professional_votes);
            holder.name = (TextView) convertView.findViewById(R.id.professional_row_name);
            holder.company = (TextView) convertView.findViewById(R.id.professional_row_company);
            holder.address = (TextView) convertView.findViewById(R.id.professional_row_address);
            holder.image = (ImageView) convertView.findViewById(R.id.professional_row_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.professional_votes.setText(isContentValid(professional.getVotes().toString()));
        holder.name.setText(isContentValid(professional.getLastName())+", "+isContentValid(professional.getName()));
        //holder.company.setText(isContentValid(professional.getCompany()));
        holder.company.setText(""); // TODO smpiano cargar el company.
        //holder.distance.setText(showCoolDistance(getContext(), professional.getDistance()));
        //holder.address.setText(isContentValid(professional.getAddress()));
        holder.address.setText(""); // TODO smpiano cargar el address.
        if (professional.getVotedByMe()) {
            convertView.findViewById(R.id.client_row_professional_heart).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.client_row_professional_heart_empty).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.client_row_professional_heart).setVisibility(View.GONE);
            convertView.findViewById(R.id.client_row_professional_heart_empty).setVisibility(View.VISIBLE);
        }
        if (!isContentValid(professional.getAvatar()).isEmpty()) {
            Picasso.with(this.getContext()).load(professional.getAvatar()).transform(new CircleTransform()).into(holder.image);
        } else if (!isContentValid(professional.getThumbnail()).isEmpty()) {
            Picasso.with(this.getContext()).load(professional.getThumbnail()).transform(new CircleTransform()).into(holder.image);
        } else {
            Picasso.with(this.getContext()).load(R.drawable.logo).transform(new CircleTransform()).into(holder.image);
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView name;
        public TextView address;
        public TextView company;
        public TextView professional_votes;
        public TextView distance;
        public ImageView image;
    }
}
