package com.example.office_management.api;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.colorSize.ColorSizeSlugResponse;
import com.example.office_management.dto.response.product.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products")
    Call<ApiResponse<ResultResponse>> apiGetAllProducts(
            @Query("sortBy") String sortBy,
            @Query("minPrice") String minPrice,
            @Query("maxPrice") String maxPrice,
            @Query("categoryId") String categoryId,
            @Query("search") String search
    );

    @GET("products/{slug}")
    Call<ApiResponse<ProductResponse>> getProductDetail(
            @Path("slug") String slug
    );

    @GET("products/color-size/{slug}")
    Call<ApiResponse<List<ColorSizeSlugResponse>>> getColorSizeSlug(
            @Path("slug") String slug
    );
}
