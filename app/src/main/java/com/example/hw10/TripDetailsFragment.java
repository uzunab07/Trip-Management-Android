package com.example.hw10;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GnssAntennaInfo;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripDetailsFragment extends Fragment implements GoogleMap.OnMapClickListener,GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    GoogleMap mMap;
    Trip trip;
    Double distance;
    Button markDone;
    MyLocation location1;
    TextView textViewTitle, textViewDebut, textViewEnd, textViewStatus, textViewDistance;
    LatLngBounds.Builder builder;
    private FusedLocationProviderClient fusedLocationClient;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Trip mytrip;
    private String id;


    public TripDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 1.
     * @return A new instance of fragment TripDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripDetailsFragment newInstance(Trip param1,String param2) {
        TripDetailsFragment fragment = new TripDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2,param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (getArguments() != null) {
            mytrip = (Trip) getArguments().getSerializable(ARG_PARAM1);
            id = (String) getArguments().get(ARG_PARAM2);
            this.trip = mytrip;
            LatLng latLng = new LatLng(trip.start.Lat, trip.start.Long);
            builder = new LatLngBounds.Builder();
            builder.include(latLng);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip_details, container, false);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewDebut = view.findViewById(R.id.textViewDebut);
        textViewEnd = view.findViewById(R.id.textViewEnd);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewDistance = view.findViewById(R.id.textViewDistance);
        markDone = view.findViewById(R.id.btnCompleteTrigger);

        markDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Trip Completed", Toast.LENGTH_SHORT).show();
                getCurrentLocation();


            }
        });


        if (trip.getStatus() != "Complete") {
            setLayout();
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {

                    mMap = googleMap;
                    UiSettings settings = googleMap.getUiSettings();
                    settings.setZoomControlsEnabled(true);
                    settings.setCompassEnabled(true);

                    googleMap.setOnMapClickListener(TripDetailsFragment.this::onMapClick);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setOnMyLocationButtonClickListener(TripDetailsFragment.this::onMyLocationButtonClick);
                    googleMap.setOnMyLocationClickListener(TripDetailsFragment.this::onMyLocationClick);
                    LatLng latLng = new LatLng(trip.start.Lat, trip.start.Long);
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker in Start"));


                }
            });
        } else {
            setLayout();
        }
        return view;
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {

        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(5000)
                .setMaxUpdateAgeMillis(0)
                .build();
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(currentLocationRequest, cancellationTokenSource.getToken()).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    location1 = new MyLocation(location.getLatitude(), location.getLongitude());
                    trip.setFinish(location1);
                    LatLng latLngCurrent = new LatLng(trip.finish.Lat, trip.finish.Long);
                    LatLng latLngOld = new LatLng(trip.start.Lat, trip.start.Long);


                    distance = SphericalUtil.computeDistanceBetween(latLngOld, latLngCurrent);
                    trip.setEnd(getTime());
                    trip.setDistance(distance * 0.000621371);
                    trip.setStatus("Complete");
                    textViewStatus.setText("Complete");
                    textViewStatus.setTextColor(Color.parseColor("#04D519"));
                    textViewEnd.setText("Completed At:" + trip.getEnd());
                    markDone.setVisibility(View.INVISIBLE);
                    textViewDistance.setVisibility(View.VISIBLE);
                    textViewDistance.setText(String.format("%2f", distance * 0.000621371) + " Miles");

                    builder.include(latLngCurrent);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Marker in Current Position"));


                    //Update Trip in Database
                    sendData();
                } else {
                    Log.d("TAG", "onComplete: " + task.getException().getMessage());
                }
            }
        });
    }


    public String getTime() {
        DateFormat now = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String date = now.format(new Date()).toString();
        return date;
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        MyLocation loc = new MyLocation(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker in Current Position"));


    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    public void setLayout() {
        if (trip.getStatus().equals("Complete")) {
            textViewStatus.setText("Complete");
            textViewStatus.setTextColor(Color.parseColor("#04D519"));
        } else if (trip.getStatus().equals("On Going")) {
            textViewStatus.setText("On Going");
            textViewStatus.setTextColor(Color.parseColor("#FB7500"));
        }
        if (trip.getEnd() == null) {
            textViewEnd.setText("Completed At: N/A");
        } else {
            textViewEnd.setText("Completed At:" + trip.getEnd());
        }
        textViewDebut.setText("Started At:" + trip.getDebut());
        textViewTitle.setText(trip.getName());

        if (trip.getDistance() != 0.0) {
            markDone.setVisibility(View.INVISIBLE);
            textViewDistance.setVisibility(View.VISIBLE);
            textViewDistance.setText(trip.getDistance() + " Miles");
        }
    }

    public void sendData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> trip = new HashMap<>();

        trip.put("end", this.trip.getEnd());
        trip.put("distance", this.trip.getDistance());
        trip.put("finish", this.trip.getFinish());
        trip.put("status", this.trip.getStatus());

        Log.d("TIG", "sendData: "+id);

        db.collection("trips")
                .document(id)
                .update(trip)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TIG", "onSuccess: It worked");
                    }
                });
    }
}