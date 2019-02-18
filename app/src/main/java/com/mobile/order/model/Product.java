package com.mobile.order.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Product {
    @Id(autoincrement = true)
    private Long id;
    private String productDocId;
    private String productName;
    private String productId;
    private String manufacturer;
    private Double retailSalePrice;
    private String retailSaleType;
    private Float quantity;
    private boolean isSync;

    /**
     * Used for active entity operations.
     */
    private transient ProductDao myDao;
    public Product(){

    }

public Product(String productId, String productName, String manufacturer){
    this.productName = productName;
    this.manufacturer = manufacturer;
    this.productId = productId;
}

@Generated(hash = 1567143239)
public Product(Long id, String productDocId, String productName,
        String productId, String manufacturer, Double retailSalePrice,
        String retailSaleType, Float quantity, boolean isSync) {
    this.id = id;
    this.productDocId = productDocId;
    this.productName = productName;
    this.productId = productId;
    this.manufacturer = manufacturer;
    this.retailSalePrice = retailSalePrice;
    this.retailSaleType = retailSaleType;
    this.quantity = quantity;
    this.isSync = isSync;
}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getRetailSalePrice() {
        return retailSalePrice;
    }

    public void setRetailSalePrice(Double retailSalePrice) {
        this.retailSalePrice = retailSalePrice;
    }

    public String getRetailSaleType() {
        return retailSaleType;
    }

    public void setRetailSaleType(String retailSaleType) {
        this.retailSaleType = retailSaleType;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public ProductDao getMyDao() {
        return myDao;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public void setMyDao(ProductDao myDao) {
        this.myDao = myDao;
    }
    public boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }
    public String toString(){
        return (null!=productId?productId+ " ":"")+productName;
    }
}
