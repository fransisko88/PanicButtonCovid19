package com.example.demo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.ui.location.LocationFragment;
import com.example.demo.utils.FirebaseUtils;
import com.example.demo.view.admin.AdminActivity;
import com.example.demo.view.admin.UpdateHospital;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class RuteLocation extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String emergencyId,hospitalId,userid,addressPasien,latitudeUser,longitudeUser;
    private TextView pasienName,phoneNumber,address,addressHospital,hospitalName;
    private ImageButton btnBack;
    private Button btnRute,btnDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rute_location);
        pasienName = findViewById(R.id.pasienName);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        btnBack = findViewById(R.id.btnBack);
        addressHospital = findViewById(R.id.addressHospital);
        hospitalName = findViewById(R.id.hospitalName);
        btnRute = findViewById(R.id.btnRute);
        btnDone = findViewById(R.id.btnDone);

        Intent data = getIntent();
        emergencyId = data.getStringExtra("emergencyId");
        hospitalId = data.getStringExtra("hospitalId");
        addressPasien = data.getStringExtra("addressPasien");
        userid = data.getStringExtra("userId");
        latitudeUser = data.getStringExtra("latitudeUser");
        longitudeUser = data.getStringExtra("longitudeUser");

        address.setText(addressPasien);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        FirebaseUtils.getFirestore().collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            pasienName.setText(document.getString("username"));
                            String number = document.getString("phoneNumber");
                            phoneNumber.setText(number.isEmpty() ? "-" : number);
                        } else {
                            Toast.makeText(RuteLocation.this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RuteLocation.this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FirebaseUtils.getFirestore().collection("hospital").document(hospitalId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            hospitalName.setText(document.getString("hospitalName"));
                            String address = document.getString("address");
                            addressHospital.setText(address.isEmpty() ? "-" : address);
                        } else {
                            Toast.makeText(RuteLocation.this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RuteLocation.this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuteLocation.super.onBackPressed();
                finish();
            }
        });

        btnRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitudeUser+","+longitudeUser);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RuteLocation.this)
                        .setMessage("Apakah penjemputan sudah selesai ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DocumentReference emergencyRef = FirebaseUtils.getFirestore().collection("emergency").document(emergencyId);
                                emergencyRef.update("status", "Done")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                Toast.makeText(RuteLocation.this, "Pasien telah selesai di antar ke rumah sakit rujukan ", Toast.LENGTH_SHORT).show();
                                                RuteLocation.super.onBackPressed();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        double latUser = Double.parseDouble(latitudeUser);
        double longiUser = Double.parseDouble(longitudeUser);
        LatLng PosisiUser = new LatLng(latUser, longiUser);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PosisiUser,16));
        MarkerOptions options = new MarkerOptions().position(PosisiUser).title("Lokasi Penjemputan Pasien");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(options);
    }
}