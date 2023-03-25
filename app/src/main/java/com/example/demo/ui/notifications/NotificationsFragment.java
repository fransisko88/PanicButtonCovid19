package com.example.demo.ui.notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.Login;
import com.example.demo.databinding.FragmentNotificationsBinding;
import com.example.demo.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private String userId;
    private TextInputEditText txtEmail,txtFullname,txtPhoneNumber;
    private DocumentReference docRef;
    private TextView btnSave;
    private Button btnLogout;
    private String TAG;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        txtEmail = binding.txtEmail;
        txtFullname = binding.txtFullname;
        txtPhoneNumber = binding.txtPhoneNumber;
        btnSave = binding.btnSave;
        btnLogout = binding.btnLogout;

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtFullname.getText().toString().isEmpty() || txtPhoneNumber.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else{
                    update();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Ingin keluar aplikasi ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseUtils.logout();
                                Intent intent = new Intent(getActivity(), Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        return root;
    }

    public void onStart(){
        super.onStart();
        userId = FirebaseUtils.getFirebaseAuth().getCurrentUser().getUid();
        docRef = FirebaseUtils.getFirestore().collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        txtEmail.setText(document.get("email").toString());
                        txtFullname.setText(document.get("username").toString());
                        txtPhoneNumber.setText(document.get("phoneNumber").toString());
                    } else {
                        Log.d(TAG, "Dokumen tidak ditemukan");
                    }
                } else {
                    Log.d(TAG, "Gagal mendapatkan dokumen: ", task.getException());
                }
            }
        });

    }

    private void update(){
        DocumentReference userRef = FirebaseUtils.getFirestore().collection("users").document(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", txtFullname.getText().toString());
        updates.put("phoneNumber", txtPhoneNumber.getText().toString());
        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}