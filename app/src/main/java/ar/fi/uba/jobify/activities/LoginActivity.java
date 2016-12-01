package ar.fi.uba.jobify.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ar.fi.uba.jobify.domains.ForgotPassword;
import ar.fi.uba.jobify.domains.Register;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.auth.GetForgotPasswordTask;
import ar.fi.uba.jobify.tasks.auth.PostLoginTask;
import ar.fi.uba.jobify.tasks.auth.PostRegistryTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.FieldValidator;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements PostLoginTask.ResultLogin,GetForgotPasswordTask.ResultForgotPassword, PostRegistryTask.ResultRegistry {

    private static final int REQUEST_SIGNUP = 0;

    private EditText emailText;
    private EditText passwordText;
    private EditText hostText;
    private Button loginButton;
    private MyPreferences pref = new MyPreferences(this);
    private ProgressDialog progressDialog;
    CallbackManager callbackManager;
    private LoginButton loginButtonFb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);
        pref.remove(getString(R.string.shared_pref_current_token));
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginButton = (Button) findViewById(R.id.activity_login_btn_login);
        emailText = (EditText) findViewById(R.id.activity_login_input_email);
        hostText = (EditText) findViewById(R.id.activity_login_host);
        hostText.setText(AppSettings.getHost());
        passwordText = (EditText) findViewById(R.id.activity_login_input_password);
        loginButtonFb = (LoginButton) findViewById(R.id.login_button_fb);


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        loginButtonFb.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        callbackManager = CallbackManager.Factory.create();

        loginButtonFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult){
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String email = object.getString("email");
                                    String birthday = object.getString("birthday");
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    Log.d("email", email);
                                    Log.d("birthday", birthday);
                                    Log.d("id", id);
                                    Log.d("name", name);
                                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                    Log.d("FCN TOKEN GET", "Refreshed token: " + refreshedToken);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

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
        if (RestClient.isOnline(this)) {
            AppSettings.setServerHost(hostText.getText().toString());
            new PostLoginTask(this).execute(emailText.getText().toString(), passwordText.getText().toString());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
        */
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        //moveTaskToBack(true);
    }

    @Override
    public void onForgotPasswordSuccess(ForgotPassword forgotPassword) {
        if (forgotPassword != null) {
            passwordText.setText(forgotPassword.getPass());
            ShowMessage.toastMessage(getApplicationContext(),"Su constraseña fue reiniciada por ["+forgotPassword.getPass()+"]");
        }
    }

    public void onLoginSuccess(Token token) {
        findViewById(R.id.activity_login_btn_login).setEnabled(true);
        progressDialog.dismiss();
        if (token == null) {
            emailText.setText("");
            passwordText.setText("");
        } else {
            MyPreferenceHelper helper = new MyPreferenceHelper(this);
            helper.saveProfessional(token.getProfessional());
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

        if (!FieldValidator.isIPValid(hostText.getText().toString())) {
            hostText.setError("Ingrese una IPv4 válida!");
            valid = false;
        } else {
            hostText.setError(null);
        }

        return valid;
    }

    public void registry(View view) {
        Intent intent = new Intent(this, RegistryActivity.class);
        startActivity(intent);
    }

    public void onClickForgotPass(View view) {
        String email = emailText.getText().toString();
        boolean valid = true;
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Ingrese un email válido!");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (RestClient.isOnline(this) && valid) {
            AppSettings.setServerHost(hostText.getText().toString());
            new GetForgotPasswordTask(this).execute(email);
        }
    }

    @Override
    public void onRegistrySuccess(Register register) {

        if (register == null) {
            Log.d("OnRegistrySucces","register == null");

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
