package ar.fi.uba.jobify.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import ar.fi.uba.jobify.domains.Professional;
import ar.fi.uba.jobify.server.RestClient;
import ar.fi.uba.jobify.tasks.auth.DeleteLogoutTask;
import ar.fi.uba.jobify.utils.AppSettings;
import ar.fi.uba.jobify.utils.CircleTransform;
import ar.fi.uba.jobify.utils.MyPreferenceHelper;
import ar.fi.uba.jobify.utils.MyPreferences;
import ar.fi.uba.jobify.utils.ShowMessage;
import fi.uba.ar.jobify.R;

import static ar.fi.uba.jobify.utils.FieldValidator.isContentValid;

public class MainActivity extends AppCompatActivity implements DeleteLogoutTask.Logout{

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final MyPreferenceHelper helper = new MyPreferenceHelper(MainActivity.this);
        final MyPreferences pref = new MyPreferences(this);
        if (helper.getProfessional() == null || pref.get(getString(R.string.shared_pref_current_token),"").isEmpty()) {
            openLoginActivity(drawerLayout);
            return;
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            // FIXME:
            // Es para poner el nombre del vendedor en el menu lateral. Lo pone con delay.
            // Si lo pongo directamente en el onCreate, explota por null
            @Override
            public void onDrawerOpened(View drawerView) {
                Professional professional = helper.getProfessional();

                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                TextView sellerName = ((TextView) navigationView.findViewById(R.id.nav_header_main_vendor_name));
                TextView sellerEmail = ((TextView) navigationView.findViewById(R.id.nav_header_main_vendor_email));

                if (sellerName != null) {
                    String fullSellerName = professional.getFullName();
                    sellerName.setText(fullSellerName);
                    sellerEmail.setText(professional.getEmail());
                    String avatar = isContentValid(professional.getAvatar());
                    ImageView mainAvatar = (ImageView) findViewById(R.id.menu_header_logo);
                    if (avatar != null && !avatar.isEmpty()) {
                        Picasso.with(MainActivity.this).load(avatar).transform(new CircleTransform()).into(mainAvatar);
                    } else {
                        Picasso.with(MainActivity.this).load(R.drawable.icon).into(mainAvatar);
                    }
                }
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        //Tomo los ultimos datos, si no tengo nada tomo los hardcoded.
        String lat = pref.get(getString(R.string.shared_pref_current_location_lat), AppSettings.getGpsLat());
        String lon = pref.get(getString(R.string.shared_pref_current_location_lon), AppSettings.getGpsLon());
        if (lat.equals(AppSettings.getGpsLat()) && lon.equals(AppSettings.getGpsLon())) {
            debugCardMessage(null);
        } else {
            Location l = new Location("");
            l.reset();
            l.setLatitude(Double.valueOf(lat));
            l.setLongitude(Double.valueOf(lon));
            debugCardMessage(l);
        }

    }

    public void showSnackbarSimpleMessage(String message){
        ShowMessage.showSnackbarSimpleMessage(this.getCurrentFocus(), message);
    }

    private void debugCardMessage(Location loc) {
        String position = "POS HARD";
        if (loc != null) {
            position = "POS lat="+loc.getLatitude()+" lon="+ loc.getLongitude();
        }
        ((TextView) findViewById(R.id.fragment_main_vendor_name)).setText(position);
    }

    public void openMyClientsActivity(View view) {
        Intent intent = new Intent(this, MyContactsActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void openSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openLogoutActivity(View view) {
        ShowMessage.toastMessage(getApplicationContext(), getString(R.string.goodby_message));
        if (RestClient.isOnline(this)) {
            String email = (new MyPreferenceHelper(this)).getProfessional().getEmail();
            String token = (new MyPreferences(this)).get(getString(R.string.shared_pref_current_token),"");
            new DeleteLogoutTask(this).execute(email, token);
        }
        onLogoutSuccess();
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.nav_contacts:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                openMyClientsActivity(null);
                                return true;
                            case R.id.nav_login:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                openLoginActivity(null);
                                return true;
                            case R.id.nav_profile:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                openProfileActivity(null);
                                return true;
                            case R.id.nav_search:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                openSearchActivity(null);
                                return true;
                            case R.id.nav_logout:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                openLogoutActivity(null);
                                return true;
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onLogoutSuccess() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
