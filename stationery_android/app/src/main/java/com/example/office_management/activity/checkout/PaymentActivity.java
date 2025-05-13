package com.example.office_management.activity.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.office_management.R;
import com.example.office_management.activity.user.CouponCodeActivity;
import com.example.office_management.dto.response.AddressResponse;
import com.example.office_management.dto.response.CartResponse;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Button btnCheckout;
    private LinearLayout ll_coupon_code, momoLayout, cashLayout;
    private RadioButton radioBtnMomo, radioBtnCash;
    private ImageButton btnBack;
    private TextView tvAmount, tvDiscount, tvDiscountOfProduct, tvTotalAmount;
    private List<CartResponse> selectedItems;
    private AddressResponse selectedAddress;
    private int totalPrice;
    private String selectedPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        selectedItems = (List<CartResponse>) intent.getSerializableExtra("selectedItems");
        totalPrice = intent.getIntExtra("totalPrice", 0);
        selectedAddress = (AddressResponse) intent.getSerializableExtra("selectedAddress");

        setBackground();
        initViews();
        paymentMethob();
        updateTotalAmount();

        btnCheckout.setOnClickListener(v -> {
            // Truyền dữ liệu sang CheckOrderActivity
            Intent checkoutIntent = new Intent(PaymentActivity.this, CheckOrderActivity.class);
            checkoutIntent.putExtra("selectedItems", (Serializable) selectedItems);
            checkoutIntent.putExtra("selectedAddress", (Serializable) selectedAddress);
            checkoutIntent.putExtra("totalAmount", calculateTotalAmount());
            checkoutIntent.putExtra("paymentMethod", selectedPaymentMethod);
            startActivity(checkoutIntent);
        });

        ll_coupon_code.setOnClickListener(v -> {
            Intent couponIntent = new Intent(PaymentActivity.this, CouponCodeActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> { onBackPressed(); });

    }

    private void setBackground(){
        ((ImageView) findViewById(R.id.step1Icon)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.step1Number)).setVisibility(View.GONE);

        ((ImageView) findViewById(R.id.step2Icon)).setVisibility(View.GONE);
        findViewById(R.id.step2Frame).setBackgroundResource(R.drawable.bg_step_active);

        findViewById(R.id.line1).setBackgroundColor(Color.parseColor("#4CAF50"));
    }

    private void initViews() {
        btnCheckout = findViewById(R.id.btnCheckout);
        ll_coupon_code = findViewById(R.id.coupon_code);
        btnBack = findViewById(R.id.btn_back);
        radioBtnCash = findViewById(R.id.radioPayment);
        radioBtnMomo = findViewById(R.id.radioMomo);
        tvAmount = findViewById(R.id.amount);
        tvDiscount = findViewById(R.id.discount);
        tvDiscountOfProduct = findViewById(R.id.discount_of_product);
        tvTotalAmount = findViewById(R.id.total_amount);
        momoLayout = findViewById(R.id.layoutMomo);
        cashLayout = findViewById(R.id.layoutCash);
    }

    private void paymentMethob(){
        // Khai báo map giữa layout và radio button
        Map<LinearLayout, RadioButton> paymentOptions = new HashMap<>();

        radioBtnCash.setClickable(false);
        radioBtnCash.setFocusable(false);
        radioBtnMomo.setClickable(false);
        radioBtnMomo.setFocusable(false);

        // Ánh xạ layout với radio button tương ứng
        paymentOptions.put(cashLayout, radioBtnCash);
        paymentOptions.put(momoLayout, radioBtnMomo);

        // Chọn mặc định là Cash
        radioBtnCash.setChecked(true);
        selectedPaymentMethod = "Cash";

        View.OnClickListener layoutClickListener = clickedLayout -> {
            for (Map.Entry<LinearLayout, RadioButton> entry : paymentOptions.entrySet()) {
                RadioButton rb = entry.getValue();
                boolean isSelected = entry.getKey().getId() == clickedLayout.getId();
                rb.setChecked(isSelected);
                if (isSelected) {
                    selectedPaymentMethod = rb == radioBtnMomo ? "Momo" : "Cash";
                }
            }
        };

        // Gán cùng một listener cho tất cả layout
        for (LinearLayout layout : paymentOptions.keySet()) {
            layout.setOnClickListener(layoutClickListener);
        }
    }

    private void updateTotalAmount() {
        DecimalFormat formatter = new DecimalFormat("#,###");

        // Hiển thị totalPrice ban đầu lên tvAmount (nếu cần)
        tvAmount.setText(String.format("%s đ", formatter.format(totalPrice)));

        // Tính toán totalAmount với các khoản giảm giá
        int totalAmount = calculateTotalAmount();
        tvTotalAmount.setText(String.format("%s đ", formatter.format(totalAmount)));
    }

    private int calculateTotalAmount() {
        int totalAmount = totalPrice;

        // Giả sử tvDiscount và tvDiscountOfProduct chứa giá trị giảm giá (nếu có)
        try {
            String discountText = tvDiscount.getText().toString().replace(" đ", "").replace(",", "");
            if (!discountText.isEmpty() && !discountText.equals("-")) {
                int discount = Integer.parseInt(discountText.replace("-", ""));
                totalAmount -= discount;
            }

            String productDiscountText = tvDiscountOfProduct.getText().toString().replace(" đ", "").replace(",", "");
            if (!productDiscountText.isEmpty() && !productDiscountText.equals("-")) {
                int productDiscount = Integer.parseInt(productDiscountText.replace("-", ""));
                totalAmount -= productDiscount;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Đảm bảo totalAmount không âm
        return Math.max(totalAmount, 0);
    }
}