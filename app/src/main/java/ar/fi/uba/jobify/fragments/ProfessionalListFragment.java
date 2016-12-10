package ar.fi.uba.jobify.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuView;
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
import ar.fi.uba.jobify.adapters.ProfessionalListAdapter;
import ar.fi.uba.jobify.domains.ProfessionalSearchItem;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.DeleteContactRejectTask;
import ar.fi.uba.jobify.tasks.contact.PostContactAcceptTask;
import ar.fi.uba.jobify.tasks.recomendation.DeleteVoteTask;
import ar.fi.uba.jobify.tasks.recomendation.PostVoteTask;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfessionalListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ProfessionalSearchItem professionalSelected;
    private MyContactsActivity act;
    private int myFriendsRequest;
    private ProfessionalListAdapter professionalListAdapter;

    public ProfessionalListFragment() {
        super();
    }

    public void refresh() {
        if (professionalListAdapter != null)
            professionalListAdapter.refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        act = (MyContactsActivity) getActivity();
        myFriendsRequest = getArguments().getInt("MY_FRIENDS");

        //inflo la vista de listado de elementos
        View fragmentView = inflater.inflate(R.layout.fragment_professional_list, container, false);
        ListView professionalList = (ListView) fragmentView.findViewById(R.id.professional_list_view);

        //desplegable
        registerForContextMenu(professionalList);


        //Defino el adapter
        professionalListAdapter = new ProfessionalListAdapter(
                getActivity(),
                getContext(),
                R.layout.list_professional_item,
                new ArrayList<ProfessionalSearchItem>(), myFriendsRequest);
        //Asocio la listView con el adapter
        professionalList.setAdapter(professionalListAdapter);
        professionalList.setOnItemClickListener(this);

        // Si tarda mucho debería mostrar una barra de progreso
        ProgressBar bar= new ProgressBar(getContext());
        bar.setIndeterminate(true);
        professionalList.setEmptyView(bar);
        professionalListAdapter.refresh();
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
        if (myFriendsRequest == 0) {
            switch (item.getItemId()) {
                case R.id.menu_my_contacts_item_chat:
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra(Intent.EXTRA_UID, professionalSelected.getEmail());
                    startActivity(intent);
                    break;
                case R.id.menu_my_contacts_item_vote:
                    if (RestClient.isOnline(getContext())) {
                        new PostVoteTask( professionalListAdapter ).execute(professionalSelected.getEmail());
                    }
                    break;
                case R.id.menu_my_contacts_item_unvote:
                    if (RestClient.isOnline(getContext())) {
                        new DeleteVoteTask( professionalListAdapter ).execute(professionalSelected.getEmail());
                    }
                    break;
                default:
                    return super.onContextItemSelected(item);
            }
        } else {
            switch (item.getItemId()) {
                case R.id.menu_my_contacts_item_accept:
                    if (RestClient.isOnline(getContext())) {
                        new PostContactAcceptTask( professionalListAdapter ).execute(professionalSelected.getEmail());
                    }
                    break;
                case R.id.menu_my_contacts_item_reject:
                    if (RestClient.isOnline(getContext())) {
                        new DeleteContactRejectTask( professionalListAdapter ).execute(professionalSelected.getEmail());
                    }
                    break;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        professionalSelected = (ProfessionalSearchItem) lv.getItemAtPosition(acmi.position);

        MenuInflater inflater = new MenuInflater(this.getContext());
        inflater.inflate((myFriendsRequest == 0)? R.menu.menu_my_friends_item : R.menu.menu_my_solicitud_contacts_item, menu);

        //TODO smpiano vote.
        if (myFriendsRequest == 0) {
            if (professionalSelected.getVotedByMe()) {
                menu.findItem(R.id.menu_my_contacts_item_vote).setVisible(false);
                menu.findItem(R.id.menu_my_contacts_item_unvote).setVisible(true);
            } else {
                menu.findItem(R.id.menu_my_contacts_item_vote).setVisible(true);
                menu.findItem(R.id.menu_my_contacts_item_unvote).setVisible(false);
            }
        }

    }
}
