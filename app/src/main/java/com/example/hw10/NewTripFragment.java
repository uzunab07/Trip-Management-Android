package com.example.hw10;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NewTripFragment extends Fragment {

    public NewTripFragment() {
        // Required empty public constructor
    }

    TextView textViewCurrentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    MyLocation location1;
    String loading = "Loading....",success = "Success";
    EditText editTextTripName;
    Trip trip = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasLocationPermission()){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            getCurrentLocation();
        }else{
            multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Create Trip");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new, container, false);

        editTextTripName = view.findViewById(R.id.editTextTripName);
        textViewCurrentLocation = view.findViewById(R.id.textViewCurrentLocation);
        textViewCurrentLocation.setText(" "+loading);
        textViewCurrentLocation.setTextColor(getResources().getColor(R.color.orange));

        view.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClickCurrentLoaction: "+textViewCurrentLocation.getText());
                String tripName = editTextTripName.getText().toString();
                if(tripName.isEmpty()) {
                    Toast.makeText(getActivity(), "Please insert the trip Name", Toast.LENGTH_SHORT).show();
                }else {

//                    trip  = new Trip(tripName,getTime(),getCurrentLocation(),"On Going");
                    trip  = new Trip(tripName,getTime(),"","On Going",0.0,getCurrentLocation(),new MyLocation());
                    Log.d("demo", "onClick:NewTrip "+trip);
                    sendData();
                    mlisten.goToBack();
                }

            }
        });



        return view;
    }
    @SuppressLint("MissingPermission")
    public MyLocation getCurrentLocation(){

        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(5000)
                .setMaxUpdateAgeMillis(10000)
                .build();
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(currentLocationRequest,cancellationTokenSource.getToken()).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    location1 = new MyLocation(location.getLatitude(),location.getLongitude());
                    textViewCurrentLocation.setText(" "+success);
                    textViewCurrentLocation.setTextColor(Color.parseColor("#04D519"));
                }else{
                    Log.d("TAGir", "onComplete: "+task.getException().getMessage());
                }
            }
        });

        return location1;
    }

    private boolean hasLocationPermission(){

        return   getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)  == PackageManager.PERMISSION_GRANTED;
    }

    private ActivityResultLauncher<String []> multiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Log.d("TAG", "onActivityResult: "+result);
        }
    });

    public String getTime (){
        DateFormat now= new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String date = now.format(new Date()).toString();
        return  date;
    }

    mListener mlisten;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlisten = (mListener) context;
    }

    public interface mListener{
        void goToBack();
    }

    public  void sendData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String,Object> trip = new HashMap<>();

        trip.put("name",this.trip.getName());
        trip.put("debut",this.trip.getDebut());
        trip.put("start",this.trip.getStart());
        trip.put("status",this.trip.getStatus());
        trip.put("end", this.trip.getEnd());
        trip.put("distance", this.trip.getDistance());
        trip.put("finish", this.trip.getFinish());
        trip.put("status", this.trip.getStatus());

        db.collection("trips")
                .add(trip)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });
    }
}