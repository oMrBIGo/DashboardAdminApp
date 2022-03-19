package com.nathit.dashboardadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.nathit.dashboardadmin.R;
import com.nathit.dashboardadmin.model.ModelAdmin;

public class AdapterAdmin extends FirebaseRecyclerAdapter<ModelAdmin, AdapterAdmin.ViewHolder> {

    public AdapterAdmin(@NonNull FirebaseRecyclerOptions<ModelAdmin> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ModelAdmin model) {
        holder.tvEmail.setText("Email: " + model.getEmail());
        holder.tvUid.setText("Uid: " + model.getUid());

        //view gone
        holder.tvName.setVisibility(View.GONE);
        holder.LineView.setVisibility(View.GONE);
        holder.linearEditDel.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmail, tvUid, tvName;
        View LineView;
        LinearLayout linearEditDel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvUid = (TextView) itemView.findViewById(R.id.tvUid);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            LineView = (View) itemView.findViewById(R.id.LineView);
            linearEditDel = (LinearLayout) itemView.findViewById(R.id.linearEditDel);
        }
    }
}
