package com.example.sojojobs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sojojobs.R;
import com.example.sojojobs.adapters.ApplicationAdapter;
import com.example.sojojobs.models.Application;
import com.example.sojojobs.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApplicationListingFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ApplicationAdapter applicationAdapter;
    private List<Application> applicationList = new ArrayList<>();
    private FirestoreHelper firestoreHelper = new FirestoreHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_listing, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_applications);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();
        loadApplications();

        return view;
    }

    private void setupRecyclerView() {
        applicationAdapter = new ApplicationAdapter(getContext(), applicationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(applicationAdapter);
    }

    private void loadApplications() {
        progressBar.setVisibility(View.VISIBLE);

        firestoreHelper.getApplications().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    List<Application> applications = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Application application = document.toObject(Application.class);
                        applications.add(application);
                    }
                    applicationList.clear();
                    applicationList.addAll(applications);
                    applicationAdapter.notifyDataSetChanged();
                } else {
                    // Handle the error
                }
            }
        });
    }
}
