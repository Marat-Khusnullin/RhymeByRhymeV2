package com.example.rhymebyrhymeversion2;

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

import com.example.rhymebyrhymeversion2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    EditText passwordAgain;
    TextView loginButton;
    TextView newAcc;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);

        login = (EditText) findViewById(R.id.editTextLogReg);
        password = (EditText) findViewById(R.id.editTextPasReg);
        passwordAgain = (EditText) findViewById(R.id.editTextPasAgain);
        loginButton = (TextView) findViewById(R.id.textButton);
        newAcc = (TextView) findViewById(R.id.newAccText);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }
                // ...
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(passwordAgain.getText().toString())) {
                    registration(login.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(RegistrationActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        password.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        passwordAgain.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        loginButton.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        newAcc.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
        loginButton.setTypeface(Typeface.createFromAsset(
                getAssets(), "fonts/Roboto-BoldCondensed.ttf"));
    }


    public void registration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    saveUser();
                    final Intent intent = new Intent(RegistrationActivity.this, MainProfileActivity.class);
                    mAuth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    // Re-enable button

                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "Вам выслано сообщение с подтверждением", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(RegistrationActivity.this, MainLogActivity.class);
                                        startActivity(intent1);
                                    } else {
                                       /* Log.e(TAG, "sendEmailVerification", task.getException());
                                        Toast.makeText(EmailPasswordActivity.this,
                                                "Failed to send verification email.",
                                                Toast.LENGTH_SHORT).show();*/
                                    }
                                }
                            });
                    /*intent.putExtra("Reference", mAuth.getCurrentUser().getUid());
                    startActivity(intent);*/
                } else {
                    //Toast.makeText(this,"" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void saveUser() {
        mAuth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString());
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        writeUser(firebaseUser.getUid(), "Не указано", "Не указано","Не указано", 0 ,"Не указано","Не указано");
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    public void writeUser(String userId, String name, String surname, String email, int year,
                          String link, String description){
        User newUser = new User(name, surname, email, year, link, description, null ,0);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("users").child(userId).setValue(newUser);
    }
}
