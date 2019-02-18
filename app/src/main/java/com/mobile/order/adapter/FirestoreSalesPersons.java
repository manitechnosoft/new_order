package com.mobile.order.adapter;

import com.mobile.order.model.Product;
import com.mobile.order.model.SalesPerson;

import java.util.List;

public interface FirestoreSalesPersons {
    void loadSalesPersons(List<SalesPerson> aList);
}
