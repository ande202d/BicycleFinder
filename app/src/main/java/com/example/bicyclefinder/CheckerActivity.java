package com.example.bicyclefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckerActivity extends AppCompatActivity {

    /*

    IF THE USER IS LOGGED IN ON FIREBASE,
    THIS ACTIVITY IS CHECKING IF THE USER ALSO EXISTS IN THE REST API

    THE REASON FOR THIS IS THAT THE REST API RESETS EVERY X-HOURS,
    WHERE THE FIREBASE IS CONSISTENT

    THE USER IS ASKED TO PROVIDE THE INFORMATION THAT THE REST SERVICE IS REQUIRING
    AND THEN WE ARE SIMPLY ADDING HIM/HER TO THE REST API AGAIN WITH THE ALREADY EXISTING FIREBASE-ID

     */

    EditText editTextName;
    EditText editTextPhone;
    EditText editTextEmail;
    EditText editTextFirebaseId;
    TextView messageTextView;
    LinearLayout checkerVisible;
    private FirebaseAuth mAuth;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        editTextName = findViewById(R.id.checkerName);
        editTextPhone = findViewById(R.id.checkerPhone);
        editTextEmail = findViewById(R.id.checkerEmail);
        editTextFirebaseId = findViewById(R.id.checkerFirebaseId);
        messageTextView = findViewById(R.id.checkerMessageView);
        mAuth = FirebaseAuth.getInstance();
        checkerVisible = findViewById(R.id.checkerVisible);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkerVisible.setVisibility(View.GONE);
        GetUserFromFirebaseId(mAuth.getUid());
    }

    private void GetUserFromFirebaseId(String firebaseId){
        Call<List<User>> callListOfUsers = ApiUtils.getInstance().getRESTService().getAllUsers();
        callListOfUsers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        //The response found users
                        for (User u : response.body()) {
                            if (u.getFirebaseUserId().equals(firebaseId)){
                                currentUser = u;
                                //Intent intent = new Intent(CheckerActivity.this, BicyclesActivity.class);
                                Intent intent = new Intent(CheckerActivity.this, BicycleListActivity.class);
                                intent.putExtra(BicyclesActivity.CURRENTUSER, currentUser);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                    if (currentUser == null){
                        messageTextView.setText("No user had firebaseId: " + firebaseId );
                        checkerVisible.setVisibility(View.VISIBLE);
                        editTextEmail.setText(mAuth.getCurrentUser().getEmail());
                        editTextFirebaseId.setText(mAuth.getCurrentUser().getUid());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                messageTextView.setText(t.getMessage());
            }
        });
    }

    public void AddUserWithFirebaseId(View view) {
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        if (name.isEmpty()) messageTextView.setText("Name cant be empty");
        else if (phone.isEmpty()) messageTextView.setText("Phone cant be empty");
        else {
            User userToAdd = new User(name, phone, editTextFirebaseId.getText().toString());
            Call<User> callAddUser = ApiUtils.getInstance().getRESTService().postUser(userToAdd);
            callAddUser.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            currentUser = response.body();
                            //Intent intent = new Intent(CheckerActivity.this, BicyclesActivity.class);
                            Intent intent = new Intent(CheckerActivity.this, BicycleListActivity.class);
                            intent.putExtra(BicyclesActivity.CURRENTUSER, currentUser);
                            startActivity(intent);
                        }
                    }
                    else messageTextView.setText(response.message());
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    messageTextView.setText(t.getMessage());
                }
            });
        }
    }
}