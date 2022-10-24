package app.clearcreek.catering.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import app.clearcreek.catering.R;
import app.clearcreek.catering.databinding.ActivityRegisterBinding;
import app.clearcreek.catering.utils.StringUtils;
import app.clearcreek.catering.utils.UiUtils;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        progressDialog = UiUtils.getProgress(this, R.string.loading_message);

        Spanned fullNameLabel = StringUtils.parseHtml(getString(R.string.full_name_required_label));
        binding.fullNameLabel.setText(fullNameLabel);

        Spanned emailLabel = StringUtils.parseHtml(getString(R.string.email_required_label));
        binding.emailLabel.setText(emailLabel);

        Spanned passwordLabel = StringUtils.parseHtml(getString(R.string.password_required_label));
        binding.passwordLabel.setText(passwordLabel);

        Spanned confirmPasswordLabel = StringUtils.parseHtml(getString(R.string.confirm_required_label));
        binding.confirmPasswordLabel.setText(confirmPasswordLabel);

        binding.submitButton.setOnClickListener(v -> onSubmit());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            goToMain();
        }
    }

    private void onSubmit() {
        String fullName = binding.fullNameValue.getText().toString().trim();
        String email = binding.emailValue.getText().toString().trim();
        String password = binding.passwordValue.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordValue.getText().toString().trim();

        if (fullName.isEmpty()) {
            showToast(R.string.full_name_error_message);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(R.string.email_error_message);
            return;
        }

        if (password.length() < 8) {
            showToast(R.string.password_error_message);
            return;
        }

        if (!confirmPassword.equals(password)) {
            showToast(R.string.confirm_password_error_message);
            return;
        }

        registerUser(fullName, email, password);
    }

    private void registerUser(String fullName, String email, String password) {
        progressDialog.show();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.isAnonymous()) {
            AuthCredential credentials = EmailAuthProvider.getCredential(email, password);
            currentUser.linkWithCredential(credentials).addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && auth.getCurrentUser() != null) {
                    progressDialog.dismiss();
                    goToMain();
                } else {
                    progressDialog.dismiss();
                    showToast(R.string.authentication_error_message);
                }
            });
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful() && auth.getCurrentUser() != null) {
                            updateUser(auth.getCurrentUser(), fullName);
                        } else {
                            progressDialog.dismiss();
                            showToast(R.string.authentication_error_message);
                        }
                    });
        }
    }

    private void updateUser(FirebaseUser currentUser, String fullName) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build();


        currentUser.updateProfile(request)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
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
