package com.mobile.order.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.ProductDao;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigActivity  extends BaseActivity implements
        FirestoreProducts, FirestoreSalesPersons {
    DaoSession daoSession;
    private ProductDao productDao;
    private SalesPersonDao salesPersonDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        daoSession = ((BaseApplication) this.getApplication()).getDaoInstance();
        productDao = daoSession.getProductDao();
        salesPersonDao = daoSession.getSalesPersonDao();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public void loadProducts(List<Product> productList){
        productDao.deleteAll();
        for(Product aProduct:productList){
            aProduct.setId(null);
            productDao.insert(aProduct);
        }
        Toast.makeText(ConfigActivity.this, "Products Synchronized Successfully!",
                Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.sync_product)
    public void syncProducts() {
        FirestoreUtil util=new FirestoreUtil();
        util.getProducts(this,null, true);

    }
    @OnClick(R.id.sync_sales_persons)
    public void syncSalesPersons() {
        FirestoreUtil util=new FirestoreUtil();
        util.getSalesPersonList(this,null);

    }
    public void loadSalesPersons(List<SalesPerson> aList){
        salesPersonDao.deleteAll();
        for(SalesPerson aProduct:aList){
            aProduct.setId(null);
            salesPersonDao.insert(aProduct);
        }
        Toast.makeText(ConfigActivity.this, "Sales person list has been synchronized successfully!",
                Toast.LENGTH_SHORT).show();
    }
}
