package edu.byu.cs240.FamilyMapClient.ui;

import androidx.appcompat.app.AppCompatActivity;
import edu.byu.cs240.FamilyMapClient.R;
import edu.byu.cs240.FamilyMapClient.model.DataCache;
import model.Event;
import model.Person;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.util.ArrayList;

public class PersonActivity extends AppCompatActivity {

    public static final String EXTRA_PERSON_ID = "PERSON";

    private TextView firstName;
    private TextView lastName;
    private TextView gender;
    private ExpandableListView expandableListView;
    private Person person;

    public Person getPerson() { return person; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Bundle extra = getIntent().getExtras();
        person = DataCache.getPersonById(extra.getString(EXTRA_PERSON_ID));

        firstName = findViewById(R.id.person_activity_fn);
        lastName = findViewById(R.id.person_activity_ln);
        gender = findViewById(R.id.person_activity_g);
        expandableListView = findViewById(R.id.person_activity_elv);

        String fn = person.getFirstName();
        String ln = person.getLastName();
        String g;

        if (person.getGender().equals("m")) { g = "Male"; }
        else { g = "Female"; }

        firstName.setText(fn.toCharArray(), 0, fn.length());
        lastName.setText(ln.toCharArray(), 0, ln.length());
        gender.setText(g.toCharArray(), 0, g.length());

        ArrayList<Person> people = DataCache.fillPeopleArray(person);
        ArrayList<Event> events = DataCache.getPersonEvents(person);

        PersonEventAdapter adapter = new PersonEventAdapter(people, events);
        expandableListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        new MenuInflater(this).inflate(R.menu.menu_person, menu);
        return true;
    }

    private class PersonEventAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;

        private final ArrayList<Person> people;
        private final ArrayList<Event> events;

        PersonEventAdapter(ArrayList<Person> people, ArrayList<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return people.size();
                case EVENT_GROUP_POSITION:
                    return events.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: "
                            + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return getString(R.string.family_title);
                case EVENT_GROUP_POSITION:
                    return getString(R.string.events_title);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: "
                            + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return people.get(childPosition);
                case EVENT_GROUP_POSITION:
                    return events.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: "
                            + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        @Override
        public boolean hasStableIds() { return false; }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    titleView.setText(R.string.family_title);
                    break;
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.events_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return itemView;
        }

        private void initializePersonView(View personItemView, int childPosition) {
            Person p = people.get(childPosition);

            Drawable genderIcon;
            ImageView gender = personItemView.findViewById(R.id.imageViewPerson);
            if (p.getGender().equals("m")) {
                genderIcon = new IconDrawable(PersonActivity.this,
                        FontAwesomeIcons.fa_male).colorRes(R.color.black).sizeDp(60);
            } else {
                genderIcon = new IconDrawable(PersonActivity.this,
                        FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(60);
            }
            gender.setImageDrawable(genderIcon);

            String name = p.getFirstName() + " " + p.getLastName();
            TextView nameView = personItemView.findViewById(R.id.name_person_item);
            nameView.setText(name);

            String relationship = DataCache.getRelationship(p, getPerson());
            TextView relationshipView = personItemView.findViewById(R.id.relationship_person_item);
            relationshipView.setText(relationship);

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra(PersonActivity.EXTRA_PERSON_ID, p.getPersonID());
                    startActivity(intent);
                }
            });
        }

        private void initializeEventView(View eventItemView, int childPosition) {
            Event e = events.get(childPosition);

            Drawable markerIcon = new IconDrawable(PersonActivity.this,
                    FontAwesomeIcons.fa_map_marker).colorRes(R.color.black).sizeDp(60);
            ImageView marker = eventItemView.findViewById(R.id.imageViewEvent);
            marker.setImageDrawable(markerIcon);

            String eventDetails = e.getEventType().toUpperCase() + ": " + e
                    .getCity() + ", " + e.getCountry() + " (" + e.getYear()
                    + ")";
            TextView detailsView = eventItemView.findViewById(R.id.associatedEvent_event_item);
            detailsView.setText(eventDetails);

            Person p = DataCache.getPersonById(e.getPersonID());
            String name = p.getFirstName() + " " + p.getLastName();
            TextView personView = eventItemView.findViewById(R.id.associatedPerson_event_item);
            personView.setText(name);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(PersonActivity.this, EventActivity.class);
                    DataCache.setEventActivityEvent(e);
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

}