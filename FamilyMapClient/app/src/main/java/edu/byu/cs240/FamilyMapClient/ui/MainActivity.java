package edu.byu.cs240.FamilyMapClient.ui;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import edu.byu.cs240.FamilyMapClient.R;
import edu.byu.cs240.FamilyMapClient.model.DataCache;

public class MainActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private final MapFragment mapFragment = new MapFragment();
    private final FragmentManager fm = this.getSupportFragmentManager();
    public static int loggedIn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        loginFragment = new LoginFragment(this);
        fm.beginTransaction().add(R.id.fragmentContainerMain, loginFragment).commit();
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        if (loggedIn == 0) {
            setLoginFragment();
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (loggedIn != 0) {
            super.onCreateOptionsMenu(menu);
            new MenuInflater(this).inflate(R.menu.menu_main, menu);
        } else {
            super.onCreateOptionsMenu(menu);
            new MenuInflater(this).inflate(R.menu.menu_main_login, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().toString().equals("Settings")) {
            Intent intent;
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else {
            Intent intent;
            intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void setMapsFragment() {
        loggedIn = 1;
        String firstName = DataCache.getUser().getFirstName();
        String lastName = DataCache.getUser().getLastName();
        Toast.makeText(this, "Hello " + firstName + " " + lastName,
                Toast.LENGTH_SHORT).show();

        invalidateOptionsMenu();
        fm.beginTransaction()
                .remove(loginFragment)
                .commit();
        fm.beginTransaction()
                .add(R.id.fragmentContainerMain, mapFragment)
                .commit();
    }

    public void setLoginFragment() {
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .remove(mapFragment)
                .commit();
        fm.beginTransaction()
                .add(R.id.fragmentContainerMain, new LoginFragment(this))
                .commit();
    }

    public void failToastLogin() {
        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
    }

    public void failToastRegister() {
        Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show();
    }
}