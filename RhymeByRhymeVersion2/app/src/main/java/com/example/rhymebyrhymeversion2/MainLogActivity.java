package com.example.rhymebyrhymeversion2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MainLogActivity extends AppCompatActivity implements SignInterface {

    TextView name;
    TextView vk;
    TextView forgetPass;
    TextView registrationLink;
    EditText login;
    EditText password;
    TextView loginButton;
    MainController mainController;




    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.name);
        vk = (TextView) findViewById(R.id.vkLog);
        forgetPass = (TextView) findViewById(R.id.forget_password);
        registrationLink = (TextView) findViewById(R.id.registration_link);

        login = (EditText) findViewById(R.id.editTextLog);
        password = (EditText) findViewById(R.id.editTextPas);

        loginButton = (TextView) findViewById(R.id.textButton);

        mainController = new MainController(this);
        registrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLogActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        /*if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(MainLogActivity.this, MainProfileActivity.class);
            startActivity(intent);
            finish();
        }*/

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    /*Intent intent = new Intent(context, MainProfile.class);
                    startActivity(intent);*/
                } else {
                    // User is signed out
                }
                // ...
            }
        };


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              sign(""+ login.getText(),"" +  password.getText());
            }
        });





        name.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        vk.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        forgetPass.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        registrationLink.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));

        login.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        password.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));

        loginButton.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));

    }


    public void sign(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    if(mAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(MainLogActivity.this, MainProfileActivity.class);
                        startActivity(intent);
                        closeActivity();
                    }
                } else {
                    Toast.makeText(MainLogActivity.this, "23123", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void closeActivity() {
        finish();
    }


}
