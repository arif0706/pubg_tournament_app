package com.example.client.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.client.MainActivity;
import com.example.client.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class Logout extends Fragment {
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount acct;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(root.getContext());
        progressDialog.setMessage("Logging out");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        auth.signOut();
        Intent intent=new Intent(getActivity().getApplicationContext(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();

        progressDialog.dismiss();

        return root;

    }
}
