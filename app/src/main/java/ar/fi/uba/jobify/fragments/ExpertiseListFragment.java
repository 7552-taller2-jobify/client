package ar.fi.uba.jobify.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ar.fi.uba.jobify.activities.ProfileActivity;
import ar.fi.uba.jobify.adapters.ContactListAdapter;
import ar.fi.uba.jobify.adapters.ExpertiseListAdapter;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.domains.ProfileExpertise;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class ExpertiseListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ExpertiseListAdapter expertiseListAdapter;

    public ExpertiseListFragment() {
        super();
    }

    public ExpertiseListAdapter getAdapter() {
        return expertiseListAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflo la vista de listado de elementos
        View fragmentView = inflater.inflate(R.layout.fragment_expertise_list, container, false);
        ListView contactsList = (ListView) fragmentView.findViewById(R.id.expertiseListView);

        //Defino el adapter
        expertiseListAdapter = new ExpertiseListAdapter(
                getActivity(),
                getContext(),
                R.layout.list_expertise_item,
                new ArrayList<ProfileExpertise>());
        //Asocio la listView con el adapter
        contactsList.setAdapter(expertiseListAdapter);
        contactsList.setOnItemClickListener(this);

        // Si tarda mucho debería mostrar una barra de progreso
        ProgressBar bar= new ProgressBar(getContext());
        bar.setIndeterminate(true);
        contactsList.setEmptyView(bar);
        expertiseListAdapter.refresh();
        return fragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Cuando selecciono un item
        ProfileExpertise profileExpertise = (ProfileExpertise)parent.getItemAtPosition(position);

    }
}
