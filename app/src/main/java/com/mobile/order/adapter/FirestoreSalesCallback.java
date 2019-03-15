package com.mobile.order.adapter;

import com.mobile.order.model.SalesOrder;

import java.util.List;

public interface FirestoreSalesCallback {
    void loadSalesItems(List<SalesOrder> salesList);
}
