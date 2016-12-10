package ar.fi.uba.jobify.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ar.fi.uba.jobify.activities.ChatActivity;
import ar.fi.uba.jobify.activities.MyContactsActivity;
import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.activities.SearchActivity;
import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.adapters.ProfessionalSearchAdapter;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.DeleteContactRejectTask;
import ar.fi.uba.jobify.tasks.contact.PostContactAcceptTask;
import ar.fi.uba.jobify.tasks.contact.PostContactRequestTask;
import ar.fi.uba.jobify.tasks.recomendation.DeleteVoteTask;
import ar.fi.uba.jobify.tasks.recomendation.PostVoteTask;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfessionalSearchFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ProfessionalSearchItem professionalSelected;
    private SearchActivity act;

    public ProfessionalSearchFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        act = (SearchActivity) getActivity();

        //inflo la vista de listado de elementos
        View fragmentView = inflater.inflate(R.layout.fragment_professional_search, container, false);
        ListView professionalList = (ListView) fragmentView.findViewById(R.id.professional_search_view);

        //desplegable
        registerForContextMenu(professionalList);

        //Defino el adapter
        ProfessionalSearchAdapter professionalSearchAdapter = new ProfessionalSearchAdapter(
                getActivity(),
                getContext(),
                R.layout.list_professional_item,
                new ArrayList<ProfessionalSearchItem>());
        //Asocio la listView con el adapter
        professionalList.setAdapter(professionalSearchAdapter);
        professionalList.setOnItemClickListener(this);

        // Si tarda mucho debería mostrar una barra de progreso
        ProgressBar bar= new ProgressBar(getContext());
        bar.setIndeterminate(true);
        professionalList.setEmptyView(bar);
        professionalSearchAdapter.refresh();
        return fragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Cuando selecciono un item
        ProfessionalSearchItem professionalSearchItem = (ProfessionalSearchItem)parent.getItemAtPosition(position);
        this.professionalSelected = professionalSearchItem;

        //Lo envío al perfil
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(Intent.EXTRA_UID, professionalSearchItem.getEmail());
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my_contacts_item_request:
                if (RestClient.isOnline(getContext())) {
                    new PostContactRequestTask( act ).execute(professionalSelected.getEmail());
                }
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        professionalSelected = (ProfessionalSearchItem) lv.getItemAtPosition(acmi.position);

        MenuInflater inflater = new MenuInflater(this.getContext());
        inflater.inflate(R.menu.menu_my_request_friends_item, menu);
    }
}
