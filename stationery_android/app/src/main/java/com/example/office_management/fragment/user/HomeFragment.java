package com.example.office_management.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.ImageView;
import android.widget.Toast;

import com.example.office_management.R;
import com.example.office_management.activity.search.ProductSearchActivity;
import com.example.office_management.activity.search.SearchActivity;
import com.example.office_management.adapter.BannerAdapter;
import com.example.office_management.adapter.CategoryAdapter;
import com.example.office_management.adapter.ProductAdapter;
import com.example.office_management.api.ProductApi;
import com.example.office_management.decoration.GridSpacingItemDecoration;
import com.example.office_management.model.Category;

import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    //Quảng cáo
    private ViewPager2 bannerSlider;
    private TabLayout bannerIndicator;
    private BannerAdapter bannerAdapter;
    private List<Integer> bannerList;
    private Handler handler = new Handler();
    private Runnable autoScrollRunnable;

    //Danh sách sản phẩm
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<ProductResponse> productList;
    private ProductApi productApi;

    //Danh mục
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categoryList;

    //Sản phẩm Flash Sale


    private ImageView btnCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bannerSlider = view.findViewById(R.id.bannerSlider);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);

        productRecyclerView = view.findViewById(R.id.productRecyclerView);

        fetchBanner();

        // Khởi tạo danh sách sản phẩm
        productList = new ArrayList<>();

        productAdapter = new ProductAdapter(getContext(), productList);
        Log.d("API", "Số lượng sản phẩm: " + productList.size());
        productRecyclerView.setAdapter(productAdapter);

        // Sử dụng GridLayoutManager để hiển thị sản phẩm theo dạng lưới, 2 cột ví dụ
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = (int) (getResources().getDisplayMetrics().density * 8);
        productRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        fetchProducts("createdAt", "", "", "", "");

        // Khởi tạo thanh tìm kiếm
        View searchBar = view.findViewById(R.id.searchBar);
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                // Mở SearchActivity khi người dùng nhấn vào thanh tìm kiếm
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            });
        }

        ImageView btnScan = view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProductSearchActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void onCategoryClick(Category category) {
        // Xử lý khi nhấn vào danh mục (nếu cần)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoScrollRunnable);
    }

    private void fetchProducts(String sortBy, String minPrice, String maxPrice, String categoryId, String search) {
        productApi = BaseURL.getUrl(requireContext()).create(ProductApi.class);
        Log.d("API", "Base URL: " + BaseURL.getUrl(requireContext()).baseUrl().toString());

        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                sortBy, minPrice, maxPrice, categoryId, search
        );
        Log.d("API", "Sending API request with sortBy=" + sortBy + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + ", categoryId=" + categoryId + ", search=" + search);

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call, Response<ApiResponse<ResultResponse>> response) {
                Log.d("API", "API response received: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<ResultResponse> apiResponse = response.body();
                        List<ProductResponse> fetchedList = apiResponse.getResult().getContent();

                        if (fetchedList != null && !fetchedList.isEmpty()) {
                            productAdapter.setData(fetchedList);
                            Log.d("API", "Số lượng sản phẩm nhận được: " + fetchedList.size());
                        } else {
                            Log.d("API", "Danh sách sản phẩm rỗng hoặc null");
                            Toast.makeText(getContext(), "Empty Product List", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("API", "Lỗi phân tích JSON: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Error JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "API request failed: " + response.message());
                    Toast.makeText(getContext(), "API request failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                Log.e("API", "API request failed: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error network" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBanner(){
        bannerSlider.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setAlpha(0f);
                page.animate().alpha(1f).setDuration(500);
            }
        });

        // Danh sách ảnh quảng cáo
        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.img_banner_1);
        bannerList.add(R.drawable.img_banner_2);
        bannerList.add(R.drawable.img_banner_6);
        bannerList.add(R.drawable.img_banner_4);
        bannerList.add(R.drawable.img_banner_5);
        bannerList.add(R.drawable.img_banner_3);

        bannerAdapter = new BannerAdapter(bannerList);
        bannerSlider.setAdapter(bannerAdapter);

        // Liên kết ViewPager2 với TabLayout
        new TabLayoutMediator(bannerIndicator, bannerSlider, (tab, position) -> {}).attach();

        // Tự động chuyển slide sau mỗi 3 giây
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerList.size();
                bannerSlider.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(autoScrollRunnable, 3000);
    }
}