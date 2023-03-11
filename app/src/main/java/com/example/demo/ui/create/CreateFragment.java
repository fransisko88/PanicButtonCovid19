package com.example.demo.ui.create;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.Register;
import com.example.demo.Utils.FirebaseUtils;
import com.example.demo.databinding.FragmentCreateBinding;
import com.example.demo.databinding.FragmentDashboardBinding;
import com.example.demo.ui.location.LocationViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateFragment extends Fragment {

    private FragmentCreateBinding binding;
    private TextInputEditText hospitalName,address,latitude,longitude;
    private TextView btnSave;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateViewModel createViewModel =
                new ViewModelProvider(this).get(CreateViewModel.class);

        binding = FragmentCreateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        hospitalName = binding.txtHospitalName;
        address = binding.txtAddress;
        latitude = binding.txtLatitude;
        longitude = binding.txtLongitude;
        btnSave = binding.btnSubmit;


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void save(){
//        String hospitalID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String hospitalID = UUID.randomUUID().toString();
        Map<String, Object> hospital = new HashMap<>();
        hospital.put("hospitalId", hospitalID);
        hospital.put("hospitalName", hospitalName.getText().toString());
        hospital.put("address", address.getText().toString());
        hospital.put("latitude", latitude.getText().toString());
        hospital.put("longitude", longitude.getText().toString());
        if(hospitalName.getText().toString().isEmpty() || latitude.getText().toString().isEmpty() || longitude.getText().toString().isEmpty() || address.getText().toString().isEmpty() ){
            Toast.makeText(getActivity(), "Data Tidak Boleh Kosong !", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference documentReference = FirebaseUtils.getFirestore().collection("hospital").document(hospitalID);
        documentReference.set(hospital).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: hospital is created for "+ hospitalID);
                Toast.makeText(getActivity(), "Pendaftaran rumah sakit "+ hospital.get("hospitalName") +" Berhasil ", Toast.LENGTH_SHORT).show();
                hospitalName.setText("");
                latitude.setText("");
                longitude.setText("");
                latitude.setText("");
                address.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }
}