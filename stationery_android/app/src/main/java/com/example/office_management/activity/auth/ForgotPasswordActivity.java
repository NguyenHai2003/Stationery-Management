package com.example.office_management.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.office_management.R;

import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.LoginApi;
import com.example.office_management.dto.request.ForgotPasswordRequest;
import com.example.office_management.dto.request.OtpVerificationRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail, edtNewPassword;
    private TextView tvSendOtp;
    private LoginApi apiService;
    private Button btnConfirm;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = BaseURL.getUrl(this).create(LoginApi.class);

        edtEmail = findViewById(R.id.edtEmail);
        tvSendOtp = findViewById(R.id.tvSendOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btn_back);

        EditText otp1 = findViewById(R.id.otp1);
        EditText otp2 = findViewById(R.id.otp2);
        EditText otp3 = findViewById(R.id.otp3);
        EditText otp4 = findViewById(R.id.otp4);
        EditText otp5 = findViewById(R.id.otp5);
        EditText otp6 = findViewById(R.id.otp6);

        tvSendOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                forgotPassword(email);
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();

            String otpString = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString()
                    + otp5.getText().toString()
                    + otp6.getText().toString();

            if (email.isEmpty() || newPassword.isEmpty() || otpString.length() != 6) {
                Toast.makeText(this, "Please enter complete information", Toast.LENGTH_SHORT).show();
                return;
            }

            int otp;
            try {
                otp = Integer.parseInt(otpString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            resetPassword(email, otp, newPassword);
        });

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void forgotPassword(String email) {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);
        apiService.forgotPassword(forgotPasswordRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password recovery email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email not sent", Toast.LENGTH_SHORT).show();
                }
                Log.d("LoginDebug", "Response code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error when sending email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword(String email, int otp, String newPassword) {
        OtpVerificationRequest request = new OtpVerificationRequest(email, otp);

        apiService.resetPassword(request, newPassword).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("goToLogin", true);
                    setResult(RESULT_OK, intent);
                    finish(); // Kết thúc Activity, quay về MainActivity
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                }
                Log.d("LoginDebug", "Response code: " + response.code());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error when resetting password", Toast.LENGTH_SHORT).show();
                Log.e("LoginDebug", "Error: ", t);
            }
        });
    }
}