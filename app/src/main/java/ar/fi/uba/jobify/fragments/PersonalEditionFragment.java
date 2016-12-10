package ar.fi.uba.jobify.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.Arrays;

import ar.fi.uba.jobify.domains.ProfileExpertise;
import ar.fi.uba.jobify.domains.ProfileSkill;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class PersonalEditionFragment extends DialogFragment implements AdapterView.OnItemClickListener,
        View.OnClickListener {

    private ArrayAdapter arrayAdapter;
    private Object current;
    private LinearLayout expertiseLayout;
    private LinearLayout skillLayout;
    private Button add;
    private Button exit;
    private EditText expertiseCompany;
    private EditText expertisePosition;
    private EditText expertiseFrom;
    private EditText expertiseTo;
    private EditText expertiseCategory;
    private EditText expertiseExpertise;
    private EditText skillCategory;
    private EditText skillSkills;
    private boolean expertiseLayoutFlag = false;
    private View fragmentView;
    private String professionalId;

    public PersonalEditionFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflo la vista de listado de elementos
        fragmentView = inflater.inflate(R.layout.fragment_personal_edition, container, false);
        ListView personalEditionList = (ListView) fragmentView.findViewById(R.id.personal_edition_listview);

        expertiseLayout = (LinearLayout) fragmentView.findViewById(R.id.personal_edition_expertise_item);
        skillLayout = (LinearLayout) fragmentView.findViewById(R.id.personal_edition_skill_item);

        expertiseCompany = (EditText) fragmentView.findViewById(R.id.personal_edition_expertise_item_company);
        expertisePosition = (EditText) fragmentView.findViewById(R.id.personal_edition_expertise_item_position);
        expertiseFrom = (EditText) fragmentView.findViewById(R.id.personal_edition_expertise_item_from);
        expertiseTo = (EditText) fragmentView.findViewById(R.id.personal_edition_expertise_item_to);
        expertiseCategory = (EditText) fragmentView.findViewById(R.id.personal_edition_expertise_item_category);
        expertiseExpertise = (EditText) fragmentView.findViewById(R.id.personal_edition_expertise_item_expertise);

        skillCategory = (EditText) fragmentView.findViewById(R.id.personal_edition_skill_item_category);
        skillSkills = (EditText) fragmentView.findViewById(R.id.personal_edition_skill_item_skills);

        Button add = (Button) fragmentView.findViewById(R.id.personal_edition_add);
        Button exit = (Button) fragmentView.findViewById(R.id.personal_edition_exit);
        add.setOnClickListener(this);
        exit.setOnClickListener(this);


        // TODO estaria bueno pasarle el adapter actual.
        arrayAdapter = (ArrayAdapter) getArguments().get("personalEditionAdapter");
        String layout = getArguments().getString("layout");
        professionalId = getArguments().getString("professionalId");
        if (layout.equals("expertise")) {
            expertiseLayoutFlag = true;
            skillLayout.setVisibility(View.GONE);
        } else {
            expertiseLayout.setVisibility(View.GONE);
        }


        //Asocio la listView con el adapter
        personalEditionList.setAdapter(arrayAdapter);
        personalEditionList.setOnItemClickListener(this);
        personalEditionList.setOnItemClickListener(this);

        // Si tarda mucho debería mostrar una barra de progreso
        ProgressBar bar= new ProgressBar(getContext());
        bar.setIndeterminate(true);
        personalEditionList.setEmptyView(bar);
        return fragmentView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_edition_item_remove:
                arrayAdapter.remove(current);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        current = adapterView.getItemAtPosition(i);
    }

    public void addPersonalEditionFragment(View v) {
        if (expertiseLayoutFlag) {
            ProfileExpertise profileExpertise = new ProfileExpertise(expertiseCompany.getText().toString());
            profileExpertise.setPosition(expertisePosition.getText().toString());
            profileExpertise.setFrom(expertiseFrom.getText().toString());
            profileExpertise.setTo(expertiseTo.getText().toString());
            profileExpertise.setExpertise(expertiseExpertise.getText().toString());
            profileExpertise.setCategory(expertiseCategory.getText().toString());
            expertiseCompany.setText("");
            expertisePosition.setText("");
            expertiseFrom.setText("");
            expertiseTo.setText("");
            expertiseCategory.setText("");
            expertiseExpertise.setText("");

            arrayAdapter.add(profileExpertise);
        } else {
            ProfileSkill profileSkill = new ProfileSkill();
            profileSkill.setCategory(skillCategory.getText().toString());
            profileSkill.getSkills().addAll(Arrays.asList(skillSkills.getText().toString().split(",")));

            skillCategory.setText("");
            skillSkills.setText("");

            arrayAdapter.add(profileSkill);
        }
    }

    public void exitPersonalEditionFragment(View v) {
        getDialog().dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personal_edition_add:
                addPersonalEditionFragment(view);
                break;
            case R.id.personal_edition_exit:
                exitPersonalEditionFragment(view);
                break;
            default:
                break;
        }
    }
}
