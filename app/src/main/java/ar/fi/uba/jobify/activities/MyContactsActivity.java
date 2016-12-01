package ar.fi.uba.jobify.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.DeleteContactRejectTask;
import ar.fi.uba.jobify.tasks.contact.GetContactPendingTask;
import ar.fi.uba.jobify.tasks.contact.GetContactsTask;
import ar.fi.uba.jobify.tasks.contact.GetMineContactListTask;
import ar.fi.uba.jobify.tasks.contact.PostContactAcceptTask;
import ar.fi.uba.jobify.tasks.contact.PostContactRequestTask;
import ar.fi.uba.jobify.tasks.recomendation.DeleteVoteTask;
import ar.fi.uba.jobify.tasks.recomendation.PostVoteTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import fi.uba.ar.jobify.R;

public class MyContactsActivity extends AppCompatActivity
        implements PostContactRequestTask.ContactAggregator, PostContactAcceptTask.ContactAggregator,
        GetContactPendingTask.ContactAggregator, DeleteContactRejectTask.ContactAggregator,
        PostVoteTask.Recomendation, DeleteVoteTask.Recomendation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ascio el layout, dentro de este tengo el fragment
        setContentView(R.layout.activity_my_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis contactos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onContactRequestSuccess() {
        // TODO smpiano contact done.
    }

    @Override
    public void onContactAcceptSuccess() {
        // TODO smpiano contact accept done.
    }

    @Override
    public void onContactPendingSuccess() {
        // TODO smpiano contact pending done.
    }

    @Override
    public void onContactRejectSuccess() {
        // TODO smpiano contact reject done.
    }

    @Override
    public void onVoteSuccess() {
        // TODO smpiano vote done.
    }

    @Override
    public void onUnvoteSuccess() {
        // TODO smpiano vote done.
    }
}
