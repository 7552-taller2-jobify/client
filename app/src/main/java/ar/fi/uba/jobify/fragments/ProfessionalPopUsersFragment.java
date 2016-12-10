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

import ar.fi.uba.jobify.activities.PopUsersActivity;
import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.activities.SearchActivity;
import ar.fi.uba.jobify.adapters.PopProfessionalListAdapter;
import ar.fi.uba.jobify.adapters.ProfessionalSearchAdapter;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.PostContactRequestTask;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfessionalPopUsersFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ProfessionalSearchItem professionalSelected;
    private PopUsersActivity act;

    public ProfessionalPopUsersFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        act = (PopUsersActivity) getActivity();

        //inflo la vista de listado de elementos
        View fragmentView = inflater.inflate(R.layout.fragment_professional_pop_users, container, false);
        ListView professionalList = (ListView) fragmentView.findViewById(R.id.professional_pop_users_view);

        //Defino el adapter
        PopProfessionalListAdapter popProfessionalListAdapter = new PopProfessionalListAdapter(
                getActivity(),
                getContext(),
                R.layout.list_professional_item,
                new ArrayList<ProfessionalSearchItem>());
        //Asocio la listView con el adapter
        professionalList.setAdapter(popProfessionalListAdapter);
        professionalList.setOnItemClickListener(this);

        // Si tarda mucho debería mostrar una barra de progreso
        ProgressBar bar= new ProgressBar(getContext());
        bar.setIndeterminate(true);
        professionalList.setEmptyView(bar);
        popProfessionalListAdapter.refresh();
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

}
