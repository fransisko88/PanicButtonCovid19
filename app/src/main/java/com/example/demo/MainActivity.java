package com.example.demo;

import android.content.Intent;
import android.os.Bundle;

import com.example.demo.utils.FirebaseUtils;
import com.example.demo.view.admin.AdminActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.demo.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseUtils.getFirebaseUser();
        if (currentUser != null) {
            if(currentUser.getEmail().equalsIgnoreCase("fransiskosihombing@gmail.com")){
                Intent loginIntent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }else{
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
    }
}