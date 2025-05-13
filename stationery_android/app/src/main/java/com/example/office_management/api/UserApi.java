package com.example.office_management.api;

import com.example.office_management.dto.request.UpdateUserRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface UserApi {
    @GET("users/info")
    Call<ApiResponse<UserResponse>> getUserInfo(@Header("Authorization") String authHeader);

    @PUT("users/update-user")
    Call<ApiResponse<UserResponse>> updateUser(@Header("Authorization") String authHeader, @Body UpdateUserRequest request);

}
