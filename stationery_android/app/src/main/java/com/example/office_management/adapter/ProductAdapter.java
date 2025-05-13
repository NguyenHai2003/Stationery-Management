package com.example.office_management.adapter;

import static com.example.office_management.activity.user.ProductDetailActivity.formatter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.office_management.R;


import java.util.ArrayList;
import java.util.List;

import com.example.office_management.activity.user.ProductDetailActivity;
import com.example.office_management.dto.response.product.ProductDetailResponse;
import com.example.office_management.dto.response.product.ProductResponse;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductResponse> productList;
    private Context context;

    public ProductAdapter(Context context, List<ProductResponse> productList) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
    }

    public void setData(List<ProductResponse> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        Log.d("Adapter", "Data updated, item count: " + productList.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);
        ProductDetailResponse detail = product.getProductDetail();
        Log.d("Adapter", "Binding product: " + product.getName() + ", Position: " + position);

        holder.tvProductName.setText(product.getName() != null ? product.getName() : "N/A");

        // Kiểm tra null cho discountPrice và originalPrice
        holder.tvProductPrice.setText(String.format("%sđ", formatter.format(
                detail.getDiscountPrice() > 0 ? detail.getDiscountPrice() : detail.getOriginalPrice()
        )));

        String originalPriceText = String.format("%sđ", formatter.format(detail.getOriginalPrice()));
        SpannableString spannableString = new SpannableString(originalPriceText);
        spannableString.setSpan(new StrikethroughSpan(), 0, originalPriceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvOldPrice.setText(spannableString);

        // Kiểm tra null cho totalRating
        //holder.tvProductRating.setText("★ " + String.valueOf(product.getTotalRating()));
        holder.tvProductRating.setText(TextUtils.getStarRatingText(product.getTotalRating()));
        holder.tvSold.setText("Sold: " + String.valueOf(product.getSoldQuantity()));

        // Tính toán phần trăm giảm giá
        int discountPercent = 0;
        if (detail.getOriginalPrice() > 0 && detail.getDiscountPrice() > 0) {
            discountPercent = 100 - (detail.getDiscountPrice() * 100 / detail.getOriginalPrice());
        }
        holder.tvDiscount.setText("-" + discountPercent + "%");

        // Tải hình ảnh
        String imageUrl = detail.getThumbnail() != null ? detail.getThumbnail() : product.getImg();
        Log.d("Glide", "Loading image: " + imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200)
                .placeholder(R.drawable.ic_box)
                .error(R.drawable.ic_box)
                .into(holder.imgProduct);

        // Thêm sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("slug", detail.getSlug()); // Gửi slug sang ProductDetailActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvOldPrice, tvDiscount, tvProductRating, tvSold;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvProductRating = itemView.findViewById(R.id.productRating);
            tvSold = itemView.findViewById(R.id.tvSold);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    public static class TextUtils {
        public static SpannableString getStarRatingText(double rating) {
            String star = "★";
            String ratingText = star + " " + rating;
            SpannableString spannable = new SpannableString(ratingText);

            // Tô màu vàng cho ký tự ngôi sao
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#FFD700")), // Màu vàng
                    0, 1, // Chỉ ký tự "★"
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            return spannable;
        }
    }

}
