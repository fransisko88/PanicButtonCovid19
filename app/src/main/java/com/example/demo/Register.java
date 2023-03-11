package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.demo.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import java.util.HashMap;
import java.util.Map;
import static android.content.ContentValues.TAG;
public class Register extends AppCompatActivity {
    private TextInputEditText username,emailUser,passwordUser;
    private Button onSignUp;
    private TextView toSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.txtUsername);
        emailUser = findViewById(R.id.txtEmail);
        passwordUser = findViewById(R.id.txtPassword);
        onSignUp = findViewById(R.id.btnRegister);
        toSignIn = findViewById(R.id.txtSignIn);

        toSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        onSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailUser.getText().toString().trim();
                final String fullName = username.getText().toString();
                String password = passwordUser.getText().toString().trim();
                if(username.getText().toString().isEmpty() || emailUser.getText().toString().isEmpty() || passwordUser.getText().toString().isEmpty() ){
                    Toast.makeText(Register.this, "Data Tidak Boleh Kosong !", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUtils.getFirebaseAuth().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful()){
                            FirebaseUtils.getFirebaseUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(Register.this, "Silahankan Cek Email Anda Untuk Verifikasi", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error: Email tidak Terkirim " + e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this, "Perdaftaran Berhasil", Toast.LENGTH_SHORT).show();
                            String userID = FirebaseUtils.getFirebaseAuth().getCurrentUser().getUid();
                            DocumentReference documentReference = FirebaseUtils.getFirestore().collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("username",fullName);
                            user.put("email",email);
                            user.put("userId",userID);
                            user.put("password",password);
                            username.setText("");
                            emailUser.setText("");
                            passwordUser.setText("");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            finish();
                        }else {
                            Toast.makeText(Register.this, "Error : Email Sudah Terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}