package com.cyclobuddy;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI elements
        EditText usernameInput = findViewById(R.id.edit_text_sign_up_username_input);
        EditText passwordInput = findViewById(R.id.edit_text_sign_up_password_input);
        EditText emailInput = findViewById(R.id.edit_text_sign_up_email_input);
        Button createAccountButton = findViewById(R.id.button_sign_up_create_the_account);

        // Initialize FireBase instances
        // Get the instance of firebaseAuth in order to use the SDK
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Get access to the Realtime Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //Find a place to store data in the database and name it "Users"
        DatabaseReference userReference = firebaseDatabase.getReference("Users");

        createAccountButton.setOnClickListener(new CreateAccountButtonListener(this, usernameInput, emailInput, passwordInput, firebaseAuth, userReference));
    }
}

class CreateAccountButtonListener implements View.OnClickListener {

    private final Context context;
    private final EditText usernameInput;
    private final EditText emailInput;
    private final EditText passwordInput;
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference userReference;

    public CreateAccountButtonListener(Context context, EditText usernameInput, EditText emailInput, EditText passwordInput,
                                 FirebaseAuth firebaseAuth, DatabaseReference userReference) {
        this.context = context;
        this.usernameInput = usernameInput;
        this.emailInput = emailInput;
        this.passwordInput = passwordInput;
        this.firebaseAuth = firebaseAuth;
        this.userReference = userReference;
    }

    @Override
    public void onClick(View v) {

        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Create an account in the firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String userId = userReference.push().getKey();
                if(userId != null) {
                    User user = new User(username, email, password);
                    userReference.child(userId).setValue(user).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            //Turn to main page(TBC)
                        }
                        else {
                            Toast.makeText(context, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    if(task.getException() != null) {
                        Toast.makeText(context, "Sign up failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.e("Error", "Exception object is null");
                    }
                }
            }
            else if (!(task.isSuccessful()) && task.getException() != null) {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class User {
        public String username;
        public String email;
        public String password;

        public User (String username, String email, String password) {
            this.email = email;
            this.password = password;
            this.username = username;
        }
    }
}

