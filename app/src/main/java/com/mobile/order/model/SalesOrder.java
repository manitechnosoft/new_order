package com.mobile.order.model;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.DaoException;

public class SalesOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long id;
    private String salesOrderDocId;

    @ToMany(referencedJoinProperty = "id")
    @OrderBy("productName DESC")
    private List<Product> productList = new ArrayList<>();
    private String salesPersonId;
    private String customerName="";
    private String customerPhone="";
    private Double total;
    private boolean isSync;
    private Date createdOn;
    private Date updatedOn;

    /**
     * Used for active entity operations.
     */
    //private transient SalesOrderDao myDao;
    public SalesOrder(){

    }

    @Generated(hash = 1096727281)
    public SalesOrder(Long id, String salesOrderDocId, String salesPersonId, String customerName,
            String customerPhone, Double total, boolean isSync, Date createdOn, Date updatedOn) {
        this.id = id;
        this.salesOrderDocId = salesOrderDocId;
        this.salesPersonId = salesPersonId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.total = total;
        this.isSync = isSync;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSalesOrderDocId() {
        return salesOrderDocId;
    }

    public void setSalesOrderDocId(String salesOrderDocId) {
        this.salesOrderDocId = salesOrderDocId;
    }

   

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getSalesPersonId() {
        return salesPersonId;
    }

    public void setSalesPersonId(String salesPersonId) {
        this.salesPersonId = salesPersonId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

  /*  public SalesOrderDao getMyDao() {
        return myDao;
    }

    public void setMyDao(SalesOrderDao myDao) {
        this.myDao = myDao;
    }*/

    public String toString(){
        return id+" " + (null!=salesPersonId?salesPersonId+ " ":"")+total;
    }

    public boolean getIsSync() {
        return this.isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }


    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public List<Product> getProductList() {
        return productList;
    }
}
