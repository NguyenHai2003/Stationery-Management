package com.example.office_management.activity.search;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.adapter.ProductAdapter;
import com.example.office_management.api.ProductApi;
import com.example.office_management.dto.response.ApiResponse;
import com.example.office_management.dto.response.ResultResponse;
import com.example.office_management.dto.response.product.ProductResponse;
import com.example.office_management.retrofit2.BaseURL;
import com.example.office_management.utils.TFLiteImageClassifier;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSearchActivity extends AppCompatActivity {
    private static final String TAG = "TEST_SCAN";
    private static final int CAMERA_PERMISSION_CODE = 100;

    private RecyclerView searchResultsRecyclerView;
    private TextView tvSearchResults;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private AlertDialog cameraDialog, loadingDialog;
    private List<ProductResponse> productDatabase;
    private ProductAdapter productAdapter;
    private List<ProductResponse> searchResultList;
    private ProductApi productApi;
    private TFLiteImageClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        cameraExecutor = Executors.newSingleThreadExecutor();
        productApi = BaseURL.getUrl(this).create(ProductApi.class);

        // Khởi tạo TFLiteImageClassifier
        try {
            classifier = new TFLiteImageClassifier(this);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing TFLiteImageClassifier: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to load model: " + e.getMessage(), Toast.LENGTH_LONG).show();
            classifier = null; // Đặt classifier thành null và tiếp tục
        }

        initViews();
        fetchProductList(null);
    }

    private void initViews() {
        ImageView btnScan = findViewById(R.id.btnScan);
        if (btnScan == null) {
            showToast("Cannot find btnScan in layout");
            return;
        }
        btnScan.setOnClickListener(v -> checkCameraPermission());

        tvSearchResults = findViewById(R.id.tvSearchResults);
        searchResultsRecyclerView = findViewById(R.id.rvSearchResults);
        if (tvSearchResults == null || searchResultsRecyclerView == null) {
            showToast("Cannot find tvSearchResults or rvSearchResults in layout");
            return;
        }

        searchResultList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, searchResultList);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(productAdapter);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCameraDialog();
        }
    }

    private void fetchProductList(String searchQuery) {
        showLoadingDialog();
        Log.d(TAG, "Fetching products with query: " + (searchQuery != null ? searchQuery : "null"));

        Call<ApiResponse<ResultResponse>> call = productApi.apiGetAllProducts(
                "name", null, null, null, searchQuery);

        call.enqueue(new Callback<ApiResponse<ResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResultResponse>> call,
                                   Response<ApiResponse<ResultResponse>> response) {
                dismissLoadingDialog();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ResultResponse> apiResponse = response.body();
                    Log.d(TAG, "API Response - Code: " + apiResponse.getCode() +
                            ", Message: " + apiResponse.getMessage());

                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        productDatabase = apiResponse.getResult().getContent();
                        displaySearchResults();
                    } else {
                        showToast("Empty API data");
                    }
                } else {
                    showToast("API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ResultResponse>> call, Throwable t) {
                dismissLoadingDialog();
                Log.e(TAG, "API call failed", t);
                showToast("Connection error: " + t.getMessage());
            }
        });
    }

    private void openCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_camera, null);
        previewView = dialogView.findViewById(R.id.previewView);
        ImageView captureButton = dialogView.findViewById(R.id.captureButton);

        if (previewView == null || captureButton == null) {
            showToast("Cannot find previewView or captureButton in dialog layout");
            return;
        }

        builder.setView(dialogView);
        cameraDialog = builder.create();
        cameraDialog.show();

        startCamera();
        captureButton.setOnClickListener(v -> takePhoto());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                Log.e(TAG, "Camera setup error", e);
                showToast("Camera error: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) {
            showToast("ImageCapture not initialized");
            return;
        }

        showLoadingDialog();
        imageCapture.takePicture(
                cameraExecutor,
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Bitmap bitmap = imageProxyToBitmap(image);
                        image.close();

                        if (cameraDialog != null && cameraDialog.isShowing()) {
                            cameraDialog.dismiss();
                        }

                        if (bitmap != null) {
                            Log.d(TAG, "Captured image size: " +
                                    bitmap.getWidth() + "x" + bitmap.getHeight());
                            processImage(bitmap);
                        } else {
                            dismissLoadingDialog();
                            showToast("Cannot process image");
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Capture error", exception);
                        dismissLoadingDialog();
                        showToast("Capture error: " + exception.getMessage());
                    }
                }
        );
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        try {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Image conversion error", e);
            return null;
        }
    }

    private boolean isImageTooDark(Bitmap bitmap) {
        int pixelCount = bitmap.getWidth() * bitmap.getHeight();
        int totalBrightness = 0;

        for (int x = 0; x < bitmap.getWidth(); x += 10) {
            for (int y = 0; y < bitmap.getHeight(); y += 10) {
                int pixel = bitmap.getPixel(x, y);
                int brightness = (int) (0.299 * ((pixel >> 16) & 0xFF) +
                        0.587 * ((pixel >> 8) & 0xFF) +
                        0.114 * (pixel & 0xFF));
                totalBrightness += brightness;
            }
        }

        int averageBrightness = totalBrightness / (pixelCount / 100);
        return averageBrightness < 50;
    }

    private void processImage(Bitmap bitmap) {
        if (isImageTooDark(bitmap)) {
            runOnUiThread(() -> {
                dismissLoadingDialog();
                showDetectionResult("Image too dark, please retake in brighter light");
            });
            return;
        }

        // Resize và crop ảnh
        int targetWidth = 640;
        int targetHeight = (int) (bitmap.getHeight() * (targetWidth / (float) bitmap.getWidth()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

        int cropSize = Math.min(resizedBitmap.getWidth(), resizedBitmap.getHeight()) * 3 / 4;
        int cropX = (resizedBitmap.getWidth() - cropSize) / 2;
        int cropY = (resizedBitmap.getHeight() - cropSize) / 2;
        Bitmap croppedBitmap = Bitmap.createBitmap(resizedBitmap, cropX, cropY, cropSize, cropSize);

        Log.d(TAG, "Processing image with TFLite");

        // Nhận diện ảnh bằng TFLiteImageClassifier
        if (classifier == null) {
            runOnUiThread(() -> {
                dismissLoadingDialog();
                showDetectionResult("Model not initialized. Please check model files and try again.");
            });
            return;
        }

        try {
            String detectedLabel = classifier.classifyImage(croppedBitmap);
            runOnUiThread(() -> {
                dismissLoadingDialog();
                if (detectedLabel.equals("Unknown")) {
                    Log.d(TAG, "No label detected");
                    showDetectionResult("No label detected");
                } else {
                    Log.d(TAG, "Detected label: " + detectedLabel);
                    showDetectionResult("Detected: " + detectedLabel);
                    fetchProductList(detectedLabel);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Image classification failed", e);
            runOnUiThread(() -> {
                dismissLoadingDialog();
                showDetectionResult("Classification error: " + e.getMessage());
            });
        }
    }

    private void showDetectionResult(String message) {
        runOnUiThread(() -> {
            dismissLoadingDialog();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            if (message.contains("too dark") || message.contains("No label") || message.contains("error")) {
                openCameraDialog();
            }
        });
    }

    private void displaySearchResults() {
        runOnUiThread(() -> {
            if (productDatabase == null || productDatabase.isEmpty()) {
                showToast("No matching products");
                tvSearchResults.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.GONE);
                return;
            }

            searchResultList.clear();
            searchResultList.addAll(productDatabase);
            tvSearchResults.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            productAdapter.notifyDataSetChanged();

            Log.d(TAG, "Displayed " + searchResultList.size() + " products");
        });
    }

    private void showLoadingDialog() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_loading);
            builder.setCancelable(false);
            loadingDialog = builder.create();
            loadingDialog.show();
        });
    }

    private void dismissLoadingDialog() {
        runOnUiThread(() -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraDialog();
            } else {
                showToast("Camera permission required");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (classifier != null) {
            classifier.close();
        }
    }
}