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
    private TextView location,namaPasien,hospitalName,hospitalAddress,hospitalId,distance;
    private ImageView picture;
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

        Intent data = getIntent();
        String locationDetail = data.getStringExtra("address");
        String pasienName = data.getStringExtra("pasienName");
        String addressHospital = data.getStringExtra("hospitalAddress");
        String nameHospital = data.getStringExtra("hospitalName");
        String idHospital = data.getStringExtra("hospitalId");
        String distanceHospital = data.getStringExtra("distance");
        location.setText(locationDetail);
        namaPasien.setText(pasienName);
        hospitalName.setText(nameHospital);
        hospitalAddress.setText(addressHospital);
        double d = Double.parseDouble(distanceHospital);
        String formattedDistance = String.format("%.2f", d);
        distance.setText(formattedDistance + " Km");

        getPictureHospital(picture,idHospital);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sos.super.onBackPressed();
                finish();
            }
        });
    }
}