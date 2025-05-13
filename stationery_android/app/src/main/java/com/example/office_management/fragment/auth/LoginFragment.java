package com.example.office_management.fragment.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.fragment.app.Fragment;

import com.example.office_management.R;
import com.example.office_management.activity.MainActivity;
import com.example.office_management.activity.auth.ForgotPasswordActivity;
import com.example.office_management.dto.response.auth.LoginGoogleResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.LoginApi;
import com.example.office_management.dto.request.GoogleLoginRequest;
import com.example.office_management.dto.request.LoginRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.LoginResponse;

import com.example.office_management.utils.DialogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import java.security.MessageDigest;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private LinearLayout btnLogin, btnGoogleSignIn;
    private LoginApi apiService;
    private TextView tvForgotPassword, tvRegister;
    private ActivityResultLauncher<Intent> resetPasswordLauncher;
    private CredentialManager credentialManager;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = BaseURL.getUrl(requireContext()).create(LoginApi.class);
        credentialManager = CredentialManager.create(requireContext());

        etEmail = view.findViewById(R.id.email);
        etPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogleSignIn = view.findViewById(R.id.btnLoginGg);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        tvRegister = view.findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> login(etEmail.getText().toString(), etPassword.getText().toString()));

        btnGoogleSignIn.setOnClickListener(v -> {
            if (checkPlayServices()) {
                signInWithGoogle();
            } else {
                Toast.makeText(requireContext(), "Google Play Services not available", Toast.LENGTH_SHORT).show();
            }
        });

        tvForgotPassword.setOnClickListener(v -> resetPasswordLauncher.launch(new Intent(getContext(), ForgotPasswordActivity.class)));

        tvRegister.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.account_bottom_navigation);
            bottomNav.setSelectedItemId(R.id.register);
        });

        resetPasswordLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("gotoLogin", false)) {
                            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.account_bottom_navigation);
                            bottomNav.setSelectedItemId(R.id.login);
                        }
                    }
                }
        );

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> btnGoogleSignIn.setEnabled(true)
        );
    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            return false;
        }
        return true;
    }

    private void login(String email, String password) {
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    if (token != null && !token.isEmpty()) {
                        saveToken(token);
                        if (isAdded()) {
                            DialogUtils.showSuccessToast(requireContext(), "Login successful!");
                            onLoginSuccess();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login failed: Invalid token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        String nonce = UUID.randomUUID().toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(nonce.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            nonce = hexString.toString();
        } catch (Exception ignored) {}

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.google_client_id))
                .setNonce(nonce)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                null,
                Runnable::run,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleGoogleSignInResult(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        if (!(e instanceof GetCredentialCancellationException)) {
                            Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void handleGoogleSignInResult(GetCredentialResponse result) {
        if (result.getCredential() instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) result.getCredential();
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                if (credential.getIdToken() != null) {
                    loginWithGoogle(credential.getIdToken());
                } else {
                    Toast.makeText(requireContext(), "Google Sign-In failed: No ID token", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithGoogle(String idToken) {
        apiService.loginWithGoogle(new GoogleLoginRequest(idToken)).enqueue(new Callback<ApiResponse<LoginGoogleResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginGoogleResponse>> call, Response<ApiResponse<LoginGoogleResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    String token = response.body().getResult().getAccessToken();
                    if (token != null && !token.isEmpty()) {
                        saveToken(token);
                        if (isAdded()) {
                            DialogUtils.showSuccessToast(requireContext(), "Google Sign-In successful!");
                            onLoginSuccess();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Google Sign-In failed: Invalid token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginGoogleResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }

    private void onLoginSuccess() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onLoginSuccess();
        } else {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.putExtra("default_page", 0);
            startActivity(intent);
            requireActivity().finish();
        }
    }
}
