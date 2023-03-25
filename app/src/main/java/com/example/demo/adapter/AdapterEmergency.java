package com.example.demo.adapter;

import static android.content.ContentValues.TAG;
import static com.example.demo.utils.FirebaseUtils.getPictureHospital;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.DetailHospital;
import com.example.demo.R;
import com.example.demo.RuteLocation;
import com.example.demo.model.Emergency;
import com.example.demo.model.Hospital;
import com.example.demo.utils.FirebaseUtils;
import com.example.demo.view.admin.UpdateHospital;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class AdapterEmergency extends RecyclerView.Adapter<AdapterEmergency.MyViewHolder>{
    private Context context;
    private ArrayList<Emergency> emergencies;

    public AdapterEmergency(Context context, ArrayList<Emergency> emergencies) {
        this.context = context;
        this.emergencies = emergencies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.emergency_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Emergency emergency = emergencies.get(position);
        FirebaseUtils.getFirestore().collection("users").document(emergency.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            holder.userName.setText(document.getString("username"));
                            holder.address.setText(emergency.getAddress());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context.getApplicationContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(), RuteLocation.class);
                intent.putExtra("emergencyId", emergency.getEmergencyId());
                intent.putExtra("hospitalId", emergency.getHospitalId());
                intent.putExtra("userId", emergency.getUserId());
                intent.putExtra("addressPasien", emergency.getAddress());
                intent.putExtra("latitudeUser", String.valueOf(emergency.getLatitudeUser()));
                intent.putExtra("longitudeUser", String.valueOf(emergency.getLongitudeUser()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName,address;
        private LinearLayout item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            address = itemView.findViewById(R.id.address);
            item = itemView.findViewById(R.id.item);
        }
    }

}
