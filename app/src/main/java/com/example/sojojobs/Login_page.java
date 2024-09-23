package com.example.sojojobs;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login_page extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView togglePasswordVisibility;
    private TextView forgotPasswordTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("731723688066-ljkeejm6oun0909vuctpbgb7agll3vgj.apps.googleusercontent.com") // Use Android Client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the Sign Up TextView
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        // Find the Login Button
        Button loginButton = findViewById(R.id.loginButton);
        // Find the Google Sign-In Button
        Button googleSignInButton = findViewById(R.id.googleSignInButton);

        // Set OnClickListener for Sign Up TextView
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Login_page.this, Signup_page.class);
            startActivity(intent);
        });

        // Set OnClickListener for Forgot Password TextView
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Login_page.this, ForgotPassword.class);
            startActivity(intent);
        });

        // Set OnClickListener for Login Button
        loginButton.setOnClickListener(v -> loginUser());

        // Set OnClickListener for Google Sign-In Button
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());
    }


    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to the main activity
                        Toast.makeText(Login_page.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login_page.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(Login_page.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Login_page", "Google sign-in successful, ID token: " + account.getIdToken());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("Login_page", "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to the main activity
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login_page.this, "Google sign-in successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login_page.this, MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(Login_page.this, "Google sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
