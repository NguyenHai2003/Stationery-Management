package com.example.office_management.activity.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;
import com.example.office_management.adapter.CartAdapter;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.CartResponse;

import java.text.DecimalFormat;
import java.util.List;

public class CheckOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalAmount;
    private Button btnConfirm;
    private List<CartResponse> selectedItems;
    private AddressResponse selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_order);

        recyclerView = findViewById(R.id.recyclerView_product_list);
        ImageButton btnBack = findViewById(R.id.btn_back);
        tvTotalAmount = findViewById(R.id.total_amount);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        selectedItems = (List<CartResponse>) intent.getSerializableExtra("selectedItems");
        selectedAddress = (AddressResponse) intent.getSerializableExtra("selectedAddress");
        int totalAmount = intent.getIntExtra("totalAmount", 0);
        String paymentMethod = intent.getStringExtra("paymentMethod");

        // Hiển thị danh sách sản phẩm lên RecyclerView với VIEW_MODE
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(this, selectedItems, CartAdapter.VIEW_MODE);
        recyclerView.setAdapter(adapter);

        // Hiển thị totalAmount
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalAmount.setText(String.format("%s đ", formatter.format(totalAmount)));

        setBackground();

        btnBack.setOnClickListener(v -> { onBackPressed(); });
    }

    private void setBackground(){
        findViewById(R.id.step1Icon).setVisibility(View.VISIBLE);
        findViewById(R.id.step1Number).setVisibility(View.GONE);

        // Bước 2
        findViewById(R.id.step2Icon).setVisibility(View.VISIBLE);
        findViewById(R.id.step2Number).setVisibility(View.GONE);
        findViewById(R.id.step2Frame).setBackgroundResource(R.drawable.bg_step_active);

        // Bước 3
        findViewById(R.id.step3Icon).setVisibility(View.GONE);
        findViewById(R.id.step3Frame).setBackgroundResource(R.drawable.bg_step_active);

        // Line màu xanh
        findViewById(R.id.line1).setBackgroundColor(Color.parseColor("#4CAF50"));
        findViewById(R.id.line2).setBackgroundColor(Color.parseColor("#4CAF50"));
    }
}