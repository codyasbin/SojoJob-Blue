package com.example.sojojobs.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sojojobs.R;
import com.example.sojojobs.adapters.JobAdapterCrud;
import com.example.sojojobs.models.Job;
import com.example.sojojobs.utils.FirestoreHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class JobPostingFragment extends Fragment {

    private EditText etJobTitle, etJobDescription, etCompanyName, etLocation, etSalary;
    private Button btnSaveJob;
    private RecyclerView recyclerViewJobs;
    private JobAdapterCrud jobAdapter;
    private List<Job> jobList = new ArrayList<>();
    private FirestoreHelper firestoreHelper;
    private FirebaseUser currentUser;
    private DocumentSnapshot lastVisible;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_posting, container, false);

        etJobTitle = view.findViewById(R.id.et_job_title);
        etJobDescription = view.findViewById(R.id.et_job_description);
        etCompanyName = view.findViewById(R.id.et_company_name);
        etLocation = view.findViewById(R.id.et_location);
        etSalary = view.findViewById(R.id.et_salary);
        btnSaveJob = view.findViewById(R.id.btn_save_job);
        recyclerViewJobs = view.findViewById(R.id.recycler_view_jobs);

        firestoreHelper = new FirestoreHelper();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        jobAdapter = new JobAdapterCrud(jobList, getChildFragmentManager(), this);
        recyclerViewJobs.setAdapter(jobAdapter);

        btnSaveJob.setOnClickListener(v -> postJob());

        loadJobs();  // Load jobs from Firestore when the fragment is created

        return view;
    }

    // Updated postJob method
    private void postJob() {
        String jobTitle = etJobTitle.getText().toString().trim();
        String jobDescription = etJobDescription.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();

        if (TextUtils.isEmpty(jobTitle) || TextUtils.isEmpty(jobDescription) || TextUtils.isEmpty(companyName) ||
                TextUtils.isEmpty(location) || TextUtils.isEmpty(salary)) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(getActivity(), "You must be logged in to post a job", Toast.LENGTH_SHORT).show();
            return;
        }

        String postedBy = currentUser.getUid();
        Timestamp postedDate = new Timestamp(new java.util.Date());

        String jobId = firestoreHelper.getJobsCollection().document().getId(); // Generate unique ID
        Job job = new Job(jobId, jobTitle, jobDescription, companyName, location, salary, postedBy, postedDate);

        firestoreHelper.addJob(job)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Job posted successfully!", Toast.LENGTH_SHORT).show();
                    loadJobs();  // Reload the job list after posting a new job
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to post job", Toast.LENGTH_SHORT).show());
    }

    // Updated showEditJobDialog method
    public void showEditJobDialog(Job job) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_job, null);
        EditText etJobTitle = dialogView.findViewById(R.id.et_edit_job_title);
        EditText etJobDescription = dialogView.findViewById(R.id.et_edit_job_description);
        EditText etCompanyName = dialogView.findViewById(R.id.et_edit_company_name);
        EditText etLocation = dialogView.findViewById(R.id.et_edit_location);
        EditText etSalary = dialogView.findViewById(R.id.et_edit_salary);

        etJobTitle.setText(job.getJobTitle());
        etJobDescription.setText(job.getJobDescription());
        etCompanyName.setText(job.getCompanyName());
        etLocation.setText(job.getLocation());
        etSalary.setText(job.getSalary());

        builder.setView(dialogView)
                .setTitle("Edit Job")
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etJobTitle.getText().toString().trim();
                    String description = etJobDescription.getText().toString().trim();
                    String company = etCompanyName.getText().toString().trim();
                    String loc = etLocation.getText().toString().trim();
                    String sal = etSalary.getText().toString().trim();

                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(company) ||
                            TextUtils.isEmpty(loc) || TextUtils.isEmpty(sal)) {
                        Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    job.setJobTitle(title);
                    job.setJobDescription(description);
                    job.setCompanyName(company);
                    job.setLocation(loc);
                    job.setSalary(sal);

                    firestoreHelper.updateJob(job.getJobId(), job) // Use jobId for update
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Job updated successfully!", Toast.LENGTH_SHORT).show();
                                loadJobs();  // Reload the job list after updating
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update job", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Updated deleteJob method
    public void deleteJob(Job job) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    firestoreHelper.deleteJob(job.getJobId()) // Use jobId for delete
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Job deleted successfully!", Toast.LENGTH_SHORT).show();
                                loadJobs();  // Reload the job list after deleting
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete job", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Updated loadJobs method
    private void loadJobs() {
        firestoreHelper.getJobs(lastVisible, 10) // Set limit to 10 jobs per page
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Job job = documentSnapshot.toObject(Job.class);
                            if (job != null) {
                                job.setJobId(documentSnapshot.getId()); // Ensure jobId is set
                                jobList.add(job);
                            }
                        }
                        jobAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to load jobs", Toast.LENGTH_SHORT).show());
    }
}
