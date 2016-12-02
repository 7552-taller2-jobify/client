package ar.fi.uba.jobify.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.iid.FirebaseInstanceId;

import ar.fi.uba.jobify.domains.Register;
import ar.fi.uba.jobify.fragments.DatePickerFragment;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.auth.PostRegistryTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.LocationHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class RegistryActivity extends MustRegistryActivity {

    private static final int REQUEST_SIGNUP = 0;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText repasswordText;
    private EditText birthdayDate;
    private Button joinmeButton;
    private String gender;
    private MyPreferences pref = new MyPreferences(this);
    private ProgressDialog progressDialog;
    private DatePickerFragment birthdayFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_registry_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        pref.remove(getString(R.string.shared_pref_current_token));
        LocationHelper.updatePosition(this.getApplicationContext());

        joinmeButton = (Button) findViewById(R.id.activity_registry_btn_joinme);
        firstNameText = (EditText) findViewById(R.id.activity_registry_input_firstname);
        lastNameText = (EditText) findViewById(R.id.activity_registry_input_lastname);
        emailText = (EditText) findViewById(R.id.activity_registry_input_email);
        passwordText = (EditText) findViewById(R.id.activity_registry_input_password);
        repasswordText = (EditText) findViewById(R.id.activity_registry_input_repassword);
        birthdayDate = (EditText) findViewById(R.id.activity_registry_input_birthday);
        initBirthDate();

        joinmeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                joinme();
            }
        });
    }

    private void initBirthDate() {
        //Calendar c = Calendar.getInstance();
        //c.set(Calendar.YEAR,c.get(Calendar.YEAR)-18);
        //birthdayDate.setText(DateUtils.formatShortDateArg2(c.getTime()));
        birthdayDate.setText("");
    }

    public void showDatePickerDialog(View v) {
        birthdayFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("id", R.id.activity_registry_input_birthday);
        birthdayFragment.setArguments(args);
        birthdayFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.activity_registry_radio_m:
                if (checked) gender = "M";
                    break;
            case R.id.activity_registry_radio_f:
                if (checked) gender = "F";
                    break;
        }
    }

    public void joinme() {

        if (!validate()) {
            onRegistryFailed();
            return;
        }

        progressDialog = new ProgressDialog(RegistryActivity.this,
                R.style.AppTheme_NoActionBar); //FIXME change for dialog
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Datos incorrectos...");
        progressDialog.show();

        joinmeButton.setEnabled(false);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCN TOKEN GET", "Refreshed token: " + refreshedToken);

        if (RestClient.isOnline(this)) {
            String lat = pref.get(getString(R.string.shared_pref_current_location_lat), AppSettings.getGpsLat());
            String lon = pref.get(getString(R.string.shared_pref_current_location_lon), AppSettings.getGpsLon());
            // TODO smpiano ver que hacer con la city
            new PostRegistryTask(this).execute(
                    emailText.getText().toString(), passwordText.getText().toString(), firstNameText.getText().toString(),
                    lastNameText.getText().toString(), gender, birthdayDate.getText().toString(), lat, lon, refreshedToken);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        //moveTaskToBack(true);
    }

    public void onRegistrySuccess(Register register) {
        joinmeButton.setEnabled(true);
        progressDialog.dismiss();
        if (register == null) {
            firstNameText.setText("");
            lastNameText.setText("");
            emailText.setText("");
            passwordText.setText("");
            repasswordText.setText("");
            ((RadioButton) findViewById(R.id.activity_registry_radio_m)).setChecked(false);
            ((RadioButton) findViewById(R.id.activity_registry_radio_f)).setChecked(false);
            gender = "";
            initBirthDate();

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void onRegistryFailed() {
        ShowMessage.showSnackbarSimpleMessage(this.getCurrentFocus(), "Datos incompletos");
        joinmeButton.setEnabled(true);
        if (progressDialog!=null) progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String firstName = emailText.getText().toString();
        String lastName = emailText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Ingrese un email válido!");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("Ingrese un contraseña!");
            valid = false;
        } else {
            if (passwordText.getText().toString().equals(repasswordText.getText().toString())) {
                passwordText.setError(null);
            } else {
                passwordText.setError("Deben coincidir las contraseñas");
                valid = false;
            }
        }

        return valid;
    }

}
