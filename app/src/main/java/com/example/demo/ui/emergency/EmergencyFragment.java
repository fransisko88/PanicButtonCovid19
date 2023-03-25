package com.example.demo.ui.emergency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.adapter.AdapterEmergency;
import com.example.demo.adapter.AdapterHospital;
import com.example.demo.model.Emergency;
import com.example.demo.model.Hospital;
import com.example.demo.utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Emergency> emergencies;
    private RecyclerView recyclerView;
    private AdapterEmergency adapterEmergency;

    public EmergencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmergencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyFragment newInstance(String param1, String param2) {
        EmergencyFragment fragment = new EmergencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_emergency);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        emergencies = new ArrayList<Emergency>();
        emergency();
        adapterEmergency = new AdapterEmergency(getActivity(),emergencies);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterEmergency);

    }

    private void emergency(){
        FirebaseUtils.getFirestore().collection("emergency").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.getDocumentChanges() != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Emergency call = dc.getDocument().toObject(Emergency.class);
                            if (call.getStatus() != null && call.getStatus().equals("Pending")) {
                                emergencies.add(call);
                                adapterEmergency.notifyDataSetChanged();
                            }
                        }
                    }
                }
                if(emergencies.size() == 0){
                    Toast.makeText(getActivity(), "Data emergency kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}