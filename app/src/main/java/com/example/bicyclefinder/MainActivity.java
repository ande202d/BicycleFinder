package com.example.bicyclefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private boolean _loginMode = false;
    private boolean _signUpMode = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "lukind";

    private TextView _messageView;
    private TextView _loggedInView;
    private EditText _loginNameField;
    private EditText _loginPhoneField;
    private EditText _loginEmailField;
    private EditText _loginPasswordField;
    private Button _loginButton;
    private Button _signUpButton;
    private Button _goToBicyclesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        _messageView = findViewById(R.id.mainTextViewMessage);
        _loggedInView = findViewById(R.id.mainTextViewLoggedIn);
        _loginNameField = findViewById(R.id.mainEditTextLoginName);
        _loginPhoneField = findViewById(R.id.mainEditTextLoginPhone);
        _loginEmailField = findViewById(R.id.mainEditTextLoginEmail);
        _loginPasswordField = findViewById(R.id.mainEditTextLoginPassword);
        _loginButton = findViewById(R.id.mainButtonLogin);
        _signUpButton = findViewById(R.id.mainButtonSignUp);
        _goToBicyclesButton = findViewById(R.id.mainButtonAllBicycles);

        SwitchMode(); //ends with loginmode = true
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        ApplyLayout();
    }

    public void SwitchMode(){
        if (_loginMode) {
            _loginMode = false;
            _signUpMode = true;
        }
        else if (_signUpMode) {
            _signUpMode = false;
            _loginMode = true;
        }
        else {
            _loginMode = true;
            _signUpMode = false;
        }
        _loginNameField.setText("");
        _loginPhoneField.setText("");
        _loginEmailField.setText("");
        _loginPasswordField.setText("");

        ApplyLayout();
    }

    private void ApplyLayout(){
        currentUser = mAuth.getCurrentUser();

        _loginButton.setBackgroundColor(_loginMode ? Color.GREEN : Color.RED);
        _signUpButton.setBackgroundColor(_signUpMode ? Color.GREEN : Color.RED);
        _loginNameField.setVisibility(_loginMode ? View.GONE : View.VISIBLE);
        _loginPhoneField.setVisibility(_loginMode ? View.GONE : View.VISIBLE);

        //region Another way
        //        if (_loginMode && currentUser == null || (_signUpMode)){
//            _loginEmailField.setVisibility(View.VISIBLE);
//            _loginPasswordField.setVisibility(View.VISIBLE);
//            _loginButton.setText("Login");
//        }
//        else {
//            _loginEmailField.setVisibility(View.GONE);
//            //_loginEmailField.setText("You are already logged in");
//            _loginPasswordField.setVisibility(View.GONE);
//            _loginButton.setText("Log Out");
//        }
        //endregion

        //if you are already logged in
        _loginEmailField.setVisibility((_loginMode && currentUser == null || (_signUpMode)) ? View.VISIBLE : View.GONE);
        _loginPasswordField.setVisibility((_loginMode && currentUser == null || (_signUpMode)) ? View.VISIBLE : View.GONE);
        _loginButton.setText((_loginMode && currentUser == null || (_signUpMode)) ? "Login" : "Log Out");

        _loggedInView.setVisibility((_loginMode && currentUser != null) ? View.VISIBLE : View.GONE);
        _loggedInView.setText((_loginMode && currentUser != null) ? ("Signed in as: " + currentUser.getEmail()) : "");
        //_messageView.setText(GetMode());

        if (currentUser == null){
            _messageView.setText("ingen er logget ind");
        }
        else {
            _messageView.setText("Signed in as: " + currentUser.getEmail());
        }
    }


    public void Login(View view) {
        if (_loginMode){
            if (mAuth.getCurrentUser() != null){ //someone is already logged in
                mAuth.signOut();
                _messageView.setText("Signed Out");
                ApplyLayout();
                return;
            }
            String email = _loginEmailField.getText().toString();
            String pass = _loginPasswordField.getText().toString();
            if (pass.isEmpty() || email.isEmpty()) return;
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(getBaseContext(), "Successfully logged in",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                _messageView.setText("Signed in as: " + user.getEmail());
                                Intent intent = new Intent(getBaseContext(), BicyclesActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getBaseContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                _messageView.setText("Ugyldigt login");
                            }
                        }
                    });
        }
        else {
            SwitchMode();
        }
    }

    public void SignUp(View view) {
        if (_signUpMode){

        }
        else {
            SwitchMode();
        }
    }

    public String GetMode(){
        String s = "";
        if (_loginMode) s+= "Login";
        if (_signUpMode) s+= "SignUp";
        return s;
    }

    public void AllBicycles(View view) {
        Intent intent = new Intent(getBaseContext(), BicyclesActivity.class);
        startActivity(intent);
    }
}