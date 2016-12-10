package ar.fi.uba.jobify.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import ar.fi.uba.jobify.adapters.ExpertiseListAdapter;
import ar.fi.uba.jobify.adapters.SkillListAdapter;
import ar.fi.uba.jobify.domains.Professional;
import ar.fi.uba.jobify.domains.ProfileExpertiseResult;
import ar.fi.uba.jobify.domains.ProfilePersonal;
import ar.fi.uba.jobify.domains.ProfilePicture;
import ar.fi.uba.jobify.domains.ProfileSkillList;
import ar.fi.uba.jobify.domains.ProfileSummary;
import ar.fi.uba.jobify.fragments.DatePickerFragment;
import ar.fi.uba.jobify.fragments.ExpertiseListFragment;
import ar.fi.uba.jobify.fragments.PersonalEditionFragment;
import ar.fi.uba.jobify.fragments.SkillListFragment;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.service.InverseGeocodeService;
import ar.fi.uba.jobify.tasks.profile.expertise.GetExpertiseTask;
import ar.fi.uba.jobify.tasks.profile.expertise.PutExpertiseTask;
import ar.fi.uba.jobify.tasks.profile.personal.GetPersonalTask;
import ar.fi.uba.jobify.tasks.profile.personal.PutPersonalTask;
import ar.fi.uba.jobify.tasks.profile.picture.GetPictureTask;
import ar.fi.uba.jobify.tasks.profile.picture.PutPictureTask;
import ar.fi.uba.jobify.tasks.profile.skills.GetSkillsTask;
import ar.fi.uba.jobify.tasks.profile.skills.PutSkillsTask;
import ar.fi.uba.jobify.tasks.profile.summary.GetSummaryTask;
import ar.fi.uba.jobify.tasks.profile.summary.PutSummaryTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.FieldValidator;
import ar.fi.uba.jobify.utils.LocationHelper;
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
        InverseGeocodeService.InverseGeocodeServiceResult,
        PutPersonalTask.ProfileEdit,
        PutSummaryTask.ProfileEdit,
        PutPictureTask.ProfileEdit,
        PutExpertiseTask.ProfileEdit,
        PutSkillsTask.ProfileEdit {

    private MyPreferences pref;
    private MyPreferenceHelper helper;
    private LocationHelper locationHelper;
    private String professionalId;

    private ImageView personalPhoto;
    private EditText personalEmail;
    private ImageView personalEmailIcon;
    private EditText personalName;
    private EditText personalBirthday;
    private Button personalBirthdayButton;
    private TextView personalGender;
    private RadioGroup personalGenderRadioGroup;
    private RadioButton personalGenderRadioMale;
    private RadioButton personalGenderRadioFemale;
    private TextView personalAddress;
    private CheckBox personalAddressCheck;
    private ImageView personalMap;
    private EditText personalSummary;
    private TextView personalSummaryLabel;

    //photo
    private CardView profilePhotoView;
    private EditText profileDetailPhoto;

    //expertises
    private CardView expertisesView;
    private TextView profileExpertisesLabel;

    //skills
    private CardView skillsView;
    private TextView profileSkillsLabel;

    private Boolean isReadMode = true;

    private FloatingActionButton fabEditMode;
    private FloatingActionButton fabCancel;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabExpertiseEditMode;
    private FloatingActionButton fabSkillEditMode;
    private CollapsingToolbarLayout toolbarName;
    private DatePickerFragment birthdayFragment;

    //read elements
    private ProfilePersonal personal;
    private ProfileSummary summary;
    private ProfilePicture picture;
    private boolean afterPersonalSave = false;
    private boolean afterPictureSave = false;
    private Professional professionalUpdated;
    private ExpertiseListFragment expertiseListFragment;
    private SkillListFragment skillListFragment;


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


        //bundle par fragments
        Bundle b = new Bundle();
        b.putString("professionalId", professionalId);
        // inflating the expertise fragment
        expertisesView = (CardView) findViewById(R.id.profile_view_expertises);
        expertiseListFragment = new ExpertiseListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_view_expertises, expertiseListFragment).commit();
        expertiseListFragment.setArguments(b);

        //inflating the skills fragment
        skillsView = (CardView) findViewById(R.id.profile_view_skills);
        skillListFragment = new SkillListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.profile_view_skills, skillListFragment).commit();
        skillListFragment.setArguments(b);


        // TODO edition time.
        personalPhoto = (ImageView) findViewById(R.id.profile_detail_image);
        personalEmail = (EditText) findViewById(R.id.profile_detail_email);
        personalEmailIcon = (ImageView) findViewById(R.id.profile_detail_email_icon);
        personalName = (EditText) findViewById(R.id.profile_detail_name);
        personalBirthday = (EditText) findViewById(R.id.profile_detail_birthday);
        personalBirthdayButton = (Button) findViewById(R.id.profile_edit_button_birthday);
        personalGender = (TextView) findViewById(R.id.profile_detail_gender);
        personalGenderRadioGroup = (RadioGroup) findViewById(R.id.profile_detail_gender_radiobutton);
        personalGenderRadioMale = (RadioButton) findViewById(R.id.profile_detail_gender_radio_m);
        personalGenderRadioFemale = (RadioButton) findViewById(R.id.profile_detail_gender_radio_f);
        personalAddress = ((TextView) findViewById(R.id.profile_detail_address));
        personalAddressCheck = ((CheckBox) findViewById(R.id.profile_detail_address_check));
        personalMap = (ImageView) findViewById(R.id.profile_detail_map);
        personalSummary = (EditText) findViewById(R.id.profile_detail_summary);
        personalSummaryLabel = (TextView) findViewById(R.id.profile_detail_summary_label);

        profilePhotoView = (CardView) findViewById(R.id.profile_detail_photo_card);
        profileDetailPhoto = (EditText) findViewById(R.id.profile_detail_photo);

        //expertises
        profileExpertisesLabel = (TextView) findViewById(R.id.profile_detail_expertises_label);

        //skills
        profileSkillsLabel = (TextView) findViewById(R.id.profile_detail_skills_label);

        fabEditMode = (FloatingActionButton) findViewById(R.id.fab_edit_mode);
        fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);
        fabSave = (FloatingActionButton) findViewById(R.id.fab_save);
        fabExpertiseEditMode = (FloatingActionButton) findViewById(R.id.fab_expertise_edit_mode);
        fabSkillEditMode = (FloatingActionButton) findViewById(R.id.fab_skill_edit_mode);
        toolbarName = (CollapsingToolbarLayout) findViewById(R.id.profile_detail_collapsing_toolbar);


        personalEmail.setOnClickListener(this);
        personalEmailIcon.setOnClickListener(this);
        fabEditMode.setOnClickListener(this);
        fabCancel.setOnClickListener(this);
        fabSave.setOnClickListener(this);
        fabExpertiseEditMode.setOnClickListener(this);
        fabSkillEditMode.setOnClickListener(this);

        this.startCleanUpUI();
    }

    private void startCleanUpUI() {

        profileExpertisesLabel.setVisibility(View.GONE);
        profileSkillsLabel.setVisibility(View.GONE);

        if (isReadMode) {
            toolbarName.setTitle("");

            personalEmail.setText("");
            personalName.setText("");
            personalBirthday.setText("");
            personalGender.setText("");
            personalAddress.setText("");

            personalEmail.setKeyListener(null);
            personalName.setTag(personalName.getKeyListener());
            personalName.setKeyListener(null);
            personalBirthday.setTag(personalBirthday.getKeyListener());
            personalBirthday.setKeyListener(null);
            personalSummary.setTag(personalSummary.getKeyListener());
            personalSummary.setKeyListener(null);
            personalAddressCheck.setVisibility(View.GONE);

            personalGender.setVisibility(View.VISIBLE);
            personalGenderRadioGroup.setVisibility(View.GONE);
            personalGenderRadioMale.setVisibility(View.GONE);
            personalGenderRadioFemale.setVisibility(View.GONE);

            personalBirthdayButton.setVisibility(View.GONE);
            personalSummaryLabel.setVisibility(View.GONE);

            profilePhotoView.setVisibility(View.GONE);

            // es la misma persona y puede editar
            if (professionalId.equals(helper.getProfessional().getEmail())) {
                fabCancel.setVisibility(View.GONE);
                fabSave.setVisibility(View.GONE);
                fabEditMode.setVisibility(View.VISIBLE);
                fabExpertiseEditMode.setVisibility(View.GONE);
                fabSkillEditMode.setVisibility(View.GONE);
            }


        } else {
            personalName.setKeyListener((KeyListener) personalName.getTag());
            //personalBirthday.setKeyListener((KeyListener) personalBirthday.getTag());
            personalBirthdayButton.setVisibility(View.VISIBLE);
            personalSummaryLabel.setVisibility(View.VISIBLE);
            personalSummary.setKeyListener((KeyListener) personalSummary.getTag());

            personalAddress.setText(getString(R.string.profile_save_current_address));
            personalAddressCheck.setVisibility(View.VISIBLE);

            if (personalGender.getText().toString().equals("Hombre")) personalGenderRadioMale.setChecked(true);
            else personalGenderRadioFemale.setChecked(true);
            personalGenderRadioGroup.setVisibility(View.VISIBLE);
            personalGenderRadioMale.setVisibility(View.VISIBLE);
            personalGenderRadioFemale.setVisibility(View.VISIBLE);
            personalGender.setVisibility(View.GONE);

            profilePhotoView.setVisibility(View.VISIBLE);
        }
    }

    public void showSnackbarSimpleMessage(String msg){
        if (this.getCurrentFocus() != null)
            ShowMessage.showSnackbarSimpleMessage(this.getCurrentFocus(), msg);
    }

    @Override
    public void onClick(View view) {
        // seleccionan en editar
        if (view.getId()==R.id.fab_edit_mode) {
            isReadMode = false;
            fabEditMode.setVisibility(View.GONE);
            fabCancel.setVisibility(View.VISIBLE);
            fabSave.setVisibility(View.VISIBLE);
            fabExpertiseEditMode.setVisibility(View.VISIBLE);
            fabSkillEditMode.setVisibility(View.VISIBLE);

            locationHelper = new LocationHelper();
            locationHelper.updatePosition(this);

            String lat = pref.get(getString(R.string.shared_pref_current_location_lat), AppSettings.getGpsLat());
            String lon = pref.get(getString(R.string.shared_pref_current_location_lon), AppSettings.getGpsLon());
            updateMap(lat, lon);

            startCleanUpUI();
        }


        if (view.getId()==R.id.fab_save) {

            String[] name = personalName.getText().toString().split(",");
            String firstName = "";
            if (name.length > 0) firstName = name[1].trim();
            String lat;
            String lon;
            if (personalAddressCheck.isChecked()) {
                lat = pref.get(getString(R.string.shared_pref_current_location_lat), AppSettings.getGpsLat());
                lon = pref.get(getString(R.string.shared_pref_current_location_lon), AppSettings.getGpsLon());
            } else {
                lat = personal.getLat();
                lon = personal.getLon();
            }
            new PutPersonalTask(this).execute(firstName,personalName.getText().toString().split(",")[0].trim(),
                    personalBirthday.getText().toString(),personalGenderRadioMale.isChecked()?"M":"F",
                    lat, lon);

            // summary
            new PutSummaryTask(this).execute(personalSummary.getText().toString());

            //photo
            new PutPictureTask(this).execute(profileDetailPhoto.getText().toString());

            // expertises
            ProfileExpertiseResult r = expertiseListFragment.getAdapter().getExpertises();
            Gson gson = new Gson();
            String json = gson.toJson(r);
            new PutExpertiseTask(this).execute(json);

            // skills
            ProfileSkillList s = skillListFragment.getAdapter().getSkills();
            json = gson.toJson(s);
            new PutSkillsTask(this).execute(json);

            // TODO smpiano save all.
            isReadMode = true;
            startCleanUpUI();
        }

        if (view.getId()==R.id.fab_cancel) {
            isReadMode = true;
            startCleanUpUI();

            // restarting
            onProfilePersonalSuccess(personal);
            onProfileSummarySuccess(summary);
            onProfilePictureSuccess(picture);
        }

        if (view.getId()==R.id.fab_expertise_edit_mode) {

            PersonalEditionFragment personalEditionFragment = new PersonalEditionFragment();
            Bundle args = new Bundle();
            args.putSerializable("personalEditionAdapter", expertiseListFragment.getAdapter());
            args.putString("layout", "expertise");
            args.putString("professionalId", professionalId);
            personalEditionFragment.setArguments(args);
            personalEditionFragment.show(getSupportFragmentManager(), "");

        }

        if (view.getId()==R.id.fab_skill_edit_mode) {

            PersonalEditionFragment personalEditionFragment = new PersonalEditionFragment();
            Bundle args = new Bundle();
            args.putSerializable("personalEditionAdapter", skillListFragment.getAdapter());
            args.putString("layout", "skill");
            args.putString("professionalId", professionalId);
            personalEditionFragment.setArguments(args);
            personalEditionFragment.show(getSupportFragmentManager(), "");

        }

        // Envio al servicio de mail
        if((view.getId() == R.id.profile_detail_email || view.getId() == R.id.profile_detail_email_icon)
                && isValidMail(personalEmail.getText()) && isReadMode){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",personalEmail.getText().toString().trim(), null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "[Jobify] - Mensaje de "+helper.getProfessional().getFullName());
            startActivity(Intent.createChooser(intent, getString(R.string.redirection_mail)));
        }
    }

    public void updateMap(String lat, String lon) {
        //llamo al servicio externo
        InverseGeocodeService addressService = new InverseGeocodeService(this,lat,lon);

        // Pongo el punto en el mapa estatico
        String mapURL="https://maps.googleapis.com/maps/api/staticmap?" +
                "zoom=15" +
                "&size=400x300" +
                "&maptype=roadmap" +
                "&key=AIzaSyAyta4hXfctjxRTFWd2zKKeDCpVe3Qa-1E" +
                "&center="+ lat+','+ lon+
                "&markers=color:blue%7C"+ lat+','+ lon;
        Picasso.with(this).load(mapURL).into(personalMap);
    }

    @Override
    public void onInverseCalculationSuccess(String address) {
        if (!isReadMode) return;
        if (address.isEmpty()) {
            ShowMessage.toastMessage(this,getString(R.string.cannot_inverse_address_calc));
        } else {
            personalAddress.setText(address);
        }
    }

    @Override
    public void onProfilePersonalSuccess(ProfilePersonal personal) {
        this.personal = personal;

        if (afterPersonalSave) {
            this.professionalUpdated = new Professional();
            professionalUpdated.setEmail(personal.getEmail());
            professionalUpdated.setName(personal.getFirstName());
            professionalUpdated.setLastName(personal.getLastName());
            professionalUpdated.setBirthday(personal.getBirthday());
            professionalUpdated.setGender(personal.getGender());
            professionalUpdated.setLat(personal.getLat());
            professionalUpdated.setLon(personal.getLon());
            afterPersonalSave = false;
        }

        if (personal.getBirthday() != null) {
            Integer edad = DateUtils.getEdad(personal.getBirthday());
            toolbarName.setTitle(isContentValid(personal.getFirstName()) + " (" + edad + " años)");
        } else {
            toolbarName.setTitle(isContentValid(personal.getFirstName()));
        }

        personalEmail.setText(isContentValid(personal.getEmail()));
        personalName.setText(isContentValid(personal.getFullName()));
        personalBirthday.setText((personal.getBirthday()==null)? "":isContentValid(DateUtils.formatShortDateArg2(personal.getBirthday())));
        personalGender.setText(getString((isContentValid(personal.getGender()).equals("M"))? R.string.masculino : R.string.femenino));


        //Coloreando el email para la action.
        int colorAccent = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
        personalEmail.setText(isContentValid(personal.getEmail()));
        if (isValidMail(personalEmail.getText())) {
            personalEmailIcon.setColorFilter(colorAccent);
            personalEmail.setTextColor(colorAccent);
        }


        // validacion
        if (personal.getLat().isEmpty() || personal.getLon().isEmpty()) {
            ShowMessage.toastMessage(this,getString(R.string.cannot_display_map));
            return;
        }
        //actualizo mapa
        updateMap(personal.getLat(), personal.getLon());
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
        this.summary = summary;
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
        this.picture = picture;

        if (afterPictureSave) {
            professionalUpdated.setAvatar(picture.getPicture());
            helper.saveProfessional(professionalUpdated);
            afterPictureSave =false;
        }

        if (isContentValid(picture.getPicture()).isEmpty()) {
            Picasso.with(this).load(R.drawable.logo).into(personalPhoto);
        } else {
            profileDetailPhoto.setText(picture.getPicture());
            Picasso.with(this).load(picture.getPicture()).into(personalPhoto);
        }
    }


    // EDITION satisfied
    @Override
    public void onProfilePersonalModificationSuccess() {
        new GetPersonalTask(this).execute(professionalId);
        afterPersonalSave = true; // controla el profesional que manejamos
    }

    @Override
    public void onProfilePictureModificationSuccess() {
        new GetPictureTask(this).execute(professionalId);
    }

    @Override
    public void onProfileSummaryModificationSuccess() {
        new GetSummaryTask(this).execute(professionalId);
        afterPictureSave = true; // controla el profesional que manejamos
    }

    @Override
    public void onProfileExpertiseModificationSuccess() {

    }

    @Override
    public void onProfileSkillModificationSuccess() {

    }
}
