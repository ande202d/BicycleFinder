package com.example.bicyclefinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BicyclesActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    List<Bike> Bikes = new ArrayList<Bike>();
    private static final String LOG_TAG = "MINE";
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicycles);

        messageView = findViewById(R.id.bicycleTextViewMessage);

        //RecyclerView -stuff
        RecyclerView recyclerView = findViewById(R.id.rvBikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, Bikes);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        getAndShowAllBikes();
    }

    @Override
    public void onItemClick(View view, int position) {
        Bike bike = adapter.getItem(position);
        Intent intent = new Intent(BicyclesActivity.this, BicycleInfoActivity.class);
        intent.putExtra(BicycleInfoActivity.BIKE, bike);
        startActivity(intent);
        Toast.makeText(this, "You clicked" + bike + " on row: " + position, Toast.LENGTH_LONG).show();
    }

    public void ReloadList(View view) {
        getAndShowAllBikes();
    }

    private void getAndShowAllBikes(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anbo-bicyclefinder.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RESTService service = retrofit.create(RESTService.class);

        Call<List<Bike>> callAllBikes = service.getAllBikes();

        callAllBikes.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful()){
                    Log.d(LOG_TAG, response.message());
                    //Bikes.add(new Bike(69, "69", "69", "69", "69", "69", "69", 69, "found"));
                    Bikes.clear();
                    Bikes.addAll(response.body());
                    messageView.setText("Vi burde få dataen");
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(LOG_TAG, response.message());
                    messageView.setText(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Log.e(LOG_TAG, t.getMessage());
                messageView.setText(t.getMessage());
            }
        });
    }
}