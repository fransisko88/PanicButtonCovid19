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
import com.example.demo.model.Hospital;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HomeFragment extends Fragment implements LocationListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FragmentHomeBinding binding;
    private ImageButton btnSos;
    private LocationManager locationManager;
    private ProgressBar progressBar;
    private String userId,email,address;
    private boolean isLocationFound = false;
    private Double latitude,longitude;
    private FirebaseFirestore db;
    private CollectionReference hospitalCollection;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnSos = binding.btnSos;
        progressBar = binding.progressBar;
        db = FirebaseFirestore.getInstance();
        hospitalCollection = db.collection("hospital");
        if(FirebaseUtils.getFirebaseUser() != null){
            userId = FirebaseUtils.getFirebaseAuth().getCurrentUser().getUid();
            email = FirebaseUtils.getFirebaseAuth().getCurrentUser().getEmail();
        }

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
        if (isLocationFound || userId.isEmpty() || email.isEmpty()) {
            return;
        }
         latitude = location.getLatitude();
         longitude = location.getLongitude();
         Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            findNearestHospital(latitude,longitude);
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

//    private void findNearestHospital(double userLat, double userLng) {
//        db = FirebaseFirestore.getInstance();
//        hospitalCollection = db.collection("hospital");
//
//        // Membuat query untuk mendapatkan semua rumah sakit
//        hospitalCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
//            List<DocumentSnapshot> hospitals = queryDocumentSnapshots.getDocuments();
//
//            // Mengurutkan rumah sakit berdasarkan jaraknya dari posisi pengguna
//            Collections.sort(hospitals, (h1, h2) -> {
//                double h1Lat = Double.parseDouble(h1.getString("latitude"));
//                double h1Lng = Double.parseDouble(h1.getString("longitude"));
//                double h2Lat = Double.parseDouble(h2.getString("latitude"));
//                double h2Lng = Double.parseDouble(h2.getString("longitude"));
//
//                double distToH1 = distance(userLat, userLng, h1Lat, h1Lng);
//                double distToH2 = distance(userLat, userLng, h2Lat, h2Lng);
//
//                return Double.compare(distToH1, distToH2);
//            });
//
//            // Mengambil rumah sakit terdekat
//            String nearestHospitalName = "";
//            String nearestHospitalId = "";
//            double jarak = 0;
//            if (!hospitals.isEmpty()) {
//                DocumentSnapshot nearestHospital = hospitals.get(0);
//                nearestHospitalName = nearestHospital.getString("hospitalName");
//                nearestHospitalId = nearestHospital.getId();
//                double nearestHospitalLat = Double.parseDouble(nearestHospital.getString("latitude"));
//                double nearestHospitalLng = Double.parseDouble(nearestHospital.getString("longitude"));
//                jarak = distance(userLat, userLng, nearestHospitalLat, nearestHospitalLng);
//                saveEmergency(jarak,nearestHospitalName,nearestHospitalId,nearestHospital.getString("address"));
//            } else {
//                nearestHospitalName = "Tidak Ditemukan";
//            }
//            Toast.makeText(getActivity(), "Rumah Sakit Rujukan :  " + nearestHospitalName, Toast.LENGTH_SHORT).show();
//            progressBar.setVisibility(View.GONE);
//            isLocationFound = true;
//        });
//    }

    private void findNearestHospital(double userLat, double userLng) {
        db = FirebaseFirestore.getInstance();
        hospitalCollection = db.collection("hospital");

        // Membuat query untuk mendapatkan semua rumah sakit
        hospitalCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> hospitals = queryDocumentSnapshots.getDocuments();

            // Mengurutkan rumah sakit berdasarkan jaraknya dari posisi pengguna
            Collections.sort(hospitals, (h1, h2) -> {
                double h1Lat = Double.parseDouble(h1.getString("latitude"));
                double h1Lng = Double.parseDouble(h1.getString("longitude"));
                double h2Lat = Double.parseDouble(h2.getString("latitude"));
                double h2Lng = Double.parseDouble(h2.getString("longitude"));

                double distToH1 = distance(userLat, userLng, h1Lat, h1Lng);
                double distToH2 = distance(userLat, userLng, h2Lat, h2Lng);

                return Double.compare(distToH1, distToH2);
            });

            // Mengambil informasi dari dua rumah sakit terdekat
            List<Double> distances = new ArrayList<>();
            List<String> hospitalNames = new ArrayList<>();
            List<String> hospitalIds = new ArrayList<>();
            List<String> hospitalAddresses = new ArrayList<>();
            int count = Math.min(2, hospitals.size());
            for (int i = 0; i < count; i++) {
                DocumentSnapshot hospital = hospitals.get(i);
                double hospitalLat = Double.parseDouble(hospital.getString("latitude"));
                double hospitalLng = Double.parseDouble(hospital.getString("longitude"));
                double distToHospital = distance(userLat, userLng, hospitalLat, hospitalLng);
                distances.add(distToHospital);
                hospitalNames.add(hospital.getString("hospitalName"));
                hospitalIds.add(hospital.getId());
                hospitalAddresses.add(hospital.getString("address"));
            }

            saveEmergency(distances, hospitalNames, hospitalIds, hospitalAddresses);

            // Menampilkan pesan Toast dengan nama rumah sakit terdekat
            String nearestHospitalName = count > 0 ? hospitalNames.get(0) : "Tidak Ditemukan";
            Toast.makeText(getActivity(), "Rumah Sakit Rujukan :  " + nearestHospitalName, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            isLocationFound = true;
        });
    }



    // Method untuk menghitung jarak antara dua titik
    private double distance(double lat1, double lng1, double lat2, double lng2) {
        int R = 6371; // Jari-jari bumi dalam kilometer
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = R * c;
        return dist;
    }

