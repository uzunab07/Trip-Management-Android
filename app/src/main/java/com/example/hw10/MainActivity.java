package com.example.hw10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NewTripFragment.mListener,TripsFragment.TListener{
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerView, new LoginFragment(),"LoginFragment")
                    .addToBackStack(null)
                    .commit();
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerView, new TripsFragment(),"TripsFragment")
                    .commit();
        }

    }

    @Override
    public void goToBack() {
        getSupportFragmentManager().popBackStack();

    }

    @Override
    public void sendDetails(Trip trip,String id) {
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView,TripDetailsFragment.newInstance(trip,id))
                .addToBackStack(null)
                .commit();
    }


}