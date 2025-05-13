package com.example.office_management.fragment.user;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.office_management.activity.MainActivity;
import com.example.office_management.R;

import com.example.office_management.dto.request.UpdateUserRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.api.LoginApi;
import com.example.office_management.api.UserApi;
import com.example.office_management.dto.response.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserInfoFragment extends Fragment {
    private ImageView imageAvatar;
    private TextView textFullName;
    private EditText textFirstName, textLastName,textEmail, textPhone, textDob;
    private Button buttonLogout, btnChangePassword, btnUpdateProfile;
    private UserApi apiService;
    private LoginApi loginApi;

    public UserInfoFragment() {
        super(R.layout.fragment_user_info);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageAvatar = view.findViewById(R.id.image_avatar);
        textFullName = view.findViewById(R.id.text_full_name);
        textFirstName = view.findViewById(R.id.text_first_name);
        textLastName = view.findViewById(R.id.text_last_name);
        textEmail = view.findViewById(R.id.text_email);
        textPhone = view.findViewById(R.id.text_phone);
        textDob = view.findViewById(R.id.text_dob);

        buttonLogout = view.findViewById(R.id.button_logout);
        btnChangePassword = view.findViewById(R.id.button_change_password);
        btnUpdateProfile = view.findViewById(R.id.button_update);

        apiService = BaseURL.getUrl(requireContext()).create(UserApi.class);
        Log.d("DEBUG_USER_INFO", "apiService = " + apiService);

        loadUserInfo();

        buttonLogout.setOnClickListener(v -> {
            logout();
        });

        btnUpdateProfile.setOnClickListener(v -> {
            updateProfile();
        });
    }

    private void loadUserInfo() {
        Log.d("DEBUG_USER_INFO", "loadUserInfo() được gọi");
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        apiService.getUserInfo(authHeader).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("USER_INFO_DEBUG", "Response: " + new Gson().toJson(response.body()));

                    UserResponse user = response.body().getResult(); // LẤY result
                    String fullName = (user.getFirstName() != null ? user.getFirstName() : "")
                            + " "
                            + (user.getLastName() != null ? user.getLastName() : "");
                    textFullName.setText(fullName);
                    textFirstName.setText(user.getFirstName());
                    textLastName.setText(user.getLastName());
                    textEmail.setText(user.getEmail());
                    textPhone.setText(user.getPhone());
                    Date dob = user.getDob();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    textDob.setText(dob != null ? formatter.format(dob) : "");

                    Glide.with(requireContext())
                            .load(user.getAvatar())
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(imageAvatar);

                } else {
                    Toast.makeText(requireContext(), "Unable to get user information", Toast.LENGTH_SHORT).show();
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Log.e("API_ERROR", "Error: " + response.code() + ", errorBody: " + errorJson);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;
        loginApi = BaseURL.getUrl(requireContext()).create(LoginApi.class);

        loginApi.logout(authHeader).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("token");
                editor.apply();

                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onLogout();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Logout error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateProfile() {

        String firstName = textFirstName.getText().toString().trim();
        String lastName = textLastName.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        String dobStr = textDob.getText().toString().trim();

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date dob = null;
        try {
            dob = inputFormat.parse(dobStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid date", Toast.LENGTH_SHORT).show();
            return; // Nếu ngày không hợp lệ thì không thực hiện cập nhật
        }

        // Định dạng lại Date thành String theo định dạng chuẩn
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dobFormatted = outputFormat.format(dob);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;
        // Gửi request cập nhật
        UpdateUserRequest request = new UpdateUserRequest(firstName, lastName, email, phone, dobFormatted);

        // Gọi API
        UserApi apiService = BaseURL.getUrl(requireContext()).create(UserApi.class);
        Call<ApiResponse<UserResponse>> call = apiService.updateUser(authHeader, request);

        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    // Bạn có thể cập nhật UI ở đây nếu muốn
                } else {
                    Toast.makeText(getContext(), "Update error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
