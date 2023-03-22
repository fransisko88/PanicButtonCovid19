package com.example.demo.ui.home;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.Login;
import com.example.demo.databinding.FragmentHomeBinding;
import com.example.demo.model.FindLocation;
import com.example.demo.sos;
import com.example.demo.utils.FirebaseUtils;
import com.example.demo.view.admin.UpdateHospital;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements LocationListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FragmentHomeBinding binding;
    private ImageButton btnSos;
    private LocationManager locationManager;
    private ProgressBar progressBar;
    private String address,userId,email;
    private boolean isLocationFound = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnSos = binding.btnSos;
        progressBar = binding.progressBar;
        userId = FirebaseUtils.getFirebaseAuth().getCurrentUser().getUid();
        email = FirebaseUtils.getFirebaseAuth().getCurrentUser().getEmail();
        btnSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Mencari Rumah Sakit Rujukan Terdekat", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                } else {
                    getLocation();
                }
            }
        });
        return root;
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (isLocationFound) { // tambahkan kondisi ini
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        ArrayList<FindLocation> hospitals = new ArrayList<>();
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            CollectionReference hospitalRef = FirebaseUtils.getFirestore().collection("hospital");
            Query query = hospitalRef.orderBy("latitude")
                    .limit(1);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String hospitalId = document.getId();
                        String hospitalName = document.getString("hospitalName");
                        String hospitalAddress = document.getString("address");
                        String hospitalLatitude = document.getString("latitude");
                        String hospitalLongitude = document.getString("longitude");
                        double jarak = getDistance(latitude, longitude, Double.parseDouble(hospitalLatitude), Double.parseDouble(hospitalLongitude));
                        FindLocation hospital = new FindLocation(hospitalId, hospitalName, hospitalAddress, jarak);
                        hospitals.add(hospital);
                    }
                    if(hospitals.size() > 0 ){
                        final Intent intent = new Intent(getContext(), sos.class);
                        intent.putExtra("address", address);
                        intent.putExtra("pasienName", email);
                        intent.putExtra("distance", String.valueOf(hospitals.get(0).getDistance()));
                        intent.putExtra("hospitalName", hospitals.get(0).getHospitalName());
                        intent.putExtra("hospitalId", hospitals.get(0).getHospitalId());
                        intent.putExtra("hospitalAddress", hospitals.get(0).getHospitalAddress());

                        String emergencyId = FirebaseUtils.getFirebaseAuth().getCurrentUser().getUid();
                        DocumentReference documentReference = FirebaseUtils.getFirestore().collection("emergency").document(emergencyId);
                        Map<String,Object> user = new HashMap<>();
                        user.put("emergencyId",emergencyId);
                        user.put("distance",String.valueOf(hospitals.get(0).getDistance()));
                        user.put("userId",userId);
                        user.put("hospitalId",hospitals.get(0).getHospitalId());
                        user.put("latitudeUser",latitude);
                        user.put("longitudeUser",longitude);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: emergency call is created for "+ emergencyId);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Gagal menemukan lokasi", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
            progressBar.setVisibility(View.GONE);
            isLocationFound = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("TAG", "onStatusChanged: Provider Out of Service");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("TAG", "onStatusChanged: Provider Temporarily Unavailable");
                break;
            case LocationProvider.AVAILABLE:
                Log.d("TAG", "onStatusChanged: Provider Available");
                break;
        }
    }


    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance;
    }
}