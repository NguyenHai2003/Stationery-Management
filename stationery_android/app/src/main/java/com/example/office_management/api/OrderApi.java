package com.example.office_management.api;

import com.example.office_management.dto.request.order.PurchaseOrderRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.MomoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {
    @POST ("purchase-orders/payment-momo")
    Call<ApiResponse<MomoResponse>> createOrderWithMomo(
            @Header("Authorization") String authHeader,
            @Body PurchaseOrderRequest request
            );

    @GET("purchase-orders/payment-momo/transaction-status/{orderId}")
    Call<ApiResponse<MomoResponse>> transactionStatus(
            @Header("Authorization") String authHeader,
            @Path("orderId") String orderId
    );
}
