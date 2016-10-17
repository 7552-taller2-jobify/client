package ar.fi.uba.jobify.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.auth.PostAuthTask;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.soldme.R;


public class LoginActivity extends AppCompatActivity implements PostAuthTask.ResultLogin {

    private static final int REQUEST_SIGNUP = 0;

    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private MyPreferences pref = new MyPreferences(this);
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref.remove(getString(R.string.shared_pref_current_token));
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginButton = (Button) findViewById(R.id.activity_login_btn_login);
        emailText = (EditText) findViewById(R.id.activity_login_input_email);
        passwordText = (EditText) findViewById(R.id.activity_login_input_password);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_NoActionBar); //FIXME change for dialog
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Datos incorrectos...");
        progressDialog.show();

        loginButton.setEnabled(false);
        if (RestClient.isOnline(this)) new PostAuthTask(this).execute(emailText.getText().toString(), passwordText.getText().toString());
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

    public void onLoginSuccess(Token token) {
        findViewById(R.id.activity_login_btn_login).setEnabled(true);
        progressDialog.dismiss();
        if (token == null) {
            emailText.setText("");
            passwordText.setText("");
        } else {
            MyPreferenceHelper helper = new MyPreferenceHelper(this);
            helper.saveSeller(token.getProfessional());
            pref.save(getString(R.string.shared_pref_current_token),token.getToken());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onLoginFailed() {
        ShowMessage.showSnackbarSimpleMessage(this.getCurrentFocus(), "Datos incompletos");
        findViewById(R.id.activity_login_btn_login).setEnabled(true);
        if (progressDialog!=null) progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Ingrese un email válido!");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("Ingrese un password!");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

}
