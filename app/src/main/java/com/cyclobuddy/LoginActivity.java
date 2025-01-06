package com.cyclobuddy;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize UI elements
        EditText usernameInput = findViewById(R.id.edit_text_login_username_input);
        EditText passwordInput = findViewById(R.id.edit_text_login_password_input);
        Button confirmButton = findViewById(R.id.button_login_confirm);
        Button forgetPasswordButton = findViewById(R.id.button_login_forget_password);

        // Initialize FireBase instances
        // Get the instance of firebaseAuth in order to use the SDK
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Get access to the Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //Find a place to store data in the database and name it "Users"
        DatabaseReference userReference = firebaseDatabase.getReference("Users");

        confirmButton.setOnClickListener(new ConfirmButtonListener(this, usernameInput, passwordInput, userReference));
        forgetPasswordButton.setOnClickListener(new ForgetPasswordButtonListener(this, usernameInput, userReference, firebaseAuth));
    }
}

class ConfirmButtonListener implements View.OnClickListener {
    private final Context context;
    private final EditText usernameInput;
    private final EditText passwordInput;
    private final DatabaseReference userReference;

    public ConfirmButtonListener (Context context, EditText usernameInput, EditText passwordInput, DatabaseReference userReference) {
        this.context = context;
        this.passwordInput = passwordInput;
        this.usernameInput = usernameInput;
        this.userReference = userReference;
    }

    @Override
    public void onClick(View view) {
        String password = passwordInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();

        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            Toast.makeText(context, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = userReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);
                        if (dbPassword != null && dbPassword.equals(password)) {
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
                            // Turn to main page(TBC)
                            return;
                        }
                    }
                    Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
                Toast.makeText(context, "Failed to login!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

class ForgetPasswordButtonListener implements View.OnClickListener {
    private final Context context;
    private final EditText usernameInput;
    private final DatabaseReference userReference;
    private final FirebaseAuth firebaseAuth;

    public ForgetPasswordButtonListener (Context context, EditText usernameInput, DatabaseReference userReference, FirebaseAuth firebaseAuth) {
        this.context = context;
        this.usernameInput = usernameInput;
        this.userReference = userReference;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public void onClick(View view) {
        String username = usernameInput.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(context, "Please enter your username first!", Toast.LENGTH_SHORT).show();
            return;
        }

        userReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String email = userSnapshot.child("email").getValue(String.class);
                                if (email != null) {
                                    firebaseAuth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                                }
                                                else if (task.getException() != null){
                                                    Toast.makeText(context, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                                    Log.e("ResetError", "Error: " + task.getException().getMessage() + "Unknown Error");
                                                }
                                            });
                                    return;
                                }
                            }
                        }
                        else {
                            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                        Toast.makeText(context, "Failed to reset password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
