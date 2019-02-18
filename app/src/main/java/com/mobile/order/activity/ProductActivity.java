package com.mobile.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobile.order.R;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.config.AppController;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.ProductDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivity extends AppCompatActivity implements
        FirestoreProducts {
    private Query mQuery;
    private ActionBar supportActionBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cancel_product)
    Button cancel;


    @BindView(R.id.retail_type)
    Spinner retailType;

    @BindView(R.id.retail_price)
    EditText retailPrice;
    DaoSession daoSession;
    ProductDao productDao;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        initToolBar();
        daoSession = ((AppController) getApplication()).getDaoSession();
        productDao = daoSession.getProductDao();
    }

    private void initToolBar(){
            setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
            supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                enableBackButton();
                supportActionBar.setDisplayShowTitleEnabled(false);
                supportActionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.back_arrow_dark));
                supportActionBar.setDisplayShowTitleEnabled(true);
            }
    }
    /**
     * Method used to enable back button
     */
    private void enableBackButton() {
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
    }
    @OnClick(R.id.add_product)
    public void addProduct(View button) {
        Product aProduct = prepareProduct();
        validateProduct(aProduct);

        aProduct.setRetailSaleType(retailType.getSelectedItem().toString());
        Double price = Double.valueOf(retailPrice.getText().toString().isEmpty()?"0":retailPrice.getText().toString());
        aProduct.setRetailSalePrice(price);

        updateProduct(aProduct);
    }
    @OnClick(R.id.cancel_product)
    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
    }
private Product prepareProduct(){
    final EditText productField = findViewById(R.id.product_name);
    String product = productField.getText().toString();
    final EditText productId = findViewById(R.id.product_id);
    Product aProduct = new Product();
    aProduct.setProductId(productId.getText().toString());
    aProduct.setProductName(product);
    return aProduct;
}



    public void updateProduct(final Product aProduct) {
        final String TAG = "updatePerson";
        if(validateProduct(aProduct)){
        Query query = FirestoreUtil.getProductCollectionRef();
        query = query.whereEqualTo("productId", aProduct.getProductName());

         query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    if(task.getResult().size()==0){
                        FirestoreUtil.getProductCollectionRef()
                                .add(aProduct)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(ProductActivity.this, "Added Newly with reference ID: "+documentReference.getId(),
                                                Toast.LENGTH_SHORT).show();
                                        //Insert into local database
                                        FirestoreUtil util=new FirestoreUtil();
                                        util.getProducts(ProductActivity.this,null);
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                    else{
                        Toast.makeText(ProductActivity.this, "Product Existed!",
                                Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot document : task.getResult()) {
                            Toast.makeText(ProductActivity.this, "Reference ID: "+document.getId(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    }

                }
                else {
                    Toast.makeText(ProductActivity.this, "Error getting documents.",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

       /* mFirestore.collection("products").document("products").set(aProduct)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProductActivity.this, "User Registered",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductActivity.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });*/

        //productReference.add(aProduct);
        }
    }

    public void loadProducts(List<Product> productList){
        productDao.deleteAll();
        for(Product aProduct:productList){
            aProduct.setId(null);
            productDao.insert(aProduct);
        }
    }



    private boolean validateProduct(final Product aProduct){
        final String TAG = "updatePerson";
        boolean validFlg = true;
        if(TextUtils.isEmpty(aProduct.getProductName()))
        {
            final TextView productErr = findViewById(R.id.productname_error_msg);
            productErr.setVisibility(View.VISIBLE);
            validFlg = false;
        }
        if(TextUtils.isEmpty(aProduct.getProductId()))
        {
            AppUtil.longToast(this,"Product Id is required.");
            validFlg = false;
        }
        return validFlg;
    }
}