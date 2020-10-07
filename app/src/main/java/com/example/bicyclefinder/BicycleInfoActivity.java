package com.example.bicyclefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BicycleInfoActivity extends AppCompatActivity {

    public static final String BIKE = "BIKE";
    public static final String CURRENTUSER = "CURRENTUSER";
    private User currentUser;
    private Bike currentBike;
    TextView message;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicycle_info);

        Intent intent = getIntent();
        currentBike = (Bike) intent.getSerializableExtra(BIKE);
        currentUser = (User) intent.getSerializableExtra(CURRENTUSER);
        message = findViewById(R.id.infoTextViewMessage);
        deleteButton = findViewById(R.id.infoDeleteButton);

        if (currentBike != null) {
            setAllBikeData(currentBike);
            if (currentBike.getUserId() != null) setAllUserData(currentBike.getUserId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        deleteButton.setVisibility(View.GONE);
        if (currentUser != null){
            if (currentUser.getId().equals(currentBike.getUserId())){
                deleteButton.setVisibility(View.VISIBLE);
            }
        }
    }

    void setAllBikeData(Bike b){
        TextView idField = findViewById(R.id.infoId);
        TextView frameNumberField = findViewById(R.id.infoFrameNumber);
        TextView kindOfBicycleField = findViewById(R.id.infoKindOfBicycle);
        TextView brandField = findViewById(R.id.infoBrand);
        TextView colorsField = findViewById(R.id.infoColors);
        TextView placeField = findViewById(R.id.infoPlace);
        TextView dateField = findViewById(R.id.infoDate);
        TextView userIdField = findViewById(R.id.infoUserId);
        TextView missingFoundField = findViewById(R.id.infoMissingFound);

        idField.setText(b.getId().toString());
        frameNumberField.setText(b.getFrameNumber());
        kindOfBicycleField.setText(b.getKindOfBicycle());
        brandField.setText(b.getBrand());
        colorsField.setText(b.getColors());
        placeField.setText(b.getPlace());
        dateField.setText(b.getDate());
        userIdField.setText(b.getUserId().toString());
        missingFoundField.setText(b.getMissingFound());
    }
    void setAllUserData(int id){
        TextView userIdField = findViewById(R.id.infoIdUser);
        TextView userNameField = findViewById(R.id.infoNameUser);
        TextView phoneField = findViewById(R.id.infoPhoneUser);
        TextView firebaseIdField = findViewById(R.id.infoFirebaseIdUser);

        RESTService service = ApiUtils.getInstance().getRESTService();

        Call<User> callOneUser = service.getOneUser(id);

        callOneUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        User u = response.body();
                        userIdField.setText(u.getId().toString());
                        userNameField.setText(u.getName());
                        phoneField.setText(u.getPhone());
                        firebaseIdField.setText(u.getFirebaseUserId());
                    } else userIdField.setText("user not found in REST");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userIdField.setText(t.getMessage());
            }
        });
    }

    public void DeleteBicycle(View view) {
        if (currentBike != null){
            Call<String> callDeleteBike = ApiUtils.getInstance().getRESTService().deleteBike(currentBike.getId());
            callDeleteBike.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        message.setText("Successfully deleted: " + currentBike.getId());
                    } else {
                        message.setText("ID: " + currentBike.getId() + ", ERROR: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    message.setText(t.getMessage());
                }
            });
        }
    }
}