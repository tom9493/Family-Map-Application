package edu.byu.cs240.FamilyMapClient.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.byu.cs240.FamilyMapClient.R;
import edu.byu.cs240.FamilyMapClient.model.DataCache;
import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private EditText searchBar;
    private ImageView searchClick;
    private ArrayList<Person> people;
    private ArrayList<Event> events;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        people = new ArrayList<>(DataCache.getAllPeople());
        events = new ArrayList<>(DataCache.getFilteredEvents());

        searchBar = (EditText) findViewById(R.id.search_bar);
        searchClick = (ImageView) findViewById(R.id.search_click);
        searchClick.setOnClickListener(v -> { search(); } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        new MenuInflater(this).inflate(R.menu.menu_search, menu);
        return true;
    }

    private class PersonEventAdapter extends RecyclerView.Adapter<PersonEventViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        PersonEventAdapter(ArrayList<Person> people, ArrayList<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public PersonEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new PersonEventViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull PersonEventViewHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class PersonEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView markerOrGender;
        private final TextView nameOrEvent;
        private final TextView associatedPerson;

        private final int viewType;
        private Person person;
        private Event event;

        PersonEventViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                markerOrGender = itemView.findViewById(R.id.imageViewPerson);
                nameOrEvent = itemView.findViewById(R.id.name_person_item);
                associatedPerson = itemView.findViewById(R.id.relationship_person_item);
            } else {
                markerOrGender = itemView.findViewById(R.id.imageViewEvent);
                nameOrEvent = itemView.findViewById(R.id.associatedEvent_event_item);
                associatedPerson = itemView.findViewById(R.id.associatedPerson_event_item);
            }
        }

        private void bind (Person person) {
            this.person = person;
            Drawable genderIcon;

            if (person.getGender().equals("m")) {
                genderIcon = new IconDrawable(SearchActivity.this,
                        FontAwesomeIcons.fa_male).colorRes(R.color.black).sizeDp(80);
            } else {
                genderIcon = new IconDrawable(SearchActivity.this,
                        FontAwesomeIcons.fa_female).colorRes(R.color.purple_200).sizeDp(80);
            }
            markerOrGender.setImageDrawable(genderIcon);

            String name = person.getFirstName() + " " + person.getLastName();
            nameOrEvent.setText(name);

            associatedPerson.setText("");
        }

        private void bind (Event event) {
            this.event = event;
            Person p = DataCache.getPersonById(event.getPersonID());
            markerOrGender.setImageDrawable(new IconDrawable(SearchActivity.this,
                    FontAwesomeIcons.fa_map_marker).colorRes(R.color.black).sizeDp(80));
            String eventDescription = event.getEventType().toUpperCase() + " " + event.getCity() +
                    " " + event.getCountry() + " (" + event.getYear() + ")";
            nameOrEvent.setText(eventDescription);
            String name = p.getFirstName() + " " + p.getLastName();
            associatedPerson.setText(name);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.EXTRA_PERSON_ID, person.getPersonID());
            } else {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                DataCache.setEventActivityEvent(event);
            }
            startActivity(intent);
        }
    }

    public void search() {
        String textGiven = searchBar.getText().toString().toLowerCase();
        ArrayList<Person> searchedPeople = new ArrayList<>();
        ArrayList<Event> searchedEvents = new ArrayList<>();
        for (int i = 0; i < people.size(); ++i) {
            Person p = people.get(i);
            String name = p.getFirstName() + " " + p.getLastName();
            if (name.toLowerCase().contains(textGiven)) { searchedPeople.add(p); }
        }

        for (int i = 0; i < events.size(); ++i) {
            Event e = events.get(i);
            String eventInfo = e.getCountry() + " " + " " + e.getCity() + " " + e.getEventType()
                    + " " + e.getYear();
            if (eventInfo.toLowerCase().contains(textGiven)) { searchedEvents.add(e); }
        }

        ArrayList<Event> sortedEvents = new ArrayList<>();
        ArrayList<Integer> yearList = new ArrayList<>();
        for (int i = 0; i < searchedEvents.size(); ++i) {
            yearList.add(searchedEvents.get(i).getYear());
        }

        List<Integer> newList = new ArrayList<>(yearList);
        Collections.sort(newList);

        for (int i = 0; i < newList.size(); ++i) {
            for (int j = 0; j < searchedEvents.size(); ++j) {
                Event e = searchedEvents.get(j);
                if (e.getYear() == newList.get(i) && !sortedEvents.contains(e)) {
                    sortedEvents.add(e);
                }
            }
        }

        PersonEventAdapter adapter = new PersonEventAdapter(searchedPeople, sortedEvents);
        recyclerView.setAdapter(adapter);
    }
}