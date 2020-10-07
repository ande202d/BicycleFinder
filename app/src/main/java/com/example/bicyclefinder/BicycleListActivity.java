package com.example.bicyclefinder;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BicycleListActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    List<Bike> Bikes = new ArrayList<Bike>();
    List<Bike> AllBikes = new ArrayList<Bike>();
    private static final String LOG_TAG = "MINE";
    private TextView messageView;
    public static final String CURRENTUSER = "CURRENTUSER";
    private User currentUser;
    private ShareActionProvider shareActionProvider;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicycle_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.listView_tabLayout);
        SetupTabLayout(tabLayout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToAddBike();
            }
        });

        messageView = findViewById(R.id.bicycleTextViewMessage);

        //RecyclerView -stuff
        RecyclerView recyclerView = findViewById(R.id.rvBikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, Bikes);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //getAndShowAllBikes();
        reloadTheBikeListAndShow("All");
    }


    private void SetupTabLayout(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Missing"));
        tabLayout.addTab(tabLayout.newTab().setText("Found"));
        tabLayout.addTab(tabLayout.newTab().setText("Mine"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(BicycleListActivity.this, tab.getPosition() + ", " + tab.getText(), Toast.LENGTH_LONG).show();
                switch (tab.getText().toString()){
                    case "All":
                        showAllBikes();
                        break;
                    case "Missing":
                        showMissingBikes();
                        break;
                    case "Found":
                        showFoundBikes();
                        break;
                    case "Mine":
                        showMyBikes();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = (User)getIntent().getSerializableExtra(CURRENTUSER);
        //reloadTheBikeList();

        //Toast.makeText(this, ""+AllBikes.size(), Toast.LENGTH_LONG).show();
        switch (tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString()){
            case "All":
                reloadTheBikeListAndShow("All");
                break;
            case "Missing":
                reloadTheBikeListAndShow("Missing");
                break;
            case "Found":
                reloadTheBikeListAndShow("Found");
                break;
            case "Mine":
                reloadTheBikeListAndShow("Mine");
                break;
            default:
                reloadTheBikeListAndShow("All");
        }
        //tabLayout.getTabAt(0).select();

        //Bikes.clear();
        //Bikes.add(new Bike(99, "hej", "hej", "hej", "hej", "hej", "hej", 99, "found"));
        adapter.notifyDataSetChanged();

        //showAllBikes();
        //Toast.makeText(this, "CurrentUser: " + currentUser.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Bike bike = adapter.getItem(position);
        Intent intent = new Intent(BicycleListActivity.this, BicycleInfoActivity.class);
        intent.putExtra(BicycleInfoActivity.BIKE, bike);
        intent.putExtra(BicycleInfoActivity.CURRENTUSER, currentUser);
        startActivity(intent);
        //Toast.makeText(this, "You clicked" + bike + " on row: " + position, Toast.LENGTH_LONG).show();
    }

    public void GoToAddBike() {
        //getAndShowAllBikes();
        Intent intent = new Intent(BicycleListActivity.this, AddBicycleActivity.class);
        intent.putExtra(AddBicycleActivity.CURRENTUSER, currentUser);
        startActivity(intent);
    }

    private void showFoundBikes(){
        Bikes.clear();
        for (Bike b : AllBikes) {
            if (b.getMissingFound().toLowerCase().equals("found")) Bikes.add(b);
        }
        adapter.notifyDataSetChanged();
    }

    private void showMissingBikes(){
        Bikes.clear();
        for (Bike b : AllBikes) {
            if (b.getMissingFound().toLowerCase().equals("missing")) Bikes.add(b);
        }
        adapter.notifyDataSetChanged();
    }

    private void showAllBikes(){
        Bikes.clear();
        Bikes.addAll(AllBikes);
        adapter.notifyDataSetChanged();
    }

    private void showMyBikes(){
        Bikes.clear();
        //Toast.makeText(this, ""+currentUser.getId(), Toast.LENGTH_LONG).show();
        if (currentUser != null){
            for (Bike b : AllBikes) {
                if (b.getUserId().equals(currentUser.getId())) {
                    Bikes.add(b);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void reloadTheBikeList(){
        Call<List<Bike>> callAllBikes = ApiUtils.getInstance().getRESTService().getAllBikes();
        callAllBikes.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful()){
                    Log.d(LOG_TAG, response.message());
                    //Bikes.add(new Bike(69, "69", "69", "69", "69", "69", "69", 69, "found"));
                    AllBikes.clear();
                    AllBikes.addAll(response.body());
                    //adapter.notifyDataSetChanged();
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

    private void reloadTheBikeListAndShow(String whatToShow){
        Call<List<Bike>> callAllBikes = ApiUtils.getInstance().getRESTService().getAllBikes();
        callAllBikes.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                if (response.isSuccessful()){
                    Log.d(LOG_TAG, response.message());
                    //Bikes.add(new Bike(69, "69", "69", "69", "69", "69", "69", 69, "found"));
                    AllBikes.clear();
                    AllBikes.addAll(response.body());
                    switch (whatToShow){
                        case "All":
                            showAllBikes();
                            break;
                        case "Missing":
                            showMissingBikes();
                            break;
                        case "Found":
                            showFoundBikes();
                            break;
                        case "Mine":
                            showMyBikes();
                            break;
                        default:
                            showAllBikes();
                    }
                    //adapter.notifyDataSetChanged();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //MenuItem menuItem = menu.findItem(R.id.action_share);
        //shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //setShareActionIntent("Want to join me for pizza?");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_all_bicycles:
                Toast.makeText(this, "All", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_show_missing_bicycles:
                Toast.makeText(this, "Missing", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_show_found_bicycles:
                Toast.makeText(this, "Found", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_show_mine_bicycles:
                Toast.makeText(this, "Mine", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_user_data:
                Toast.makeText(this, "UserData", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Head First, 2nd, page 333
    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }


}