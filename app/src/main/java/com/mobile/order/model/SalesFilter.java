package com.mobile.order.model;

import java.util.Date;

public class SalesFilter {
    private Date fromDate;
    private Date toDate;
    private String customerName;
    private String salesPersonId;
    private Boolean isFullySettled;
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Boolean isFullySettled() {
        return isFullySettled;
    }

    public void setFullySettled(Boolean fullySettled) {
        isFullySettled = fullySettled;
    }

    public String getSalesPersonId() {
        return salesPersonId;
    }

    public void setSalesPersonId(String salesPersonId) {
        this.salesPersonId = salesPersonId;
    }
}
