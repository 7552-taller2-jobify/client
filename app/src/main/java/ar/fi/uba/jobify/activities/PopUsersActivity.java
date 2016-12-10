package ar.fi.uba.jobify.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ar.fi.uba.jobify.tasks.contact.PostContactRequestTask;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

public class PopUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Más Populares");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
