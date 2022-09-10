package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.reactivex.rxjava3.annotations.NonNull;

public class Login extends AppCompatActivity {

    SignInButton sbtn;
    GoogleSignInOptions gso;
    GoogleSignInClient mgoogleSignInClient;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if(currentUser!=null)
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();

    }
    // [END on_start_check_user]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sbtn = findViewById(R.id.btn);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        processreq();

        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processlogin();
            }
        });


    }

    private void processlogin() {

        Intent signInIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,100);

    }

    private void processreq() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }
    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Error in getting google`s information ",Toast.LENGTH_LONG).show();

                // Google Sign In failed, update UI appropriately

            }
        }
    }
    // [END onactivityresult]
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"sign in ",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG1", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Problem found in sign in ",Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    // [END auth_with_google]

}