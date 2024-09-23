package com.example.sojojobs.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sojojobs.R;
import com.example.sojojobs.adapters.JobAdapter;
import com.example.sojojobs.models.Job;
import com.example.sojojobs.utils.FirestoreHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JobListFragment extends Fragment {

    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<Job> jobList = new ArrayList<>();
    private FirestoreHelper firestoreHelper;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;
    private Button loadMoreButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_jobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        jobAdapter = new JobAdapter(jobList, getParentFragmentManager());
        recyclerView.setAdapter(jobAdapter);

        firestoreHelper = new FirestoreHelper();

        loadMoreButton = view.findViewById(R.id.load_more_button);
        progressBar = view.findViewById(R.id.progress_bar);

        loadMoreButton.setOnClickListener(v -> loadMoreJobs());

        loadJobs();

        return view;
    }

    private void loadJobs() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        firestoreHelper.getJobs(null, 3).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                jobList.clear();
                jobList.addAll(queryDocumentSnapshots.toObjects(Job.class));
                if (queryDocumentSnapshots.size() > 0) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                }
                jobAdapter.notifyDataSetChanged();
                updateLoadMoreVisibility();
            }
            isLoading = false;
            progressBar.setVisibility(View.GONE);
        });
    }

    private void loadMoreJobs() {
        if (isLoading || lastVisible == null) {
            return;
        }

        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        firestoreHelper.getJobs(lastVisible, 3).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                jobList.addAll(queryDocumentSnapshots.toObjects(Job.class));
                if (queryDocumentSnapshots.size() > 0) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                }
                jobAdapter.notifyDataSetChanged();
                updateLoadMoreVisibility();
            }
            isLoading = false;
            progressBar.setVisibility(View.GONE);
        });
    }

    private void updateLoadMoreVisibility() {
            loadMoreButton.setVisibility(View.VISIBLE);
    }
}
