package com.mobile.order.adapter;

import com.mobile.order.model.Product;

import java.util.List;

public interface FirestoreProducts {
    void loadProducts(List<Product> productList);
}
