package edu.byu.cs240.FamilyMapClient.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import edu.byu.cs240.FamilyMapClient.R;
import edu.byu.cs240.FamilyMapClient.model.DataCache;
import edu.byu.cs240.FamilyMapClient.model.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    private SwitchCompat lifeStorySwitch;
    private SwitchCompat familyTreeSwitch;
    private SwitchCompat spouseSwitch;
    private SwitchCompat fatherSwitch;
    private SwitchCompat motherSwitch;
    private SwitchCompat maleSwitch;
    private SwitchCompat femaleSwitch;
    private LinearLayout logoutLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = DataCache.getSettings();

        lifeStorySwitch = (SwitchCompat) findViewById(R.id.life_story_switch);
        familyTreeSwitch = (SwitchCompat) findViewById(R.id.family_tree_switch);
        spouseSwitch = (SwitchCompat) findViewById(R.id.spouse_switch);
        fatherSwitch = (SwitchCompat) findViewById(R.id.father_switch);
        motherSwitch = (SwitchCompat) findViewById(R.id.mother_switch);
        maleSwitch = (SwitchCompat) findViewById(R.id.male_switch);
        femaleSwitch = (SwitchCompat) findViewById(R.id.female_switch);

        setSwitches();

        lifeStorySwitch.setOnCheckedChangeListener(getListener());
        familyTreeSwitch.setOnCheckedChangeListener(getListener());
        spouseSwitch.setOnCheckedChangeListener(getListener());
        fatherSwitch.setOnCheckedChangeListener(getListener());
        motherSwitch.setOnCheckedChangeListener(getListener());
        maleSwitch.setOnCheckedChangeListener(getListener());
        femaleSwitch.setOnCheckedChangeListener(getListener());

        logoutLL = (LinearLayout) findViewById(R.id.logout_ll);
        logoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.loggedIn = 0;
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        new MenuInflater(this).inflate(R.menu.menu_settings, menu);
        return true;
    }

    private CompoundButton.OnCheckedChangeListener getListener() {
        return (buttonView, isChecked) -> {
            settings.setStoryLines(lifeStorySwitch.isChecked());
            settings.setFamilyTree(familyTreeSwitch.isChecked());
            settings.setSpouse(spouseSwitch.isChecked());
            settings.setFather(fatherSwitch.isChecked());
            settings.setMother(motherSwitch.isChecked());
            settings.setMale(maleSwitch.isChecked());
            settings.setFemale(femaleSwitch.isChecked());
            setSettingsResult();
            DataCache.makeFilteredEvents();
            MapFragment.mMap.clear();
            MapFragment.addEventMarkers();
        };
    }
    
    private void setSwitches() {
        if (settings.isStoryLines()) { lifeStorySwitch.setChecked(true); }
        if (settings.isFamilyTree()) { familyTreeSwitch.setChecked(true); }
        if (settings.isSpouse()) { spouseSwitch.setChecked(true); }
        if (settings.isFather()) { fatherSwitch.setChecked(true); }
        if (settings.isMother()) { motherSwitch.setChecked(true); }
        if (settings.isMale()) { maleSwitch.setChecked(true); }
        if (settings.isFemale()) { femaleSwitch.setChecked(true); }
    }

    private void setSettingsResult() { DataCache.setSettings(settings);  }
}