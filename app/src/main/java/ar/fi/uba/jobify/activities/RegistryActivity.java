package ar.fi.uba.jobify.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.iid.FirebaseInstanceId;

import ar.fi.uba.jobify.domains.Register;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.auth.PostAuthTask;
import ar.fi.uba.jobify.tasks.registry.PostRegistryTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class RegistryActivity extends AppCompatActivity implements PostRegistryTask.ResultRegistry {

    private static final int REQUEST_SIGNUP = 0;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText repasswordText;
    private DatePicker birthdayDate;
    private RadioButton genderMale;
    private Button joinmeButton;
    private String gender;
    private MyPreferences pref = new MyPreferences(this);
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        pref.remove(getString(R.string.shared_pref_current_token));

        joinmeButton = (Button) findViewById(R.id.activity_registry_btn_joinme);
        firstNameText = (EditText) findViewById(R.id.activity_registry_input_firstname);
        lastNameText = (EditText) findViewById(R.id.activity_registry_input_lastname);
        emailText = (EditText) findViewById(R.id.activity_registry_input_email);
        passwordText = (EditText) findViewById(R.id.activity_registry_input_password);
        repasswordText = (EditText) findViewById(R.id.activity_registry_input_repassword);
        birthdayDate = (DatePicker) findViewById(R.id.activity_registry_input_birthday);

        joinmeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                joinme();
            }
        });
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

        String birthday = birthdayDate.getDayOfMonth()+"/"+birthdayDate.getMonth()+"/"+birthdayDate.getYear();

        //Obtengo el token para probar
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCN TOKEN GET", "Refreshed token: " + refreshedToken);

        if (RestClient.isOnline(this)) new PostRegistryTask(this).execute(
                emailText.getText().toString(), passwordText.getText().toString(), firstNameText.getText().toString(),
                lastNameText.getText().toString(), gender, birthday, "1","2","cityexample", refreshedToken);
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
