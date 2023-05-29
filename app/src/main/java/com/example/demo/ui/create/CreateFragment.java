package com.example.demo.ui.create;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.model.Hospital;
import com.example.demo.utils.FirebaseUtils;
import com.example.demo.databinding.FragmentCreateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.hsr.geohash.GeoHash;

public class CreateFragment extends Fragment {
    private static final int GALLERY_REQUEST_PROFILE = 1;
    private Uri mImageUriProfile = null;
    private FragmentCreateBinding binding;
    private TextInputEditText hospitalName,address,latitude,longitude;
    private TextView btnSave;
    private LinearLayout picture;
    private ImageView iconImage,imageHospital;
    private StorageReference profileRef;



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
        picture = binding.backgroundSampul;
        imageHospital = binding.photoSampul;
        iconImage = binding.iconImage;


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra(Intent.ACTION_PICK, true);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_PROFILE);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // method untuk menghitung jarak antara dua titik dalam km
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // radius bumi dalam km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return distance;
    }

    public void save(){
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
        try {
            savePhoto(mImageUriProfile,hospitalID);
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
                    imageHospital.setImageURI(null);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                }
            });


        }catch (Exception e){
            Log.d(TAG, "onFailure: " + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_PROFILE && resultCode == RESULT_OK){
            mImageUriProfile = data.getData();
            iconImage.setVisibility(View.GONE);
            imageHospital.setImageURI(mImageUriProfile);
        }
    }

    private void savePhoto(Uri mImageUriProfile,String uuid){

        if(mImageUriProfile != null){
            profileRef = FirebaseUtils.getStorageReference().child("hospital/"+uuid+"/profile.jpg");
            profileRef.putFile(mImageUriProfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}