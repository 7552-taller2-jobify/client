package ar.fi.uba.jobify.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ar.fi.uba.jobify.tasks.contact.DeleteContactRejectTask;
import ar.fi.uba.jobify.tasks.contact.GetContactPendingTask;
import ar.fi.uba.jobify.tasks.contact.PostContactAcceptTask;
import ar.fi.uba.jobify.tasks.recomendation.DeleteVoteTask;
import ar.fi.uba.jobify.tasks.recomendation.PostVoteTask;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

public class MyContactsActivity extends AppCompatActivity
        implements PostContactAcceptTask.ContactAggregator,
        DeleteContactRejectTask.ContactAggregator,
        PostVoteTask.Recomendation, DeleteVoteTask.Recomendation {

    private TabLayout tabsLayout;

    public TabLayout getTabsLayout() {
        return tabsLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ascio el layout, dentro de este tengo el fragment
        setContentView(R.layout.activity_my_contacts);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis contactos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabsLayout = (TabLayout) findViewById(R.id.my_contacts_tabs);


        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_professional_pager);

        ViewPager viewer = (ViewPager) findViewById(R.id.professional_view_pager);

        //Defino el adapter
        ProfessionalFriendsAdapter adapter = new ProfessionalFriendsAdapter(getSupportFragmentManager());
        //Asocio la listView con el adapter
        viewer.setAdapter(adapter);
         */


    }

    @Override
    public void onContactAcceptSuccess(String contact) {
        ShowMessage.showSnackbarSimpleMessage(getCurrentFocus(),"["+contact+"] solicitud aceptada.");
    }


    @Override
    public void onContactRejectSuccess(String contact) {
        ShowMessage.showSnackbarSimpleMessage(getCurrentFocus(),"["+contact+"] solicitud rechazada.");
    }

    @Override
    public void onUnvoteSuccess(String contact) {
        ShowMessage.showSnackbarSimpleMessage(getCurrentFocus(),"["+contact+"] me fallaste!");
    }

    @Override
    public void onVoteSuccess(String contact) {
        ShowMessage.showSnackbarSimpleMessage(getCurrentFocus(),"["+contact+"] recomendado.");
    }
}
