package app.clearcreek.catering.ui.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.databinding.ActivityLoginBinding;
import app.clearcreek.catering.utils.StringUtils;
import app.clearcreek.catering.utils.UiUtils;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        Spanned loginLabel = StringUtils.parseHtml(getString(R.string.username_required_label));
        binding.loginLabel.setText(loginLabel);

        Spanned passwordLabel = StringUtils.parseHtml(getString(R.string.password_required_label));
        binding.passwordLabel.setText(passwordLabel);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.isAnonymous()) {
            binding.guestButton.setVisibility(View.GONE);
            binding.signupButton.setVisibility(View.VISIBLE);
        }

        binding.submitButton.setOnClickListener(v -> onSubmit());
        binding.guestButton.setOnClickListener(v -> onGuest());
        binding.signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        binding.forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            goToMain();
        }
    }

    private void onGuest() {
        ProgressDialog progressDialog = UiUtils.getProgress(this, R.string.loading_message);
        progressDialog.show();

        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && auth.getCurrentUser() != null) {
                progressDialog.dismiss();
                goToMain();
            } else {
                progressDialog.dismiss();
                showToast(R.string.authentication_error_message);
            }
        });
    }

    private void onSubmit() {
        String email = binding.loginValue.getText().toString().trim();
        String password = binding.passwordValue.getText().toString().trim();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(R.string.email_error_message);
            return;
        }

        if (password.length() < 8) {
            showToast(R.string.password_error_message);
            return;
        }

        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        ProgressDialog progressDialog = UiUtils.getProgress(this, R.string.loading_message);
        progressDialog.show();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.isAnonymous()) {
            auth.signOut();
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        progressDialog.dismiss();
                        goToMain();
                    } else {
                        progressDialog.dismiss();
                        showToast(R.string.authentication_error_message);
                    }
                });
    }

    private void showToast(@StringRes int resource) {
        showToast(getString(resource));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
