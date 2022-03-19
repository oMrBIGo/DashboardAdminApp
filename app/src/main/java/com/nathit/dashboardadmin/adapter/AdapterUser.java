package com.nathit.dashboardadmin.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nathit.dashboardadmin.R;
import com.nathit.dashboardadmin.model.ModelUser;

import java.util.HashMap;
import java.util.Map;

public class AdapterUser extends FirebaseRecyclerAdapter<ModelUser, AdapterUser.ViewHolder> {

    Dialog dialog;
    FirebaseAuth firebaseAuth;

    public AdapterUser(@NonNull FirebaseRecyclerOptions<ModelUser> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ModelUser model) {
        holder.tvEmail.setText("Email: " + model.getEmail());
        holder.tvName.setText("Name: " + model.getName());
        holder.tvUid.setText("Uid: " + model.getUid());
        dialog = new Dialog(holder.tvName.getContext());

        //Edit Profile
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.manager_edit);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                firebaseAuth = FirebaseAuth.getInstance();

                TextInputLayout text_input_name = dialog.findViewById(R.id.text_input_name);
                text_input_name.setHintEnabled(false);

                EditText name = dialog.findViewById(R.id.name);
                Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
                Button btnDelete = dialog.findViewById(R.id.btnDelete);
                Button cancel = dialog.findViewById(R.id.cancel);
                name.setText(model.getName());

                //Cancel
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Update Profile
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name.getText().toString());
                        dialog.dismiss();
                        FirebaseDatabase.getInstance().getReference().child("User")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.tvEmail.getContext(), "User Information has been updated.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.tvEmail.getContext(), "User information update failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });

        //Delete Profile
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.tvEmail.getContext());
                builder.setTitle("Are you sure you want to cancel this account?");
                builder.setMessage("This account information will be permanently deleted. Cannot recover other data.");
                builder.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmail, tvName, tvUid;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvUid = (TextView) itemView.findViewById(R.id.tvUid);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }
}
