package ar.fi.uba.jobify.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ar.fi.uba.jobify.adapters.SkillListAdapter;
import ar.fi.uba.jobify.domains.ProfileSkill;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class SkillListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public SkillListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflo la vista de listado de elementos
        View fragmentView = inflater.inflate(R.layout.fragment_skill_list, container, false);
        ListView contactsList = (ListView) fragmentView.findViewById(R.id.skillListView);

        //Defino el adapter
        SkillListAdapter skillListAdapter = new SkillListAdapter(
                getActivity(),
                getContext(),
                R.layout.list_skill_item,
                new ArrayList<ProfileSkill>());
        //Asocio la listView con el adapter
        contactsList.setAdapter(skillListAdapter);
        contactsList.setOnItemClickListener(this);

        // Si tarda mucho debería mostrar una barra de progreso
        ProgressBar bar= new ProgressBar(getContext());
        bar.setIndeterminate(true);
        contactsList.setEmptyView(bar);
        skillListAdapter.refresh();
        return fragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Cuando selecciono un item
        ProfileSkill profileSkill = (ProfileSkill)parent.getItemAtPosition(position);

    }
}
