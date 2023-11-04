package com.example.demo;

import static com.example.demo.utils.FirebaseUtils.getPictureHospital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class sos extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView location,namaPasien,hospitalName,hospitalAddress,distance,hospitalName2,hospitalAddress2,distance2;
    private ImageView picture,picture2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        btnBack = findViewById(R.id.btnBack);
        location = findViewById(R.id.detailAddress);
        namaPasien = findViewById(R.id.namaPasien);
        hospitalAddress = findViewById(R.id.hospitalAddress);
        hospitalName = findViewById(R.id.hospitalName);
        distance = findViewById(R.id.distance);
        picture = findViewById(R.id.backgroundSampul);
        hospitalAddress2 = findViewById(R.id.hospitalAddress2);
        hospitalName2 = findViewById(R.id.hospitalName2);
        distance2 = findViewById(R.id.distance2);
        picture2 = findViewById(R.id.backgroundSampul2);

        Intent data = getIntent();
        String locationDetail = data.getStringExtra("address");
        String pasienName = data.getStringExtra("pasienName");
        String addressHospital = data.getStringExtra("hospitalAddress");
        String nameHospital = data.getStringExtra("hospitalName");
        String idHospital = data.getStringExtra("hospitalId");
        String distanceHospital = data.getStringExtra("distance");
        String addressHospital2 = data.getStringExtra("hospitalAddress2");
        String nameHospital2 = data.getStringExtra("hospitalName2");
        String idHospital2 = data.getStringExtra("hospitalId2");
        String distanceHospital2 = data.getStringExtra("distance2");
        location.setText(locationDetail);
        namaPasien.setText(pasienName);
        hospitalName.setText(nameHospital);
        hospitalAddress.setText(addressHospital);
        double d = Double.parseDouble(distanceHospital);
        String formattedDistance = String.format("%.2f", d);
        distance.setText(formattedDistance + " Km");
        hospitalName2.setText(nameHospital2);
        hospitalAddress2.setText(addressHospital2);
        double d2 = Double.parseDouble(distanceHospital2);
        String formattedDistance2 = String.format("%.2f", d2);
        distance2.setText(formattedDistance2 + " Km");

        getPictureHospital(picture,idHospital);
        getPictureHospital(picture2,idHospital2);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sos.super.onBackPressed();
                finish();
            }
        });
    }
}