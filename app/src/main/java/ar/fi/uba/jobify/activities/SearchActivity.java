package ar.fi.uba.jobify.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ar.fi.uba.jobify.tasks.contact.PostContactRequestTask;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

public class SearchActivity extends AppCompatActivity implements PostContactRequestTask.ContactAggregator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Red de Profesionales");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onContactRequestSuccess(String contact) {
        ShowMessage.showSnackbarSimpleMessage(getCurrentFocus(),"["+contact+"] solicitud pendiente.");
    }
}
