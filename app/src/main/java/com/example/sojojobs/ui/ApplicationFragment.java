package com.example.sojojobs.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sojojobs.R;
import com.example.sojojobs.models.Application;
import com.example.sojojobs.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ApplicationFragment extends Fragment {

    private static final int PICK_FILE_REQUEST = 1;

    private EditText etName, etEmail, etPhone;
    private Button btnSelectCv, btnApply;
    private TextView tvCvFileName;
    private ProgressBar progressBar;
    private Uri fileUri;

    private FirebaseStorage storage;
    private FirestoreHelper firestoreHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application, container, false);

        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        btnSelectCv = view.findViewById(R.id.btn_select_cv);
        btnApply = view.findViewById(R.id.btn_apply);
        tvCvFileName = view.findViewById(R.id.tv_cv_file_name);
        progressBar = view.findViewById(R.id.progressBar);

        storage = FirebaseStorage.getInstance();
        firestoreHelper = new FirestoreHelper();

        btnSelectCv.setOnClickListener(v -> openFilePicker());
        btnApply.setOnClickListener(v -> applyForJob());

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            tvCvFileName.setText(fileUri.getLastPathSegment()); // Show the selected file name
        }
    }

    private void applyForJob() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        String fileName = UUID.randomUUID().toString();
        StorageReference fileReference = storage.getReference("cvs/" + fileName);

        if (fileUri != null) {
            fileReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        Application application = new Application(
                                etName.getText().toString(),
                                etEmail.getText().toString(),
                                etPhone.getText().toString(),
                                downloadUri.toString()
                        );

                        firestoreHelper.addApplication(application)
                                .addOnSuccessListener(aVoid -> {
                                    progressBar.setVisibility(View.GONE); // Hide progress bar
                                    Toast.makeText(getContext(), "Application submitted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE); // Hide progress bar
                                    Toast.makeText(getContext(), "Failed to submit application", Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        Toast.makeText(getContext(), "Failed to upload CV", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE); // Hide progress bar if no file is selected
        }
    }
}
