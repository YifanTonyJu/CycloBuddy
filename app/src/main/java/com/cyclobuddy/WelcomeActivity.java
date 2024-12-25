package com.cyclobuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button loginButton = findViewById(R.id.button_login);
        Button signUpButton = findViewById(R.id.button_sign_up);

        LoginButtonClickListener loginListener = new LoginButtonClickListener(this);
        loginButton.setOnClickListener(loginListener);

        SignUpButtonClickListener signUpListener = new SignUpButtonClickListener(this);
        signUpButton.setOnClickListener(signUpListener);
    }
}

class LoginButtonClickListener implements View.OnClickListener {
    private final Context context;

    // transmit the WelcomeActivity to this class
    public LoginButtonClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        //change to LoginActivity page
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}

class SignUpButtonClickListener implements View.OnClickListener {
    private final Context context;

    // transmit the WelcomeActivity to this class
    public SignUpButtonClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        //change to LoginActivity page
        Intent intent = new Intent(context,SignUpActivity.class);
        context.startActivity(intent);
    }
}