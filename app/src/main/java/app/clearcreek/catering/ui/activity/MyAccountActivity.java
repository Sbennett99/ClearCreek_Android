package app.clearcreek.catering.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.databinding.ActivityMyAccountBinding;
import app.clearcreek.catering.utils.StringUtils;
import app.clearcreek.catering.utils.UiUtils;

public class MyAccountActivity extends AppCompatActivity {

    private ActivityMyAccountBinding binding;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        progressDialog = UiUtils.getProgress(this, R.string.loading_message);

        if (auth.getCurrentUser() != null) {
            binding.fullNameValue.setText(auth.getCurrentUser().getDisplayName());
            binding.emailValue.setText(auth.getCurrentUser().getEmail());
        }

        Spanned oldPasswordLabel = StringUtils.parseHtml(getString(R.string.old_password_required_label));
        binding.oldPasswordLabel.setText(oldPasswordLabel);

        Spanned passwordLabel = StringUtils.parseHtml(getString(R.string.password_required_label));
        binding.passwordLabel.setText(passwordLabel);

        Spanned confirmPasswordLabel = StringUtils.parseHtml(getString(R.string.confirm_required_label));
        binding.confirmPasswordLabel.setText(confirmPasswordLabel);

        binding.updatePasswordButton.setOnClickListener(v -> {
            binding.updatePasswordButton.setVisibility(View.GONE);
            binding.updatePasswordLayout.setVisibility(View.VISIBLE);
        });

        binding.updateButton.setOnClickListener(v -> onUpdatePassword());
        binding.signOutButton.setOnClickListener(v -> onSignOut());
    }

    private void onUpdatePassword() {
        String currentPassword = binding.oldPasswordValue.getText().toString().trim();
        String newPassword = binding.passwordValue.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordValue.getText().toString().trim();

        if (currentPassword.length() < 8) {
            showToast(R.string.old_password_error_message);
            return;
        }

        if (newPassword.length() < 8) {
            showToast(R.string.password_error_message);
            return;
        }

        if (!confirmPassword.equals(newPassword)) {
            showToast(R.string.confirm_password_error_message);
            return;
        }

        updatePassword(currentPassword, newPassword);
    }

    private void updatePassword(String currentPassword, String newPassword) {
        progressDialog.show();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            updatePassword(user, newPassword);
                        } else {
                            progressDialog.hide();
                            showToast(R.string.authentication_error_message);
                        }
                    });
        }
    }

    private void updatePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(this, task -> {
                    progressDialog.hide();
                    if (task.isSuccessful()) {
                        showToast(R.string.password_updated_message);
                        binding.updatePasswordButton.setVisibility(View.VISIBLE);
                        binding.updatePasswordLayout.setVisibility(View.GONE);
                    } else {
                        showToast(R.string.authentication_error_message);
                    }
                });
    }

    private void onSignOut() {
        auth.signOut();
        AppController.getPreferencesHelper().clearCart();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void showToast(@StringRes int resource) {
        showToast(getString(resource));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
