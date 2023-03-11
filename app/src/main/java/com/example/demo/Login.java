package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private TextInputEditText txtEmail,txtPassword;
    private Button onSignIn;
    private ProgressBar progressBar;
    private TextView toSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        onSignIn = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        toSignUp = findViewById(R.id.txtSignUp);

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

        onSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    txtEmail.setError("Email cannot be empty!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    txtPassword.setError("Password cannot be empty");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                FirebaseUtils.getFirebaseAuth().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(Login.this,MainActivity.class));
                            Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(Login.this, "Gagal ! Password Salah ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}