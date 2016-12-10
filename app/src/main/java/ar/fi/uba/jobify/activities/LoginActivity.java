package ar.fi.uba.jobify.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import ar.fi.uba.jobify.domains.ForgotPassword;
import ar.fi.uba.jobify.domains.Register;
import ar.fi.uba.jobify.domains.Token;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.auth.GetForgotPasswordTask;
import ar.fi.uba.jobify.tasks.auth.PostLoginTask;
import ar.fi.uba.jobify.tasks.auth.PostRegistryTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.FieldValidator;
import ar.fi.uba.jobify.utils.LocationHelper;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;


public class LoginActivity extends MustRegistryActivity implements PostLoginTask.ResultLogin,
        GetForgotPasswordTask.ResultForgotPassword {

    private static final int REQUEST_SIGNUP = 0;

    private EditText emailText;
    private EditText passwordText;
    private EditText hostText;
    private Button loginButton;
    private MyPreferences pref = new MyPreferences(this);
    private ProgressDialog progressDialog;
    CallbackManager callbackManager;
    private LoginButton loginButtonFb;
    private String emailFb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_login);
        pref.remove(getString(R.string.shared_pref_current_token));

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
        loginButtonFb.setReadPermissions(Arrays.asList("public_profile,email,user_birthday"));
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
                                    emailFb = object.getString("email");
                                    String birthday = object.has("birthday")? DateUtils.formatFacebook(object.getString("birthday")) : "";
                                    String firstName = object.has("first_name")? object.getString("first_name") : "";
                                    String lastName = object.has("last_name")? object.getString("last_name") : "";
                                    String gender = object.has("gender")? (object.getString("gender").equals("male")? "M" : "F") : "";
                                    String picture= object.has("picture")? object.getJSONObject("picture").has("data") ?
                                            object.getJSONObject("picture").getJSONObject("data").has("url")?
                                            object.getJSONObject("picture").getJSONObject("data").getString("url") : "" : "" : "";
                                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                                    Log.d("FacebookLogin", "[emailfb="+emailFb+",birth="+birthday+",fName="+firstName+"lName="+lastName+",gender="+gender+"faceToken="+refreshedToken+"]");

                                    LocationHelper loc = new LocationHelper();
                                    loc.updatePosition(getApplicationContext());
                                    String lat = pref.get(getString(R.string.shared_pref_current_location_lat), AppSettings.getGpsLat());
                                    String lon = pref.get(getString(R.string.shared_pref_current_location_lon), AppSettings.getGpsLon());
                                    if (RestClient.isOnline(getApplicationContext())) {
                                        new PostRegistryTask(LoginActivity.this).execute(
                                                emailFb, "", firstName, lastName, gender, birthday, lat, lon, refreshedToken, picture);

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    LoginActivity.this.finish();
                                }
                            }

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                emailText.setText("");
                passwordText.setText("");
            }

            @Override
            public void onError(FacebookException error) {
                ShowMessage.toastMessage(getApplicationContext(), error.getMessage());
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
       if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }

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
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
        if (register != null && RestClient.isOnline(this)) {
            new PostLoginTask(this).execute(emailFb,"","FACE");
        }
    }


}
