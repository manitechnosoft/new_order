package com.mobile.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.adapter.DisplayUpdateProductsAdapter;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.helper.FontHelper;
import com.mobile.order.helper.Fonts;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.ProductDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisplayAndUpdateProductActivity  extends BaseActivity implements FirestoreProducts {
    @BindView(R.id.product_list)
    RecyclerView productList;

    @BindView(R.id.btn_confirm)
    Button updateProducts;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    boolean editFlag = true;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Product> rvProductList=new ArrayList<>();
    final String TAG ="DisplayProductActivity";
    DaoSession daoSession;
    ProductDao productDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_update_products);
        ButterKnife.bind(this);

        //productList.setNestedScrollingEnabled(true);
        // specify an adapter (see also next example)

        //getAllProducts();
        daoSession = ((BaseApplication) getApplication()).getDaoInstance();
        productDao = daoSession.getProductDao();

        FirestoreUtil util=new FirestoreUtil();
        util.getProducts(DisplayAndUpdateProductActivity.this, null, true);
        initializeToolbar();
    }
    public void loadProducts(List<Product> dbProductList){
        rvProductList = dbProductList;

        // com.google.firebase.quickstart.auth.greendao.DividerItemDecoration dividerItemDecoration =new com.google.firebase.quickstart.auth.greendao.DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(), R.drawable.item_decorator));
        // productList.addItemDecoration(dividerItemDecoration);

        mAdapter = new DisplayUpdateProductsAdapter(this, rvProductList);
        productList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        productList.setLayoutManager(mLayoutManager);
        productList.setAdapter(mAdapter);
        productList.setItemViewCacheSize(rvProductList.size());
        mAdapter.notifyDataSetChanged();
    }
    @OnClick(R.id.btn_confirm)
    public void updateProducts(View button) {

        for(Product aProduct:rvProductList){
            if(aProduct.getProductName().isEmpty()){
                Toast productErr = Toast.makeText(getApplicationContext(),
                        "Product Name can not be empty",
                        Toast.LENGTH_LONG);
                productErr.show();
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        updateProducts.setEnabled(false);
        AppUtil.putInProductPref(this, 0);
        AppUtil.putInProductPref(DisplayAndUpdateProductActivity.this, 0);
        final int totalRow = null!=rvProductList?rvProductList.size():0;
        for(final Product aProduct:rvProductList){
            final DocumentReference productDocRef = FirestoreUtil.getProductCollectionRef().document(aProduct.getProductDocId());
            productDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final Product dbProduct = documentSnapshot.toObject(Product.class);
                    dbProduct.setProductName(aProduct.getProductName());
                    dbProduct.setManufacturer(aProduct.getManufacturer());

                    dbProduct.setRetailSaleType(aProduct.getRetailSaleType());
                    dbProduct.setRetailSalePrice(aProduct.getRetailSalePrice());
                    productDocRef.set(dbProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Product "+ dbProduct.getProductName() +" has been updated Successfully!");
                            int count = AppUtil.getFromProductPref(DisplayAndUpdateProductActivity.this);
                            count++;
                            AppUtil.putInProductPref(DisplayAndUpdateProductActivity.this, count);
                            if(count == totalRow){
                                AppUtil.putInProductPref(DisplayAndUpdateProductActivity.this, 0);
                                updateProducts.setEnabled(true);
                                FirestoreUtil util=new FirestoreUtil();
                                util.getProducts(DisplayAndUpdateProductActivity.this, null, false);
                                Toast.makeText(getApplicationContext(),
                                        "All Products are updated!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Product " + dbProduct.getProductName() +" has not been updated Successfully!", e);
                        }
                    });
                }
            });
        }
        updateInLocalDatabase(rvProductList);
    }
    private void updateInLocalDatabase(List<Product> productList){
        productDao.deleteAll();
        for(Product aProduct:productList){
            aProduct.setId(null);
            productDao.insert(aProduct);
        }
    }
    @OnClick(R.id.cancel)
    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * Method used to initialize toolbar
     */
    private void initializeToolbar() {
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setTitle(AppUtil.applyFontStyle(
                    this.getResources().getString(R.string.list_products),
                    FontHelper.getFont(Fonts.MULI_SEMI_BOLD))
            );
            supportActionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_arrow_dark));
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setCustomView(R.layout.activity_main);
            supportActionBar.setElevation(4);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public boolean isEditFlag() {
        return editFlag;
    }

    public void setEditFlag(boolean editFlag) {
        this.editFlag = editFlag;
    }
}
