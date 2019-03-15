package com.mobile.order.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SalesPerson {
    @Id(autoincrement = true)
    private Long id;
    private String salesPersonDocId;
    private String salesPersonId;
    private String firstName;
    private String lastName;
    private boolean isSync;

    private transient SalesPersonDao salesPersonDao;
    public SalesPerson(){

    }
public SalesPerson(String salesPersonId, String firstName, String lastName){
    this.salesPersonId = salesPersonId;
    this.firstName = firstName;
    this.lastName = lastName;
}
@Generated(hash = 64305358)
public SalesPerson(Long id, String salesPersonDocId, String salesPersonId, String firstName, String lastName,
        boolean isSync) {
    this.id = id;
    this.salesPersonDocId = salesPersonDocId;
    this.salesPersonId = salesPersonId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.isSync = isSync;
}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSalesPersonId() {
        return salesPersonId;
    }

    public void setSalesPersonId(String salesPersonId) {
        this.salesPersonId = salesPersonId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
    public boolean getIsSync() {
        return this.isSync;
    }
    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    public String getSalesPersonDocId() {
        return salesPersonDocId;
    }

    public void setSalesPersonDocId(String salesPersonDocId) {
        this.salesPersonDocId = salesPersonDocId;
    }

    public String toString(){
        return (null!=salesPersonId?salesPersonId+ " ":"")+ (null!=firstName?firstName:"");
    }
}
