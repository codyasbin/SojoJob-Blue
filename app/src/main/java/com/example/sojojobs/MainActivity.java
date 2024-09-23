package com.example.sojojobs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.sojojobs.ui.ApplicationFragment;
import com.example.sojojobs.ui.ApplicationListingFragment;
import com.example.sojojobs.ui.JobListFragment;
import com.example.sojojobs.ui.JobPostingFragment;
import com.example.sojojobs.ui.UserProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load the default fragment when the app starts
        if (savedInstanceState == null) {
            loadFragment(new JobListFragment());
            navigationView.setCheckedItem(R.id.item_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;


        // Handle the item selection using if-else
        if (item.getItemId() == R.id.item_home) {
            selectedFragment = new JobListFragment();
        } else if (item.getItemId() == R.id.item_Applicants) {
            selectedFragment = new ApplicationListingFragment();
        } else if (item.getItemId() == R.id.item_profile) {
            selectedFragment = new UserProfileFragment();
        } else if (item.getItemId() == R.id.item_company) {
            selectedFragment = new JobPostingFragment();
        } else if (item.getItemId() == R.id.item_logout) {
            handleLogout();
            return true;
        }


        // Replace the current fragment with the selected fragment
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to handle logout
    private void handleLogout() {
        // Perform the logout process (e.g., FirebaseAuth sign out)
        FirebaseAuth.getInstance().signOut();

        // Optionally, navigate to a login screen or other action
        Intent intent = new Intent(this, Login_page.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    // Method to load the selected fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Optional: Add transaction to back stack
        transaction.commit();
    }



}
