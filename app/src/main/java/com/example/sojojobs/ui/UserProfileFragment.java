package com.example.sojojobs.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.sojojobs.R;
import com.example.sojojobs.models.UserProfile;
import com.example.sojojobs.utils.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class UserProfileFragment extends Fragment {

    private LinearLayout layoutProfileView, layoutProfileForm;
    private ImageView ivProfileImageDisplay, ivProfileImage;
    private TextView tvFullName, tvEmail, tvPhone;
    private EditText etFullName, etEmail, etPhone;
    private Button btnEditProfile, btnSaveProfile;
    private FirestoreHelper firestoreHelper;
    private FirebaseUser currentUser;
    private Uri profileImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        layoutProfileView = view.findViewById(R.id.layout_profile_view);
        layoutProfileForm = view.findViewById(R.id.layout_profile_form);
        ivProfileImageDisplay = view.findViewById(R.id.iv_profile_image_display);
        ivProfileImage = view.findViewById(R.id.iv_profile_image);
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        etFullName = view.findViewById(R.id.et_full_name);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSaveProfile = view.findViewById(R.id.btn_save_profile);

        firestoreHelper = new FirestoreHelper();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnEditProfile.setOnClickListener(v -> switchToEditMode());
        ivProfileImage.setOnClickListener(v -> chooseImage());
        btnSaveProfile.setOnClickListener(v -> saveUserProfile());

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        String userId = currentUser.getUid();
        firestoreHelper.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                if (userProfile != null) {
                    displayProfile(userProfile);
                }
            } else {
                switchToEditMode();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            switchToEditMode();
        });
    }

    private void displayProfile(UserProfile userProfile) {
        layoutProfileView.setVisibility(View.VISIBLE);
        layoutProfileForm.setVisibility(View.GONE);

        tvFullName.setText(userProfile.getFullName());
        tvEmail.setText(userProfile.getEmail());
        tvPhone.setText(userProfile.getPhoneNumber());

        Glide.with(this).load(userProfile.getProfileImageUrl()).into(ivProfileImageDisplay);
    }

    private void switchToEditMode() {
        layoutProfileView.setVisibility(View.GONE);
        layoutProfileForm.setVisibility(View.VISIBLE);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            Glide.with(this).load(profileImageUri).into(ivProfileImage);
        }
    }

    private void saveUserProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || profileImageUri == null) {
            Toast.makeText(getActivity(), "All fields and profile image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        final String userId = currentUser.getUid();
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profileImages/" + UUID.randomUUID().toString());

        profileImageRef.putFile(profileImageUri).addOnSuccessListener(taskSnapshot -> {
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                UserProfile userProfile = new UserProfile(
                        userId,
                        fullName,
                        email,
                        phone,
                        imageUrl
                );

                firestoreHelper.addUserProfile(userProfile).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    displayProfile(userProfile);
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed to upload profile image", Toast.LENGTH_SHORT).show();
        });
    }
}
