
package com.example.rhymebyrhymeversion2;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * Created by Марат on 30.10.2017.
 */

public class MainController {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SignInterface signInterface;



    public MainController(SignInterface signInterface) {
        this.signInterface = signInterface;
    }






}
