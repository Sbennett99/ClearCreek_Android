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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.clearcreek.catering.R;
import app.clearcreek.catering.databinding.ActivityForgotPasswordBinding;
import app.clearcreek.catering.utils.StringUtils;
import app.clearcreek.catering.utils.UiUtils;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        Spanned emailLabel = StringUtils.parseHtml(getString(R.string.email_required_label));
        binding.emailLabel.setText(emailLabel);

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
        String email = binding.emailValue.getText().toString().trim();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(R.string.email_error_message);
            return;
        }

        sendResetMail(email);
    }

    private void sendResetMail(String email) {
        ProgressDialog progressDialog = UiUtils.getProgress(this, R.string.loading_message);
        progressDialog.show();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        showToast(R.string.reset_password_email_message);
                        finish();
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
