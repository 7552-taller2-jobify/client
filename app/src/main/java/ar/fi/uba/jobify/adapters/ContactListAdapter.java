package ar.fi.uba.jobify.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.domains.ProfileContactsResult;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.GetMineContactListTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.CircleTransform;
import ar.fi.uba.jobify.utils.LocationHelper;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;

public class ContactListAdapter extends ArrayAdapter<ProfessionalSearchItem> implements GetMineContactListTask.ClientsListAggregator {

    private LocationHelper locHelper;
    private long total;
    private long offset;
    private boolean fetching;
    private Activity activity;
    private final MyPreferences pref;
    private final MyPreferenceHelper helper;

    public ContactListAdapter(Activity activity, Context context, int resource,
                              List<ProfessionalSearchItem> contacts) {
        super(context, resource, contacts);
        this.activity = activity;
        total=1;
        offset=0;
        fetching=false;
        LocationHelper.updatePosition(this.getContext());
        pref = new MyPreferences(getContext());
        helper = new MyPreferenceHelper(getContext());
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
            GetMineContactListTask contactList = new GetMineContactListTask(ContactListAdapter.this);
            String lat = pref.get(getContext().getString(R.string.shared_pref_current_location_lat), AppSettings.getGpsLat());
            String lon = pref.get(getContext().getString(R.string.shared_pref_current_location_lon), AppSettings.getGpsLon());
            contactList.execute(helper.getProfessional().getEmail(), String.valueOf(offset), lat, lon);
        }
    }

    @Override
    public void addClients(ProfileContactsResult profileContactsResult) {
        if(profileContactsResult != null) {
            //this.clear();
            this.addAll(profileContactsResult.getProfessionals());
            this.offset = this.getCount();
            this.total = profileContactsResult.getProfessionals().size();
            fetching = false;

            if (total == 0) {
                Context ctx = getActivity().getApplicationContext();
                ShowMessage.toastMessage(ctx, ctx.getString(R.string.zero_contacts));
            }
        }else{
            Log.w(this.getClass().getCanonicalName(), "Algo salio mal agregando contactos.");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfessionalSearchItem contact = this.getItem(position);
        ViewHolder holder;
        if(position==this.getCount()-1){
            fetchMore();
        }

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_professional_item, null);

            holder = new ViewHolder();
            holder.client_id = (TextView) convertView.findViewById(R.id.client_row_professional_id);
            holder.name = (TextView) convertView.findViewById(R.id.professional_row_name);
            holder.company = (TextView) convertView.findViewById(R.id.professional_row_company);
            holder.distance = (TextView) convertView.findViewById(R.id.professional_row_client_distance);
            holder.address = (TextView) convertView.findViewById(R.id.professional_row_address);
            holder.image = (ImageView) convertView.findViewById(R.id.professional_row_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //holder.client_id.setText("# "+ isContentValid(Long.toString(contact.getId())));
        holder.client_id.setText(""); //TODO smpiano arreglar
        holder.name.setText(isContentValid(contact.getLastName())+", "+isContentValid(contact.getName()));
        //holder.company.setText(isContentValid(contact.getCompany()));
        holder.company.setText("");
        //holder.distance.setText(showCoolDistance(getContext(), contact.getDistance()));
        holder.distance.setText("");
        //holder.address.setText(isContentValid(contact.getAddress()));
        holder.address.setText("");
        if (isContentValid(contact.getThumbnail()).isEmpty()) {
            Picasso.with(this.getContext()).load(R.drawable.logo).transform(new CircleTransform()).into(holder.image);
        } else {
            Picasso.with(this.getContext()).load(contact.getThumbnail()).transform(new CircleTransform()).into(holder.image);
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView name;
        public TextView address;
        public TextView company;
        public TextView client_id;
        public TextView distance;
        public ImageView image;
    }
}
