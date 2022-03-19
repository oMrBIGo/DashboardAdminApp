package com.nathit.dashboardadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; //import androidx (SearchView)
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nathit.dashboardadmin.adapter.AdapterAdmin;
import com.nathit.dashboardadmin.model.ModelAdmin;

public class ManagerAdminActivity extends AppCompatActivity {

    //view
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    AdapterAdmin adapterAdmin;  //Adapter
    SearchView searchView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_admin);

        init_screen();

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ModelAdmin> options = new FirebaseRecyclerOptions
                .Builder<ModelAdmin>()
                .setQuery(database.getReference("Admin"), ModelAdmin.class)
                .build();
        adapterAdmin = new AdapterAdmin(options);
        recyclerView.setAdapter(adapterAdmin);

        //search email admin...
        searchView = findViewById(R.id.SearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearchEmail(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearchEmail(newText);
                return false;
            }
        });

    }

    private void textSearchEmail(String newText) {
        FirebaseRecyclerOptions<ModelAdmin> options = new FirebaseRecyclerOptions
                .Builder<ModelAdmin>()
                .setQuery(database.getReference("Admin").orderByChild("email").startAt(newText).endAt(newText + "~"), ModelAdmin.class)
                .build();
        adapterAdmin = new AdapterAdmin(options);
        adapterAdmin.startListening();
        recyclerView.setAdapter(adapterAdmin);
    }

    //Start Activity
    @Override
    protected void onStart() {
        super.onStart();
        adapterAdmin.startListening();
    }

    //End Activity
    @Override
    protected void onStop() {
        super.onStop();
        adapterAdmin.stopListening();
    }

    private void init_screen() {
        final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View view = getWindow().getDecorView();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    view.setSystemUiVisibility(flags);
                }
            }
        });
    }

    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            startActivity(new Intent(ManagerAdminActivity.this, DashboardActivity.class));
            finish();
        }
        super.onBackPressed();
    }
}