package ar.fi.uba.jobify.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ar.fi.uba.jobify.domains.Contact;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.contact.GetContactTask;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;
import static ar.fi.uba.jobify.utils.FieldValidator.isValidMail;
import static ar.fi.uba.jobify.utils.FieldValidator.isValidPhone;

public class ContactActivity extends AppCompatActivity implements GetContactTask.ClientReceiver, View.OnClickListener{

    private long clientId;
    private MyPreferences pref = new MyPreferences(this);

    public ContactActivity(){
        super();
        clientId=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent= getIntent();
        clientId = intent.getLongExtra(Intent.EXTRA_UID, 0);
        if (RestClient.isOnline(this)) new GetContactTask(this).execute(Long.toString(clientId));

        findViewById(R.id.client_detail_phone).setOnClickListener(this);
        findViewById(R.id.client_detail_phone_number_icon).setOnClickListener(this);
        findViewById(R.id.client_detail_email).setOnClickListener(this);
        findViewById(R.id.client_detail_email_icon).setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        pref.save(getString(R.string.shared_pref_current_client_id), clientId);

        this.startCleanUpUI();
    }

    private void startCleanUpUI() {
        ((CollapsingToolbarLayout) findViewById(R.id.client_detail_collapsing_toolbar)).setTitle("");

        ((TextView) findViewById(R.id.client_detail_id)).setText("");
        ((TextView) findViewById(R.id.client_detail_name)).setText("");
        ((TextView) findViewById(R.id.client_detail_company)).setText("");
        ((TextView) findViewById(R.id.client_detail_cuil)).setText("");
        ((TextView) findViewById(R.id.client_detail_address)).setText("");
        ((TextView) findViewById(R.id.client_detail_phone)).setText("");
        ((TextView) findViewById(R.id.client_detail_email)).setText("");
    }

    public void showSnackbarSimpleMessage(String msg){
        ShowMessage.showSnackbarSimpleMessage(this.getCurrentFocus(), msg);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.fab) {
            CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.client_detail_coordinatorLayout);
            ShowMessage.showSnackbarSimpleMessage(cl, "No se hace nada");
            //if (this.draftOrders == null) {
            //    ShowMessage.showSnackbarSimpleMessage(cl, "No se pudo obtener info del pedido");
            //} else if (this.draftOrders.size() > 0) {
            //    // si hay orden, mostrar mensaje diciendo que ya existe una orden "activa"
            //    ShowMessage.showSnackbarSimpleMessage(cl, "Ya existe un pedido borrador en curso!");
            //} else {
            //    // si no hay orden, crear una nueva
            //Cuidado que cambio todo
            //    if (RestClient.isOnline(this)) new PostOrdersTask(this).execute(pref.get(getString(R.string.shared_pref_current_seller), 1L).toString(), Long.toString(clientId));
            //}
        } else if((view.getId() == R.id.client_detail_phone || view.getId() == R.id.client_detail_phone_number_icon)
                && isValidPhone(((TextView) findViewById(R.id.client_detail_phone)).getText())){
            String uri = "tel:" + ((TextView) findViewById(R.id.client_detail_phone)).getText().toString().trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
        if((view.getId() == R.id.client_detail_email || view.getId() == R.id.client_detail_email_icon)
                && isValidMail(((TextView) findViewById(R.id.client_detail_email)).getText())){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",((TextView) findViewById(R.id.client_detail_email)).getText().toString().trim(), null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[OrderTracker] - Mensaje del vendedor");
            startActivity(Intent.createChooser(intent, "Send email..."));
        }
    }

    public void updateClientInformation(Contact contact){
        ((CollapsingToolbarLayout) findViewById(R.id.client_detail_collapsing_toolbar)).setTitle(contact.getFullName());
        if (isContentValid(contact.getAvatar()).isEmpty()) {
            Picasso.with(this).load(R.drawable.logo).into(((ImageView) findViewById(R.id.client_detail_image)));
        } else {
            Picasso.with(this).load(contact.getAvatar()).into(((ImageView) findViewById(R.id.client_detail_image)));
        }

        ((TextView) findViewById(R.id.client_detail_id)).setText(isContentValid(Long.toString(contact.getId())));
        ((TextView) findViewById(R.id.client_detail_name)).setText(isContentValid(contact.getFullName()));
        ((TextView) findViewById(R.id.client_detail_company)).setText(isContentValid(contact.getCompany()));
        ((TextView) findViewById(R.id.client_detail_cuil)).setText(isContentValid(contact.getCuil()));
        ((TextView) findViewById(R.id.client_detail_address)).setText(isContentValid(contact.getAddress()));

        int colorAccent = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        TextView phoneField = ((TextView)findViewById(R.id.client_detail_phone));
        phoneField.setText(isContentValid(contact.getPhoneNumber()));
        if (isValidPhone(phoneField.getText())) {
            ((ImageView) findViewById(R.id.client_detail_phone_number_icon)).setColorFilter(colorAccent);
            phoneField.setTextColor(colorAccent);
        }

        TextView mailField = ((TextView)findViewById(R.id.client_detail_email));
        mailField.setText(isContentValid(contact.getEmail()));
        if (isValidMail(mailField.getText())) {
            ((ImageView)findViewById(R.id.client_detail_email_icon)).setColorFilter(colorAccent);
            mailField.setTextColor(colorAccent);
        }

        String mapURL="https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=400x300&maptype=roadmap&key=AIzaSyB7KkfXSNVvngEQ0LwhvLSt7i1oB4p2RdQ&center="+ contact.getLat()+','+ contact.getLon()+"&markers=color:blue%7C"+ contact.getLat()+','+ contact.getLon();
        Picasso.with(this).load(mapURL).into(((ImageView) findViewById(R.id.client_detail_map)));
    }
}
