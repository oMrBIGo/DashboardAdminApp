package com.nathit.dashboardadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nathit.dashboardadmin.adapter.AdapterUser;
import com.nathit.dashboardadmin.model.ModelUser;

public class ManagerUserActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    AdapterUser adapterUser;
    SearchView searchView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);

        init_screen();

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ModelUser> option = new FirebaseRecyclerOptions
                .Builder<ModelUser>()
                .setQuery(database.getReference("User"), ModelUser.class)
                .build();
        adapterUser = new AdapterUser(option);
        recyclerView.setAdapter(adapterUser);

        //search email user...
        searchView = findViewById(R.id.searchView);
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

    private void textSearchEmail(String query) {
        FirebaseRecyclerOptions<ModelUser> options = new FirebaseRecyclerOptions
                .Builder<ModelUser>()
                .setQuery(database.getReference("User").orderByChild("email").startAt(query).endAt(query + "~"), ModelUser.class)
                .build();
        adapterUser = new AdapterUser(options);
        adapterUser.startListening();
        recyclerView.setAdapter(adapterUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterUser.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterUser.stopListening();
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
            startActivity(new Intent(ManagerUserActivity.this, DashboardActivity.class));
            finish();
        }
        super.onBackPressed();
    }
}