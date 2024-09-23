package com.example.sojojobs;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Signup_page extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText passwordEditText;
    private ImageView togglePasswordVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page);

        mAuth = FirebaseAuth.getInstance();
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.signUpButton).setOnClickListener(v -> createAccount());
        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());

        // Set up the click listener for the "Sign In" text view
        TextView signInTextView = findViewById(R.id.signInTextView);
        signInTextView.setOnClickListener(v -> {
            // Navigate to Login_page activity
            startActivity(new Intent(Signup_page.this, Login_page.class));
            finish();  // Optional: Call finish() to close the current activity if desired
        });
    }

    private void createAccount() {
        String fullName = ((EditText) findViewById(R.id.fullNameEditText)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            // Show error message for empty fields
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            Toast.makeText(Signup_page.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to Login_page activity
                            startActivity(new Intent(Signup_page.this, Login_page.class));

                            finish();  // Optional: Call finish() to close the current activity if desired

                        } else {
                            // If registration fails, display a message to the user
                            Toast.makeText(Signup_page.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.baseline_remove_red_eye_24);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.baseline_remove_red_eye_24);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }
}
