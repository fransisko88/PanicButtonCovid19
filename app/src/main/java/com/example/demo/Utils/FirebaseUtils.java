package com.example.demo.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {
    private static FirebaseAuth fAuth;
    private static FirebaseFirestore fStore;

    public static FirebaseUser getFirebaseUser() {
        FirebaseAuth mAuth = getFirebaseAuth();
        if (mAuth != null) {
            return mAuth.getCurrentUser();
        }
        return null;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (fAuth == null) {
            fAuth = FirebaseAuth.getInstance();
        }
        return fAuth;
    }

    public static FirebaseFirestore getFirestore() {
        if (fStore == null) {
            fStore = FirebaseFirestore.getInstance();
        }
        return fStore;
    }
}
