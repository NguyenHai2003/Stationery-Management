package com.example.office_management.activity.user;

import static com.example.office_management.adapter.ProductAdapter.TextUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.office_management.R;
import com.example.office_management.activity.address.ShippingActivity;
import com.example.office_management.api.CartApi;
import com.example.office_management.api.ProductApi;
import com.example.office_management.dto.request.cart.AddCartRequest;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.CartResponse;
import com.example.office_management.dto.response.ImageResponse;
import com.example.office_management.dto.response.colorSize.ColorSizeSlugResponse;
import com.example.office_management.dto.response.colorSize.SizeSlugResponse;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.fragment.user.HomeFragment;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.adapter.*;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView productName, discountPrice, originalPrice, description, discountPercentage, rating, sold, imageCarouselIndicator, tvQuanlity;
    private RecyclerView sizeRecyclerView, colorRecyclerView;
    private LinearLayout sizeSection, colorSection;
    private Button addToCartButton, buyNowButton;
    private ImagePagerAdapter imageAdapter;
    private ProductApi productApi;
    private CartApi cartApi;
    private List<ImageResponse> images;
    private ImageButton btnBack, btnHome, btnCart, btnSearch, btnDecrement, btnIncrement;
    private SizeSlugResponse selectedSize;
    private ColorSizeSlugResponse selectedColor;
    private List<ColorSizeSlugResponse> colorList = new ArrayList<>();
    private String slug;
    public static final DecimalFormat formatter = new DecimalFormat("#,###");
    private ProductResponse cachedProductResponse;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    private androidx.appcompat.widget.SearchView searchView;
    private int quantity = 1;
    private String productDetailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initViews();


        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
        btnHome.setOnClickListener(v -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.getVisibility() == View.GONE) {
                    // Hiện thanh tìm kiếm
                    searchView.setVisibility(View.VISIBLE);
                    searchView.requestFocus();
                } else {
                    // Đóng và reset về ban đầu
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setVisibility(View.GONE);
                }
            }
        });

        btnIncrement.setOnClickListener(v -> {
            quantity++;
            updateQuantityText();
        });

        btnDecrement.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityText();
            }
        });

        slug = getIntent().getStringExtra("slug");

        if (slug == null) {
            Toast.makeText(this, "No product slug", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        addToCartButton.setOnClickListener(v -> {
            if (productDetailId == null || productDetailId.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy mã sản phẩm chi tiết", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Add to cart", "Product Detail ID: " + productDetailId + ", Quantity: " + quantity);
            addItemToCart(productDetailId, quantity);
        });

        buyNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, ShippingActivity.class);
            startActivity(intent);
        });

        productApi = BaseURL.getUrl(this).create(ProductApi.class);
        cartApi = BaseURL.getUrl(this).create(CartApi.class);

        // Gọi API lấy chi tiết sản phẩm và màu sắc/kích thước
        fetchProductDetails(slug);
        fetchColorSizeData(slug);


    }

    private void updateQuantityText() {
        tvQuanlity.setText(String.valueOf(quantity));
    }


    private void initViews() {
        viewPager = findViewById(R.id.imageViewPager);
        imageCarouselIndicator = findViewById(R.id.imageCarouselIndicator);
        productName = findViewById(R.id.productName);
        discountPrice = findViewById(R.id.productPrice);
        originalPrice = findViewById(R.id.originalPrice);
        description = findViewById(R.id.productDescription);
        discountPercentage = findViewById(R.id.discountPercentage);
        rating = findViewById(R.id.productRating);
        sold = findViewById(R.id.tvSold);
        addToCartButton = findViewById(R.id.addToCartButton);
        buyNowButton = findViewById(R.id.buyNowButton);
        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btnHome);
        btnCart = findViewById(R.id.btnCart);
        sizeSection = findViewById(R.id.sizeSection);
        colorSection = findViewById(R.id.colorSection);
        sizeRecyclerView = findViewById(R.id.sizeRecyclerView);
        colorRecyclerView = findViewById(R.id.colorRecyclerView);
        searchView = findViewById(R.id.searchView);
        btnSearch = findViewById(R.id.btnSearch);
        tvQuanlity = findViewById(R.id.quantity);
        btnIncrement = findViewById(R.id.incrementButton);
        btnDecrement = findViewById(R.id.decrementButton);
    }

    private void fetchProductDetails(String slug) {
        if (cachedProductResponse != null && slug.equals(this.slug)) {
            displayProductDetails(cachedProductResponse);
            return;
        }

        Call<ApiResponse<ProductResponse>> call = productApi.getProductDetail(slug);
        call.enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    cachedProductResponse = response.body().getResult();
                    displayProductDetails(cachedProductResponse);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Unable to load the product", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayProductDetails(ProductResponse productResponse) {
        runOnUiThread(() -> {
            productName.setText(productResponse.getName());
            description.setText(productResponse.getDescription());
            rating.setText("★ " + productResponse.getTotalRating());
            //sold.setText("Sold: " + productResponse.getSoldQuantity());
            rating.setText(TextUtils.getStarRatingText(productResponse.getTotalRating()));

            ProductDetailResponse detail = productResponse.getProductDetail();
            Log.d("ProductDetailResponse", "Detail received: " + new Gson().toJson(detail));
            productDetailId = detail.getProductDetailId();
            discountPrice.setText(String.format("%sđ", formatter.format(
                    detail.getDiscountPrice() > 0 ? detail.getDiscountPrice() : detail.getOriginalPrice()
            )));

            images = detail.getImages();
            updateImagesByColor(detail);

            String originalPriceText = String.format("%sđ", formatter.format(detail.getOriginalPrice()));
            SpannableString spannableString = new SpannableString(originalPriceText);
            spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            originalPrice.setText(spannableString);

            int discountPercent = 0;
            if (detail.getOriginalPrice() > 0 && detail.getDiscountPrice() > 0) {
                discountPercent = 100 - (detail.getDiscountPrice() * 100 / detail.getOriginalPrice());
            }
            discountPercentage.setText("-" + discountPercent + "%");
        });
    }

    private void fetchColorSizeData(String slug) {
        Call<ApiResponse<List<ColorSizeSlugResponse>>> call = productApi.getColorSizeSlug(slug);
        call.enqueue(new Callback<ApiResponse<List<ColorSizeSlugResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ColorSizeSlugResponse>>> call, Response<ApiResponse<List<ColorSizeSlugResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<ColorSizeSlugResponse> colorSizeList = response.body().getResult();
                    Log.d("FetchColorSize", "Data received: " + colorSizeList.toString());
                    displayColorSizeData(colorSizeList);
                } else {
                    Log.e("FetchColorSize", "Failed to load data: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải màu sắc và kích thước", Toast.LENGTH_SHORT).show();
                    colorSection.setVisibility(View.GONE);
                    sizeSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ColorSizeSlugResponse>>> call, Throwable t) {
                Log.e("FetchColorSize", "Error: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                colorSection.setVisibility(View.GONE);
                sizeSection.setVisibility(View.GONE);
            }
        });
    }

    private void displayColorSizeData(final List<ColorSizeSlugResponse> colorSizeList) {
        if (colorSizeList == null || colorSizeList.isEmpty()) {
            Log.w("ColorSizeData", "Danh sách màu sắc và kích thước rỗng hoặc null");
            colorSection.setVisibility(View.GONE);
            sizeSection.setVisibility(View.GONE);
            // Trường hợp 4: Không có màu sắc và không có kích thước
            fetchProductDetails(slug);
            return;
        }

        colorList = new ArrayList<>();
        List<SizeSlugResponse> allSizes = new ArrayList<>();

        for (ColorSizeSlugResponse color : colorSizeList) {
            if (color != null && color.getHex() != null && !color.getHex().isEmpty()) {
                colorList.add(color);
            }
            if (color != null && color.getSizes() != null) {
                allSizes.addAll(color.getSizes());
            }
        }

        if (!colorList.isEmpty()) {
            // Trường hợp 1 và 2: Có màu sắc
            colorSection.setVisibility(View.VISIBLE);
            colorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ColorAdapter colorAdapter = new ColorAdapter(colorList);
            colorAdapter.setOnColorClickListener(color -> {
                selectedColor = color;
                selectedSize = null; // Reset kích thước khi chọn màu sắc mới
                // Kiểm tra xem có kích thước thực sự hay không
                boolean hasValidSize = color.getSizes() != null && color.getSizes().stream()
                        .anyMatch(size -> size.getSize() != null && !size.getSize().equals("null"));
                if (hasValidSize) {
                    // Trường hợp 1: Có màu sắc và có kích thước
                    updateSizeRecyclerView(color.getSizes());
                } else {
                    // Trường hợp 2: Có màu sắc nhưng không có kích thước
                    sizeSection.setVisibility(View.GONE);
                    // Lấy slug từ sizes của màu (nếu có)
                    String colorSlug = color.getSizes() != null && !color.getSizes().isEmpty()
                            ? color.getSizes().get(0).getSlug() : slug;
                    fetchProductDetailsByColor(colorSlug);
                }
            });
            colorRecyclerView.setAdapter(colorAdapter);

            // Chọn màu đầu tiên mặc định
            selectedColor = colorList.get(0);
            // Kiểm tra xem có kích thước thực sự hay không
            boolean hasValidSize = selectedColor.getSizes() != null && selectedColor.getSizes().stream()
                    .anyMatch(size -> size.getSize() != null && !size.getSize().equals("null"));
            if (hasValidSize) {
                // Trường hợp 1: Có màu sắc và có kích thước
                updateSizeRecyclerView(selectedColor.getSizes());
            } else {
                // Trường hợp 2: Có màu sắc nhưng không có kích thước
                sizeSection.setVisibility(View.GONE);
                // Lấy slug từ sizes của màu (nếu có)
                String colorSlug = selectedColor.getSizes() != null && !selectedColor.getSizes().isEmpty()
                        ? selectedColor.getSizes().get(0).getSlug() : slug;
                fetchProductDetailsByColor(colorSlug);
            }
        } else if (!allSizes.isEmpty()) {
            // Trường hợp 3: Không có màu sắc nhưng có kích thước
            colorSection.setVisibility(View.GONE);
            selectedColor = null;
            updateSizeRecyclerView(allSizes);
        } else {
            // Trường hợp 4: Không có màu sắc và không có kích thước
            colorSection.setVisibility(View.GONE);
            sizeSection.setVisibility(View.GONE);
            fetchProductDetails(slug);
        }
    }

    private void fetchProductDetailsByColor(String colorSlug) {
        if (colorSlug == null || colorSlug.isEmpty()) {
            Log.e("FetchProductDetailsByColor", "Color slug is null or empty");
            Toast.makeText(this, "Không thể tải ảnh sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("FetchProductDetailsByColor", "Fetching details for color slug: " + colorSlug);
        Call<ApiResponse<ProductResponse>> call = productApi.getProductDetail(colorSlug);
        call.enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    ProductResponse productResponse = response.body().getResult();
                    Log.d("ProductDetailResponse", "Received: " + new Gson().toJson(productResponse));
                    // Cập nhật ảnh và các chi tiết khác
                    displayProductDetails(productResponse);
                } else {
                    Log.e("ProductDetailResponse", "Failed: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Log.e("ProductDetailResponse", "Error: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSizeRecyclerView(List<SizeSlugResponse> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            hideSizeSection();
            return;
        }

        // Lọc kích thước hợp lệ
        List<SizeSlugResponse> validSizes = new ArrayList<>();
        for (SizeSlugResponse s : sizes) {
            if (s != null && s.getSize() != null && !s.getSize().isEmpty() && s.getSlug() != null && !s.getSlug().isEmpty()) {
                validSizes.add(s);
            }
        }

        if (validSizes.isEmpty()) {
            hideSizeSection();
            return;
        }

        // Nếu có màu sắc nhưng chưa chọn, ẩn sizeSection
        if (!colorList.isEmpty() && selectedColor == null) {
            hideSizeSection();
            Toast.makeText(this, "Vui lòng chọn màu sắc trước", Toast.LENGTH_SHORT).show();
            return;
        }

        sizeSection.setVisibility(View.VISIBLE);

        // Tái sử dụng adapter nếu tồn tại
        SizeAdapter sizeAdapter = (SizeAdapter) sizeRecyclerView.getAdapter();
        if (sizeAdapter == null) {
            sizeAdapter = new SizeAdapter(validSizes);
            sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            sizeRecyclerView.setAdapter(sizeAdapter);
        } else {
            sizeAdapter.updateSizes(validSizes);
        }

        // Vô hiệu hóa sizeRecyclerView nếu màu sắc chưa được chọn
        sizeRecyclerView.setEnabled(colorList.isEmpty() || selectedColor != null);

        sizeAdapter.setOnSizeClickListener(size -> {
            if (!colorList.isEmpty() && selectedColor == null) {
                Toast.makeText(this, "Vui lòng chọn màu sắc trước", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSize = size;
            fetchProductDetailsWithSlug(size.getSlug());
        });

        // Chọn kích thước đầu tiên mặc định và gọi API
        selectedSize = validSizes.get(0);
        if (colorList.isEmpty() || selectedColor != null) {
            fetchProductDetailsWithSlug(selectedSize.getSlug());
        }
    }

    private void fetchProductDetailsWithSlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            Log.e("FetchProductDetails", "Slug is null or empty");
            Toast.makeText(this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("FetchProductDetails", "Fetching details for slug: " + slug);
        Call<ApiResponse<ProductResponse>> call = productApi.getProductDetail(slug);
        call.enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    ProductResponse productResponse = response.body().getResult();
                    Log.d("ProductDetailResponse", "Received: " + new Gson().toJson(productResponse));
                    displayProductDetails(productResponse);
                } else {
                    Log.e("ProductDetailResponse", "Failed: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Log.e("ProductDetailResponse", "Error: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideSizeSection() {
        sizeSection.setVisibility(View.GONE);
    }

    private void updateImagesByColor(ProductDetailResponse detail) {
        List<String> imageUrls = new ArrayList<>();
        if (detail != null && detail.getImages() != null && !detail.getImages().isEmpty()) {
            for (ImageResponse img : detail.getImages()) {
                imageUrls.add(img.getUrl());
            }
        } else if (images != null && !images.isEmpty()) {
            for (ImageResponse img : images) {
                imageUrls.add(img.getUrl());
            }
        }

        // Cập nhật adapter hiện tại nếu có
        if (imageAdapter == null) {
            imageAdapter = new ImagePagerAdapter(imageUrls, ProductDetailActivity.this);
            viewPager.setAdapter(imageAdapter);
        } else {
            imageAdapter.updateImages(imageUrls);
        }

        if (imageUrls.size() > 1) {
            imageCarouselIndicator.setText("1/" + imageUrls.size());
            if (pageChangeCallback == null) {
                pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        imageCarouselIndicator.setText((position + 1) + "/" + imageUrls.size());
                    }
                };
                viewPager.registerOnPageChangeCallback(pageChangeCallback);
            }
        } else {
            imageCarouselIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pageChangeCallback != null) {
            viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        }
        images = null; // Xóa để tránh rò rỉ bộ nhớ
    }

    private void addItemToCart(String productDetailId, int quantity) {
        SharedPreferences prefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String authHeader = "Bearer " + token;

        AddCartRequest request = new AddCartRequest(productDetailId, quantity);
        cartApi.addItemCart(authHeader, request)
                .enqueue(new Callback<ApiResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CartResponse cart = response.body().getResult();
                            Log.d("Cart Response", "Cart: " + new Gson().toJson(cart));
                            Toast.makeText(ProductDetailActivity.this, "Added to cart: " + cart.getProductName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Add failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}