package com.example.office_management.dto.response;

import java.io.Serializable;
import java.util.Date;

public class CartResponse implements Serializable {
    private String userId;
    private String productId;
    private String productDetailId;
    private String productName;
    private String colorName;
    private String sizeName;
    private int quantity;
    private int originalPrice;
    private int discountPrice;
    private String imageUrl;
    private Date createdAt;
    private String slug;
    private boolean isSelected = false;

    public CartResponse(){}

    public String getProductDetailId() {
        return productDetailId;
    }

    public String getProductName() {
        return productName;
    }

    public String getColorName() {
        return colorName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSlug() { return slug; }
}
