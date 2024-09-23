package com.example.sojojobs.utils;

import com.example.sojojobs.models.Application;
import com.example.sojojobs.models.Job;
import com.example.sojojobs.models.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreHelper {

    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Add a job with an auto-generated ID
    public Task<DocumentReference> addJob(Job job) {
        CollectionReference jobsRef = db.collection("jobs");
        return jobsRef.add(job).addOnSuccessListener(documentReference -> {
            // Set the jobId field in the Job object after Firestore generates it
            job.setJobId(documentReference.getId());
            documentReference.set(job);
        });
    }

    public CollectionReference getJobsCollection() {
        return db.collection("jobs");
    }

    // Get jobs with pagination
    public Task<QuerySnapshot> getJobs(DocumentSnapshot lastVisible, int limit) {
        CollectionReference jobsRef = db.collection("jobs");
        Query query = jobsRef.orderBy("postedDate", Query.Direction.DESCENDING)
                .limit(limit);
        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }
        return query.get();
    }

    // Add a new application
    public Task<DocumentReference> addApplication(Application application) {
        CollectionReference applicationsRef = db.collection("applications");
        return applicationsRef.add(application);
    }

    // Get all applications
    public Task<QuerySnapshot> getApplications() {
        return db.collection("applications").get();
    }

    // Get an application by applicationId
    public Task<DocumentSnapshot> getApplication(String applicationId) {
        return db.collection("applications").document(applicationId).get();
    }

    // Add or update user profile
    public Task<Void> addUserProfile(UserProfile userProfile) {
        CollectionReference profilesRef = db.collection("userProfiles");
        return profilesRef.document(userProfile.getUserId()).set(userProfile);
    }

    // Get a user profile by userId
    public Task<DocumentSnapshot> getUserProfile(String userId) {
        return db.collection("userProfiles").document(userId).get();
    }

    // Update a job by jobId
    public Task<Void> updateJob(String jobId, Job job) {
        return db.collection("jobs").document(jobId).set(job);
    }

    // Delete a job by jobId
    public Task<Void> deleteJob(String jobId) {
        return db.collection("jobs").document(jobId).delete();
    }
}
