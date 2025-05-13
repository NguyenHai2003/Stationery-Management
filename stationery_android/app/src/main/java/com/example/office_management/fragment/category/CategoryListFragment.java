package com.example.office_management.fragment.category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.adapter.CategoryAdapter;
import com.example.office_management.api.CategoryApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.category.CategoryProductResponse;
import com.example.office_management.model.Category;
import com.example.office_management.retrofit2.BaseURL;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListFragment extends Fragment {
    private RecyclerView rvSidebarCategories, rvHotCategories;
    private ProgressBar progressBar;
    private CategoryAdapter sidebarAdapter, hotAdapter;
    private List<Category> sidebarList = new ArrayList<>();
    private List<Category> hotList = new ArrayList<>();
    private CategoryApi categoryApi;

    public CategoryListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryApi = BaseURL.getUrl(requireContext()).create(CategoryApi.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CategoryListFragment", "Creating view");
        View view = inflater.inflate(R.layout.fragment_category__list, container, false);
        rvSidebarCategories = view.findViewById(R.id.rv_sidebar_categories);
        rvHotCategories = view.findViewById(R.id.rv_hot_categories);
        progressBar = view.findViewById(R.id.progressBar);
        Log.d("CategoryListFragment", "RecyclerViews initialized: " + (rvSidebarCategories != null) + ", " + (rvHotCategories != null));

        // Initialize adapters
        sidebarAdapter = new CategoryAdapter(requireContext(), sidebarList, category -> {
            // TODO: Handle sidebar category click
            Log.d("CategoryListFragment", "Sidebar category clicked: " + category.getCategoryName());
        }, false);
        rvSidebarCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSidebarCategories.setAdapter(sidebarAdapter);

        hotAdapter = new CategoryAdapter(requireContext(), hotList, category -> {
            // TODO: Handle hot category click
            Log.d("CategoryListFragment", "Hot category clicked: " + category.getCategoryName());
        }, true);
        rvHotCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvHotCategories.setAdapter(hotAdapter);

        // Fetch data from APIs
        fetchSidebarCategories();
        fetchHotCategories();

        return view;
    }

    private void fetchSidebarCategories() {
        progressBar.setVisibility(View.VISIBLE);
        Call<ApiResponse<List<CategoryProductResponse>>> call = categoryApi.apiTopUserCategories(null);
        call.enqueue(new Callback<ApiResponse<List<CategoryProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryProductResponse>>> call, Response<ApiResponse<List<CategoryProductResponse>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryProductResponse> apiCategories = response.body().getResult();
                    sidebarList.clear();
                    for (CategoryProductResponse apiCategory : apiCategories) {
                        Log.d("CategoryListFragment", "Sidebar Category: " + apiCategory.getCategoryName() +
                                ", Icon: " + apiCategory.getIcon() +
                                ", BgColor: " + apiCategory.getBgColor());
                        sidebarList.add(new Category(
                                apiCategory.getCategoryId(),
                                apiCategory.getCategoryName(),
                                apiCategory.getIcon(),
                                apiCategory.getBgColor()
                        ));
                    }
                    sidebarAdapter.notifyDataSetChanged();
                    Log.d("CategoryListFragment", "Sidebar categories loaded: " + sidebarList.size());
                } else {
                    Log.e("CategoryListFragment", "Failed to load sidebar categories: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryProductResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("CategoryListFragment", "Error fetching sidebar categories: " + t.getMessage());
            }
        });
    }

    private void fetchHotCategories() {
        progressBar.setVisibility(View.VISIBLE);
        Call<ApiResponse<List<CategoryProductResponse>>> call = categoryApi.apiAllCategory();
        call.enqueue(new Callback<ApiResponse<List<CategoryProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryProductResponse>>> call, Response<ApiResponse<List<CategoryProductResponse>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryProductResponse> apiCategories = response.body().getResult();
                    hotList.clear();
                    for (CategoryProductResponse apiCategory : apiCategories) {
                        Log.d("CategoryListFragment", "Hot Category: " + apiCategory.getCategoryName() +
                                ", Icon: " + apiCategory.getIcon() +
                                ", BgColor: " + apiCategory.getBgColor());
                        hotList.add(new Category(
                                apiCategory.getCategoryId(),
                                apiCategory.getCategoryName(),
                                apiCategory.getIcon(),
                                apiCategory.getBgColor()
                        ));
                    }
                    hotAdapter.notifyDataSetChanged();
                    Log.d("CategoryListFragment", "Hot categories loaded: " + hotList.size());
                } else {
                    Log.e("CategoryListFragment", "Failed to load hot categories: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryProductResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("CategoryListFragment", "Error fetching hot categories: " + t.getMessage());
            }
        });
    }
}