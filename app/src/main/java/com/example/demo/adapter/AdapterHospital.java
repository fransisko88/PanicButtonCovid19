package com.example.demo.adapter;
import static com.example.demo.utils.FirebaseUtils.getPictureHospital;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.DetailHospital;
import com.example.demo.R;
import com.example.demo.model.Hospital;
import com.example.demo.utils.FirebaseUtils;
import com.example.demo.view.admin.UpdateHospital;

import java.util.ArrayList;

public class AdapterHospital extends RecyclerView.Adapter<AdapterHospital.MyViewHolder>{
    private Context context;
    private ArrayList<Hospital> hospitalList;

    public AdapterHospital(Context context, ArrayList<Hospital> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.hospital_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Hospital hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getHospitalName());
        holder.address.setText(hospital.getAddress());
        getPictureHospital(holder.picture,hospital.getHospitalId());
        String email = FirebaseUtils.getFirebaseUser().getEmail();
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.equalsIgnoreCase("admin@gmail.com")){
                    final Intent intent = new Intent(v.getContext(), UpdateHospital.class);
                    intent.putExtra("hospitalId", hospital.getHospitalId());
                    intent.putExtra("hospitalName", hospital.getHospitalName());
                    intent.putExtra("address", hospital.getAddress());
                    intent.putExtra("latitude", hospital.getLatitude());
                    intent.putExtra("longitude", hospital.getLongitude());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView hospitalName,address;
        private LinearLayout item;
        private ImageView picture;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            address = itemView.findViewById(R.id.address);
            picture = itemView.findViewById(R.id.imageHospital);
            item = itemView.findViewById(R.id.item);
        }
    }

}
