package com.mobile.order.adapter;

import com.mobile.order.model.SalesOrder;

import java.util.List;

public interface FirestoreSalesFilter {
    void refreshCustomerSpinner(List<SalesOrder> dbSalesList);
}
