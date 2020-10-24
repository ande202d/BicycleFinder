package com.example.bicyclefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBicycleActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MINE";
    public static final String CURRENTUSER = "CURRENTUSER";
    private User currentUser;
    TextView message;
    private FirebaseAuth mAuth;

    RadioGroup radioGroup;
    RadioButton radioButtonFound;
    RadioButton radioButtonMissing;

    EditText addFieldFrameNumber;
    EditText addFieldKindOfBicycle;
    EditText addFieldBrand;
    EditText addFieldColors;
    EditText addFieldPlace;
    EditText addFieldDate;

    LinearLayout addVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bicycle);

        mAuth = FirebaseAuth.getInstance();

        message = findViewById(R.id.addTextViewMessage);

        radioGroup = findViewById(R.id.addRadioGroup);
        radioButtonFound = findViewById(R.id.addRadioGroupFound);
        radioButtonMissing = findViewById(R.id.addRadioGroupMissing);

        addFieldFrameNumber = findViewById(R.id.addFrameNumber);
        addFieldKindOfBicycle = findViewById(R.id.addKindOfBicycle);
        addFieldBrand = findViewById(R.id.addBrand);
        addFieldColors = findViewById(R.id.addColors);
        addFieldPlace = findViewById(R.id.addPlace);
        addFieldDate = findViewById(R.id.addDate);

        addVisibility = findViewById(R.id.addVisibility);

        addFieldDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        addVisibility.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        currentUser = (User)intent.getSerializableExtra(CURRENTUSER);
        if (currentUser != null){
            currentUser = (User)intent.getSerializableExtra(CURRENTUSER);
            addVisibility.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "CurrentUser: " + currentUser.getName(), Toast.LENGTH_LONG).show();
        } else {
            addVisibility.setVisibility(View.INVISIBLE);
            message.setText("You need to be logged in");
        }

        //region GETTING CURRENT USER (OLD)
//        if (mAuth.getCurrentUser() != null){
//            List<User> listToCheck = new ArrayList<>();
//            Call<List<User>> callGetAllUsers = ApiUtils.getInstance().getRESTService().getAllUsers();
//            callGetAllUsers.enqueue(new Callback<List<User>>() {
//                @Override
//                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                    if (response.isSuccessful()){
//                        listToCheck.addAll(response.body());
//                        String mAuthCurrentUserId = mAuth.getCurrentUser().getUid();
//                        for (User u : response.body()) {
//                            if (u.getFirebaseUserId().equals(mAuthCurrentUserId)){ //if one of the users in REST is the same as the one we are logged in as
//                                currentUser = u;
//                            }
//                        }
//                    }
//                    else message.setText("Couldnt check if firebase id on REST exists");
//                }
//
//                @Override
//                public void onFailure(Call<List<User>> call, Throwable t) {
//                    message.setText(t.getMessage());
//                }
//            });
//        }
        //endregion
    }

    public void AddBicycle(View view){
        String frameNumber = addFieldFrameNumber.getText().toString();
        String kindOfBicycle = addFieldKindOfBicycle.getText().toString();
        String brand = addFieldBrand.getText().toString();
        String colors = addFieldColors.getText().toString();
        String place = addFieldPlace.getText().toString();

        if (currentUser == null){
            message.setText("You need to be logged in");
        }
        else if (frameNumber.isEmpty()){
            message.setText("FrameNumber cant be empty");
        }
        else if (place.isEmpty()){
            message.setText("Place cant be empty");
        }
        else {
            Bike bikeToAdd = new Bike(
                    0,
                    frameNumber,
                    kindOfBicycle,
                    brand,
                    colors,
                    place,
                    "",
                    currentUser.getId(),
                    //69,
                    getMissingFound()
            );
            Call<Bike> callAddBike = ApiUtils.getInstance().getRESTService().postBike(bikeToAdd);
            callAddBike.enqueue(new Callback<Bike>() {
                @Override
                public void onResponse(Call<Bike> call, Response<Bike> response) {
                    if (response.isSuccessful()){
                        message.setText("Success");
                        Log.d(LOG_TAG, "Bike added successfully");
                    } else message.setText("Failed: " + response.message());
                }

                @Override
                public void onFailure(Call<Bike> call, Throwable t) {
                    message.setText("ERROR: " + t.getMessage());
                }
            });
        }
    }

    private String getMissingFound() {
        if (radioGroup.getCheckedRadioButtonId() == radioButtonFound.getId()){
            return "Found";
        }
        else if(radioGroup.getCheckedRadioButtonId() == radioButtonMissing.getId()){
            return "Missing";
        }
        return "This should not be reachable";
    }
}