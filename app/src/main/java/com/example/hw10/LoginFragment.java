package com.example.hw10;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    Button login,register;
    EditText editTextTextEmailAddress,editTextTextPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Login");
        View view =  inflater.inflate(R.layout.fragment_login, container, false);


        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword);
        register = view.findViewById(R.id.btnCancel);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerView, new RegisterFragment(),"LoginFragment")
                        .commit();
            }
        });

        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password;
                email = editTextTextEmailAddress.getText().toString();
                password = editTextTextPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(), "Please Insert the email and password", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.containerView,new TripsFragment(),"TripsFragment")
                                                .commit();
                                    }else{
                                        Log.d("TAG", "onComplete: "+task.getException().getMessage());
                                    }
                                }
                            });
                }


            }
        });

        return view;
    }
}