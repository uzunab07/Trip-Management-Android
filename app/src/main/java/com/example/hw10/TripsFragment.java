package com.example.hw10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class TripsFragment extends Fragment implements TripAdapter.TListener{

ArrayList<Trip> trips;
LinearLayoutManager linearLayoutManager;
RecyclerView recyclerView;
TripAdapter adapter;
Button newTrip;
ArrayList<String> documentId;

    public TripsFragment() {
        // Required empty public constructor
    }


    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        trips = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        documentId =  new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_trips, container, false);
        getData();

        getActivity().setTitle("Trips");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TripAdapter(trips,TripsFragment.this);
        recyclerView.setAdapter(adapter);

        newTrip = view.findViewById(R.id.btnNewTrip);
        newTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView,new NewTripFragment(),"NewTripFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView,new LoginFragment())
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTrip(Trip trip){
//        trips.add(trip);
    }

    @Override
    public void contactDetails(int position) {
        Trip trip = trips.get(position);
        Log.d("TIIG", "onSuccessTripsFragment: "+documentId.get(position));
        listener.sendDetails(trip,documentId.get(position));
    }

    TListener listener;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  TListener){
            listener = (TListener) context;
        }else{
            throw  new RuntimeException(context.toString()+" Must Implement MFListener");
        }
    }

    public interface TListener{
        public void sendDetails(Trip trip,String id);
    }

    public void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        trips.clear();

        db.collection("trips")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                           documentId.add(document.getId());
                           HashMap<String,Double> start = (HashMap<String, Double>) document.get("start");
                            HashMap<String,Double> finish = (HashMap<String, Double>) document.get("finish");
                            Trip t = new Trip(document.getString("name"),document.getString("debut"),document.getString("end"),document.getString("status"),document.getDouble("distance"),new MyLocation(start.get("Lat"),start.get("Long")),new MyLocation(finish.get("Lat"),finish.get("Long")));
                            trips.add(t);
                        }
                        adapter.notifyDataSetChanged();
                    }

                });

    }
}