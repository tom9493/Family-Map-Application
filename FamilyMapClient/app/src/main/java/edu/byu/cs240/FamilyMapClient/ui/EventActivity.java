package edu.byu.cs240.FamilyMapClient.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import edu.byu.cs240.FamilyMapClient.R;

public class EventActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private final FragmentManager fm = this.getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mapFragment = new MapFragment();
        fm.beginTransaction()
                .add(R.id.fragmentContainerEvent, mapFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        new MenuInflater(this).inflate(R.menu.menu_event, menu);
        return true;
    }
}