package com.example.office_management.dto.request.order;

import java.io.Serializable;

public class PurchaseOrderProductRequest implements Serializable {
    private String productDetailId;
    private Integer quantity;
    private String productPromotionId;

    public String getProductDetailId() {
        return productDetailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getProductPromotionId() {
        return productPromotionId;
    }
}
