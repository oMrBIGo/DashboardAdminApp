package com.nathit.dashboardadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_EMAIL = "rememberMe";
    //View
    TextInputEditText etEmail, etPassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_screen();

        //init view
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //save email and password
        CheckBox checkBox = (CheckBox) findViewById(R.id.remember);
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String email = sharedPreferences.getString("svEmail", "");
        String password = sharedPreferences.getString("svPassword", "");
        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false) == true){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        etEmail.setText(email);
        etPassword.setText(password);

        //Button Login click -> DashboardActivity
        Button LoginBtn = (Button) findViewById(R.id.BtnLogin);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String checkPassword = "^(?=\\S+$).{6,20}$";    //กำหนดรหัสผ่าน

                if (checkBox.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                    StoreDataUsingSharedPref(email,password);

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etEmail.setError("Please enter your email address completely.");
                        etEmail.requestFocus();
                    } else if (TextUtils.isEmpty(password)) {
                        etPassword.setError("Please enter your password.");
                        etPassword.requestFocus();
                    } else if (!password.matches(checkPassword)) {
                        etPassword.setError("Please enter a password of 6 characters or more.");
                        etPassword.requestFocus();
                    } else {
                        loginAdmin(email, password);
                    }

                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etEmail.setError("Please enter your email address completely.");
                        etEmail.requestFocus();
                    } else if (TextUtils.isEmpty(password)) {
                        etPassword.setError("Please enter your password.");
                        etPassword.requestFocus();
                    } else if (!password.matches(checkPassword)) {
                        etPassword.setError("Please enter a password of 6 characters or more.");
                        etPassword.requestFocus();
                    } else {
                        getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit().clear().commit();
                        loginAdmin(email, password);
                    }

                }

            }
        });
    }

    private void StoreDataUsingSharedPref(String email, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit();
        editor.putString("svEmail", email);
        editor.putString("svPassword", password);
        editor.apply();
    }

    private void loginAdmin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                String email = firebaseUser.getEmail();
                                uid = firebaseUser.getUid();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Admin");

                                reference.child(uid).setValue(hashMap);
                            }
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                etEmail.setError("Username does not exist in the system. Please register again.");
                                etEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                etEmail.setError("some information is not correct Please check and try again.");
                                etEmail.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
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
}