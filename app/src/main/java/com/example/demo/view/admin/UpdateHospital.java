package com.example.demo.view.admin;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.Login;
import com.example.demo.R;
import com.example.demo.ui.beranda.HomeAdminFragment;
import com.example.demo.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UpdateHospital extends AppCompatActivity {
    private static final int GALLERY_REQUEST_PROFILE = 1;
    private Uri mImageUriProfile = null;
    private ImageButton btnBack;
    private TextView btnSave;
    private LinearLayout picture;
    private ImageView iconImage,imageHospital;
    private Button btnDelete;
    private StorageReference profileRef;
    private TextInputEditText txtHospitalName,txtAddress,txtLatitude,txtLongitude;
    String hospitalId,hospitalName,address,latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_hospital);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        txtHospitalName = findViewById(R.id.txtHospitalName);
        txtAddress = findViewById(R.id.txtAddress);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        picture = findViewById(R.id.backgroundSampul);
        imageHospital = findViewById(R.id.photoSampul);
        iconImage = findViewById(R.id.iconImage);
        btnDelete = findViewById(R.id.btnDelete);

        Intent value_data = getIntent();
        hospitalId = value_data.getStringExtra("hospitalId");
        hospitalName = value_data.getStringExtra("hospitalName");
        address = value_data.getStringExtra("address");
        latitude = value_data.getStringExtra("latitude");
        longitude = value_data.getStringExtra("longitude");

        FirebaseUtils.getPictureHospital(imageHospital,hospitalId);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              UpdateHospital.super.onBackPressed();
              finish();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHospital.setImageURI(null);
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra(Intent.ACTION_PICK, true);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_PROFILE);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UpdateHospital.this)
                        .setMessage("Ingin Menghapus data ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteHospital(hospitalId);
                                Intent intent = new Intent(UpdateHospital.this, AdminActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = FirebaseUtils.getFirestore().collection("hospital").document(hospitalId);
                Map<String, Object> updates = new HashMap<>();
                updates.put("hospitalName", txtHospitalName.getText().toString());
                updates.put("address", txtAddress.getText().toString());
                updates.put("latitude", txtLatitude.getText().toString());
                updates.put("longitude", txtLongitude.getText().toString());
                updatePhoto();
                docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateHospital.this, "Data berhasil di edit !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateHospital.this, AdminActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Gagal memperbarui data", e);
                            }
                        });
            }
        });
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

    private void updatePhoto(){

        if(mImageUriProfile != null){
            profileRef = FirebaseUtils.getStorageReference().child("hospital/"+hospitalId+"/profile.jpg");
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
                    Toast.makeText(UpdateHospital.this, "Failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void deleteHospital(String id){
        CollectionReference collectionRef = FirebaseUtils.getFirestore().collection("hospital");
        collectionRef.document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateHospital.this, "Data berhasil di hapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Gagal menghapus dokumen", e);
                    }
                });

    }

    @Override
    protected void onStart(){
        super.onStart();
        txtHospitalName.setText(hospitalName);
        txtAddress.setText(latitude);
        txtLongitude.setText(longitude);
        txtLatitude.setText(latitude);
        txtAddress.setText(address);
    }
}