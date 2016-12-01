package ar.fi.uba.jobify.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ar.fi.uba.jobify.adapters.ExpertiseListAdapter;
import ar.fi.uba.jobify.adapters.SkillListAdapter;
import ar.fi.uba.jobify.domains.Professional;
import ar.fi.uba.jobify.domains.ProfilePersonal;
import ar.fi.uba.jobify.domains.ProfilePicture;
import ar.fi.uba.jobify.domains.ProfileSummary;
import ar.fi.uba.jobify.fragments.DatePickerFragment;
import ar.fi.uba.jobify.fragments.ExpertiseListFragment;
import ar.fi.uba.jobify.fragments.SkillListFragment;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.service.InverseGeocodeService;
import ar.fi.uba.jobify.tasks.profile.personal.GetPersonalTask;
import ar.fi.uba.jobify.tasks.profile.picture.GetPictureTask;
import ar.fi.uba.jobify.tasks.profile.summary.GetSummaryTask;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;
import static ar.fi.uba.jobify.utils.FieldValidator.isValidMail;

public class ProfileActivity extends AppCompatActivity
        implements GetPersonalTask.ProfileRead,
        GetSummaryTask.ProfileRead,
        GetPictureTask.ProfileRead,
        ExpertiseListAdapter.ExpertisesRead,
        SkillListAdapter.SkillRead,
        View.OnClickListener,
        InverseGeocodeService.InverseGeocodeServiceResult {

    private MyPreferences pref;
    private MyPreferenceHelper helper;
    private String professionalId;

    private ImageView personalPhoto;
    private EditText personalEmail;
    private ImageView personalEmailIcon;
    private EditText personalName;
    private EditText personalBirthday;
    private Button personalBirthdayButton;
    private TextView personalGender;
    private TextView personalAddress;
    private ImageView personalMap;
    private EditText personalSummary;
    private TextView personalSummaryLabel;

    //expertises
    private CardView expertisesView;
    private TextView profileExpertisesLabel;

    //skills
    private CardView skillsView;
    private TextView profileSkillsLabel;



    private FloatingActionButton fab;
    private CollapsingToolbarLayout toolbarName;
    private DatePickerFragment birthdayFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pref = new MyPreferences(this);
        helper = new MyPreferenceHelper(this);
        professionalId = helper.getProfessional().getEmail();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String intentExtraUid = intent.getStringExtra(Intent.EXTRA_UID);
        if (intentExtraUid != null) professionalId = intentExtraUid;

        if (RestClient.isOnline(this)) {
            new GetPersonalTask(this).execute(professionalId);
            new GetSummaryTask(this).execute(professionalId);
            new GetPictureTask(this).execute(professionalId);
        }


        // inflating the expertise fragment
        expertisesView = (CardView) findViewById(R.id.profile_view_expertises);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_view_expertises, new ExpertiseListFragment()).commit();

        //inflating the skills fragment
        skillsView = (CardView) findViewById(R.id.profile_view_skills);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_view_skills, new SkillListFragment()).commit();


        // TODO edition time.
        personalPhoto = (ImageView) findViewById(R.id.profile_detail_image);
        personalEmail = (EditText) findViewById(R.id.profile_detail_email);
        personalEmailIcon = (ImageView) findViewById(R.id.profile_detail_email_icon);
        personalName = (EditText) findViewById(R.id.profile_detail_name);
        personalBirthday = (EditText) findViewById(R.id.profile_detail_birthday);
        personalBirthdayButton = (Button) findViewById(R.id.profile_edit_button_birthday);
        personalGender = (TextView) findViewById(R.id.profile_detail_gender);
        personalAddress = ((TextView) findViewById(R.id.profile_detail_address));
        personalMap = (ImageView) findViewById(R.id.profile_detail_map);
        personalSummary = (EditText) findViewById(R.id.profile_detail_summary);
        personalSummaryLabel = (TextView) findViewById(R.id.profile_detail_summary_label);

        //expertises
        profileExpertisesLabel = (TextView) findViewById(R.id.profile_detail_expertises_label);

        //skills
        profileSkillsLabel = (TextView) findViewById(R.id.profile_detail_skills_label);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbarName = (CollapsingToolbarLayout) findViewById(R.id.profile_detail_collapsing_toolbar);

        personalEmail.setOnClickListener(this);
        personalEmailIcon.setOnClickListener(this);
        personalName.setOnClickListener(this);
        personalBirthday.setOnClickListener(this);
        personalGender.setOnClickListener(this);
        personalAddress.setOnClickListener(this);
        fab.setOnClickListener(this);

        this.startCleanUpUI(true);
    }

    private void startCleanUpUI(Boolean isReadMode) {

        profileExpertisesLabel.setVisibility(View.INVISIBLE);
        profileSkillsLabel.setVisibility(View.INVISIBLE);

        if (isReadMode) {
            toolbarName.setTitle("");

            personalEmail.setText("");
            personalName.setText("");
            personalBirthday.setText("");
            personalGender.setText("");
            personalAddress.setText("");

            personalEmail.setTag(personalEmail.getKeyListener());
            personalEmail.setKeyListener(null);
            personalName.setTag(personalName.getKeyListener());
            personalName.setKeyListener(null);
            personalBirthday.setTag(personalBirthday.getKeyListener());
            personalBirthday.setKeyListener(null);
            personalSummary.setTag(personalSummary.getKeyListener());
            personalSummary.setKeyListener(null);
            personalAddress.setTag(personalAddress.getKeyListener());
            personalAddress.setKeyListener(null);

            personalBirthdayButton.setVisibility(View.INVISIBLE);
            personalSummaryLabel.setVisibility(View.INVISIBLE);
        } else {
            personalEmail.setKeyListener((KeyListener) personalEmail.getTag());
            personalName.setKeyListener((KeyListener) personalName.getTag());
            personalBirthday.setKeyListener((KeyListener) personalBirthday.getTag());
            personalBirthdayButton.setVisibility(View.VISIBLE);
            personalSummaryLabel.setVisibility(View.VISIBLE);
            personalSummary.setKeyListener((KeyListener) personalSummary.getTag());
            personalAddress.setKeyListener((KeyListener) personalAddress.getTag());
        }
    }

    public void showSnackbarSimpleMessage(String msg){
        ShowMessage.showSnackbarSimpleMessage(this.getCurrentFocus(), msg);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.fab) {
            CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.profile_detail_coordinatorLayout);
            ShowMessage.showSnackbarSimpleMessage(cl, "No se hace nada");
            //if (this.draftOrders == null) {
            //    ShowMessage.showSnackbarSimpleMessage(cl, "No se pudo obtener info del pedido");
            //} else if (this.draftOrders.size() > 0) {
            //    // si hay orden, mostrar mensaje diciendo que ya existe una orden "activa"
            //    ShowMessage.showSnackbarSimpleMessage(cl, "Ya existe un pedido borrador en curso!");
            //} else {
            //    // si no hay orden, crear una nueva
            //Cuidado que cambio todo
            //    if (RestClient.isOnline(this)) new PostOrdersTask(this).execute(pref.get(getString(R.string.shared_pref_current_seller), 1L).toString(), Long.toString(professionalId));
            //}
        }
        if((view.getId() == R.id.profile_detail_email || view.getId() == R.id.profile_detail_email_icon)
                && isValidMail(personalEmail.getText())){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",personalEmail.getText().toString().trim(), null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[Jobify] - Mensaje de "+helper.getProfessional().getFullName());
            startActivity(Intent.createChooser(intent, getString(R.string.redirection_mail)));
        }
    }

    public void updatePhoto(Professional professional){

    }

    public void updateMap(ProfilePersonal personal) {
        // validacion
        if (personal.getLat().isEmpty() || personal.getLon().isEmpty()) {
            ShowMessage.toastMessage(this,getString(R.string.cannot_display_map));
            return;
        }

        //llamo al servicio externo
        InverseGeocodeService addressService = new InverseGeocodeService(this,personal.getLat(),personal.getLon());

        // Pongo el punto en el mapa estatico
        String mapURL="https://maps.googleapis.com/maps/api/staticmap?" +
                "zoom=15" +
                "&size=400x300" +
                "&maptype=roadmap" +
                "&key=AIzaSyAyta4hXfctjxRTFWd2zKKeDCpVe3Qa-1E" +
                "&center="+ personal.getLat()+','+ personal.getLon()+
                "&markers=color:blue%7C"+ personal.getLat()+','+ personal.getLon();
        Picasso.with(this).load(mapURL).into(personalMap);
    }

    @Override
    public void onInverseCalculationSuccess(String address) {
        if (address.isEmpty()) {
            ShowMessage.toastMessage(this,getString(R.string.cannot_inverse_address_calc));
        } else {
            personalAddress.setText(address);
        }
    }

    @Override
    public void onProfilePersonalSuccess(ProfilePersonal personal) {
        Integer edad = DateUtils.getEdad(personal.getBirthday());
        toolbarName.setTitle(isContentValid(personal.getFirstName())+" ("+edad+" años)");

        personalEmail.setText(isContentValid(personal.getEmail()));
        personalName.setText(isContentValid(personal.getFullName()));
        personalBirthday.setText(isContentValid(DateUtils.formatShortDateArg2(personal.getBirthday())));
        personalGender.setText(getString((isContentValid(personal.getGender()).equals("M"))? R.string.masculino : R.string.femenino));


        //personalAddress.setText(isContentValid(professional.getAddress()));

        //Coloreando el email para la action.
        int colorAccent = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        personalEmail.setText(isContentValid(personal.getEmail()));
        if (isValidMail(personalEmail.getText())) {
            personalEmailIcon.setColorFilter(colorAccent);
            personalEmail.setTextColor(colorAccent);
        }

        //actualizo mapa
        updateMap(personal);
    }

    public void showDatePickerDialog(View view) {
        birthdayFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("id", R.id.profile_detail_birthday);
        birthdayFragment.setArguments(args);
        birthdayFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onProfileSummarySuccess(ProfileSummary summary) {
        if (summary != null && !summary.getSummary().isEmpty()) {
            personalSummary.setText(isContentValid(summary.getSummary()));
        } else {
            personalSummary.setText(getString(R.string.summary_is_empty));
        }
    }

    @Override
    public void onExpertisesEmpty() {
        profileExpertisesLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSkillEmpty() {
        profileSkillsLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProfilePictureSuccess(ProfilePicture picture) {
        if (isContentValid(picture.getPicture()).isEmpty()) {
            Picasso.with(this).load(R.drawable.logo).into(personalPhoto);
        } else {
            Picasso.with(this).load(picture.getPicture()).into(personalPhoto);
        }
    }
}
