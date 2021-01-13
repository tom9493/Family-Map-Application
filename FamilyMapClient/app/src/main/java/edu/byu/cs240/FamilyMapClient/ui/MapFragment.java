package edu.byu.cs240.FamilyMapClient.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import edu.byu.cs240.FamilyMapClient.R;
import edu.byu.cs240.FamilyMapClient.model.DataCache;
import model.Event;
import model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static String ARG_EVENT_ID = "event-id";
    private static String ARG_CAMERA_POS = "camera-pos";

    public static GoogleMap mMap;
    private Event selectedEvent;
    public static Map<Marker, Event> markersToEvents = new HashMap<>();
    private ArrayList<Polyline> lines = new ArrayList<>();

    private ImageView genderImageView;
    private TextView personNameTextView;
    private TextView eventDetailsTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        genderImageView = (ImageView)view.findViewById(R.id.mapImageView);
        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                colorRes(R.color.black).sizeDp(80);
        genderImageView.setImageDrawable(genderIcon);
        setEventInfoClickListener(genderImageView);

        personNameTextView = (TextView)view.findViewById(R.id.associatedPerson);
        setEventInfoClickListener(personNameTextView);

        eventDetailsTextView = (TextView)view.findViewById(R.id.associatedEvent);
        setEventInfoClickListener(eventDetailsTextView);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);
        mMap.setOnMarkerClickListener(markerClickListener);

        addEventMarkers();
        if (DataCache.getEventActivityEvent() != null) {
            setCameraToEvent(DataCache.getEventActivityEvent());
        }
    }

    public static void addEventMarkers() {
        markersToEvents.clear();
        Person p = DataCache.getUser();
        ArrayList<Event> events = new ArrayList<>(DataCache.getFilteredEvents());
        for (int i = 0; i < events.size(); ++i) {
            Event event = events.get(i);
            Person person = DataCache.getPerson(event);
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(person
                    .getFirstName() + " " + person.getLastName() + "'s " + event.getEventType() +
                    " event").icon(BitmapDescriptorFactory.defaultMarker(
                            DataCache.getEventTypeColors().get(event.getEventType()))));
            markersToEvents.put(marker, event);
            if (event.getPersonID().equals(p.getPersonID())) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location)); }
        }
    }

    private final GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            removeLines();
            selectedEvent = markersToEvents.get(marker);
            Person person = DataCache.getPerson(selectedEvent);

            String fullName = person.getFirstName() + " " + person.getLastName();
            String eventDetails = selectedEvent.getEventType().toUpperCase() + ": " + selectedEvent
                    .getCity() + ", " + selectedEvent.getCountry() + " (" + selectedEvent.getYear()
                    + ")";

            personNameTextView.setText(fullName.toCharArray(), 0, fullName.length());
            eventDetailsTextView.setText(eventDetails.toCharArray(), 0, eventDetails.length());

            if (person.getGender().equals("m")) {
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.black).sizeDp(80);
                genderImageView.setImageDrawable(genderIcon);
            }
            else {
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.purple_200).sizeDp(80);
                genderImageView.setImageDrawable(genderIcon);
            }

            drawPolyLines(selectedEvent);
            return true;
        }
    };

    private final View.OnClickListener eventInfoClickListener = (v) -> {
        if (selectedEvent != null) {
            Person person = DataCache.getPersonById(selectedEvent.getPersonID());
            Intent intent = new Intent(getActivity(), PersonActivity.class);
            intent.putExtra(PersonActivity.EXTRA_PERSON_ID, person.getPersonID());
            startActivity(intent);
        }
    };

    public void setEventInfoClickListener(View view) {
        view.setOnClickListener(eventInfoClickListener);
    }

    public void setCameraToEvent(Event event) {
        LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

       Marker marker = null;
       for (Map.Entry<Marker, Event> entry : markersToEvents.entrySet()) {
            if (entry.getValue() == event) { marker = entry.getKey(); }
        }
        markerClickListener.onMarkerClick(marker);
        DataCache.setEventActivityEvent(null);
    }

    public void drawPolyLines(Event e) {
        Person associatedPerson = DataCache.getPersonById(e.getPersonID());
        Map<String, ArrayList<Event>> m = DataCache.getPersonEvents();
        if (DataCache.getSettings().isSpouse() && associatedPerson.getSpouseID() != null) {
            Event spouseEvent = Objects.requireNonNull(m.get(associatedPerson.getSpouseID())).get(0);
            LatLng selectedEventLatLng = new LatLng(e.getLatitude(), e.getLongitude());
            LatLng spouseEventLatLng = new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude());
            Polyline spousePolyline = mMap.addPolyline(new PolylineOptions().add(
                    selectedEventLatLng,
                    spouseEventLatLng)
            );
            spousePolyline.setWidth(25f);
            spousePolyline.setColor(Color.RED);
            lines.add(spousePolyline);
        }

        if (DataCache.getSettings().isStoryLines()) {
            ArrayList<Event> personEvents = m.get(e.getPersonID());
            ArrayList<LatLng> locations = new ArrayList<>();
            for (int i = 0; i < personEvents.size(); ++i) {
                LatLng newLocation = new LatLng(personEvents.get(i).getLatitude(),
                        personEvents.get(i).getLongitude());
                locations.add(newLocation);
            }
            Polyline lifeStoryLine = mMap.addPolyline(new PolylineOptions().addAll(locations));
            lifeStoryLine.setWidth(20f);
            lifeStoryLine.setColor(Color.BLUE);
            lines.add(lifeStoryLine);
        }

        if (DataCache.getSettings().isFamilyTree()) {
            Float f = 40f;
            makeFamilyTreeLines(e, f, m);
        }
    }

    public void makeFamilyTreeLines(Event e, Float f, Map<String, ArrayList<Event>> m) {
        Person p = DataCache.getPersonById(e.getPersonID());
        LatLng eventLocation = new LatLng(e.getLatitude(), e.getLongitude());
        if (p.getFatherID() != null) {
            Event fe = Objects.requireNonNull(m.get(p.getFatherID())).get(0);
            LatLng newLocation = new LatLng(fe.getLatitude(), fe.getLongitude());
            Polyline newLine = mMap.addPolyline(new PolylineOptions()
                    .add(eventLocation).add(newLocation));
            newLine.setWidth(f);
            newLine.setColor(Color.YELLOW);
            lines.add(newLine);
            Float newFloat = f/2;
            makeFamilyTreeLines(fe, newFloat, m);
        }

        if (p.getMotherID() != null) {
            Event me = Objects.requireNonNull(m.get(p.getMotherID())).get(0);
            LatLng newLocation = new LatLng(me.getLatitude(), me.getLongitude());
            Polyline newLine = mMap.addPolyline(new PolylineOptions()
                    .add(eventLocation).add(newLocation));
            newLine.setWidth(f);
            newLine.setColor(Color.YELLOW);
            lines.add(newLine);
            Float newFloat = f/2;
            makeFamilyTreeLines(me, newFloat, m);
        }
    }

    public void removeLines() {
        for (int i = 0; i < lines.size(); ++i) {
            lines.get(i).remove();
        }
        lines.clear();
    }

    @Override
    public void onMapLoaded() {}
}