//    public void saveEmergency(double distance,String hospitalName,String hospitalId,String hospitalAddress){
//        String emergencyId = UUID.randomUUID().toString();
//        DocumentReference documentReference = FirebaseUtils.getFirestore().collection("emergency").document(emergencyId);
//        Map<String,Object> user = new HashMap<>();
//        user.put("emergencyId",emergencyId);
//        user.put("distance",String.valueOf(distance));
//        user.put("userId",userId);
//        user.put("hospitalId",hospitalId);
//        user.put("latitudeUser",latitude);
//        user.put("longitudeUser",longitude);
//        user.put("address",address);
//        user.put("status","Pending");
//        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "onSuccess: emergency call is created for "+ emergencyId);
//                final Intent intent = new Intent(getContext(), sos.class);
//                intent.putExtra("address", address);
//                intent.putExtra("pasienName", email);
//                intent.putExtra("distance", String.valueOf(distance));
//                intent.putExtra("hospitalName",hospitalName);
//                intent.putExtra("hospitalId", hospitalId);
//                intent.putExtra("hospitalAddress", hospitalAddress);
//                intent.putExtra("hospitalId2", hospitalId);
//                intent.putExtra("distance2", String.valueOf(distance));
//                intent.putExtra("hospitalName2",hospitalName);
//                intent.putExtra("hospitalAddress2", hospitalAddress);
//                startActivity(intent);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e.toString());
//            }
//        });
//    }

    public void saveEmergency(List<Double> distances, List<String> hospitalNames, List<String> hospitalIds, List<String> hospitalAddresses) {
        String emergencyId = UUID.randomUUID().toString();
        DocumentReference documentReference = FirebaseUtils.getFirestore().collection("emergency").document(emergencyId);
        Map<String, Object> user = new HashMap<>();
        user.put("emergencyId", emergencyId);
        user.put("distance", String.valueOf(distances.get(0)));
        user.put("userId", userId);
        user.put("hospitalId", hospitalIds.get(0));
        user.put("latitudeUser", latitude);
        user.put("longitudeUser", longitude);
        user.put("address", address);
        user.put("status", "Pending");

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: emergency call is created for " + emergencyId);
                final Intent intent = new Intent(getContext(), sos.class);
                intent.putExtra("address", address);
                intent.putExtra("pasienName", email);
                intent.putExtra("distance", String.valueOf(distances.get(0)));
                intent.putExtra("hospitalName", hospitalNames.get(0));
                intent.putExtra("hospitalId", hospitalIds.get(0));
                intent.putExtra("hospitalAddress", hospitalAddresses.get(0));
                intent.putExtra("distance2", String.valueOf(distances.get(1)));
                intent.putExtra("hospitalName2", hospitalNames.get(1));
                intent.putExtra("hospitalId2", hospitalIds.get(1));
                intent.putExtra("hospitalAddress2", hospitalAddresses.get(1));
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }


}