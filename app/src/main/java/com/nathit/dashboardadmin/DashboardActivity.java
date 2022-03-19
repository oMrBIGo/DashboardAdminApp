package com.nathit.dashboardadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nathit.dashboardadmin.model.ModelAdmin;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseDatabase database;
    TextView TvEmail, TvUid;
    LinearLayout logout;

    TextView tvCountUser, tvCountAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init_screen();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        tvCountUser = findViewById(R.id.count_user);
        tvCountAdmin = findViewById(R.id.count_admin);
        CountUser();
        CountAdmin();

        TvEmail = findViewById(R.id.email);
        TvUid = findViewById(R.id.uid);

        showAdminProfile();

        //manager_user
        LinearLayout manager_user = (LinearLayout) findViewById(R.id.manager_user);
        manager_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ManagerUserActivity.class));
                finish();
            }
        });

        //manager_admin
        LinearLayout manager_admin = (LinearLayout) findViewById(R.id.manager_admin);
        manager_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ManagerAdminActivity.class));
                finish();
            }
        });

        logout = findViewById(R.id.BtnLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut(); //ออกจากระบบ
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void CountUser() {
        reference = database.getReference("User");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = (int) snapshot.getChildrenCount();
                String userCounter = String.valueOf(counter);

                tvCountUser.setText(userCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CountAdmin() {
        reference = database.getReference("Admin");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = (int) snapshot.getChildrenCount();
                String adminCounter = String.valueOf(counter);

                tvCountAdmin.setText(adminCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAdminProfile() {
        String userID = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelAdmin modelAdmin = snapshot.getValue(ModelAdmin.class);
                if (modelAdmin != null) {
                    String email = "" + snapshot.child("email").getValue();
                    String uid = "" + snapshot.child("uid").getValue();

                    TvEmail.setText(email); //แสดงอีเมล
                    TvUid.setText("Uid: "+uid); //แสดง UID
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void init_screen() {
        final  int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
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
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
        super.onBackPressed();
    }
}