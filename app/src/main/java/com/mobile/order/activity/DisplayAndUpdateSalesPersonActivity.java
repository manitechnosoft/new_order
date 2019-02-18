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
import com.mobile.order.R;
import com.mobile.order.adapter.DisplayUpdateProductsAdapter;
import com.mobile.order.adapter.DisplayUpdateSalesPersonAdapter;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.config.AppController;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.helper.FontHelper;
import com.mobile.order.helper.Fonts;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisplayAndUpdateSalesPersonActivity extends AppCompatActivity implements FirestoreSalesPersons {
    @BindView(R.id.product_list)
    RecyclerView productList;

    @BindView(R.id.btn_confirm)
    Button updateProducts;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SalesPerson> salesPersonList =new ArrayList<>();
    DaoSession daoSession;
    SalesPersonDao salesPersonDao;
    final String TAG ="DisplayProductActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_update_sales_person);
        ButterKnife.bind(this);
        daoSession = ((AppController) getApplication()).getDaoSession();
        salesPersonDao = daoSession.getSalesPersonDao();
        //productList.setNestedScrollingEnabled(true);
        // specify an adapter (see also next example)

        FirestoreUtil util=new FirestoreUtil();
        util.getSalesPersonList(DisplayAndUpdateSalesPersonActivity.this, null);
        initializeToolbar();

    }
    public void loadSalesPersons(List<SalesPerson> aList){
        salesPersonList = aList;

        mAdapter = new DisplayUpdateSalesPersonAdapter(salesPersonList);
        productList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        productList.setLayoutManager(mLayoutManager);
        productList.setAdapter(mAdapter);
        productList.setItemViewCacheSize(salesPersonList.size());
        mAdapter.notifyDataSetChanged();
    }
    @OnClick(R.id.btn_confirm)
    public void updateProducts(View button) {
        for(SalesPerson person: salesPersonList){
            if(person.getSalesPersonId().isEmpty()){
                Toast productErr = Toast.makeText(getApplicationContext(),
                        "ID can not be empty",
                        Toast.LENGTH_LONG);
                productErr.show();
                mAdapter.notifyDataSetChanged();
                break;
            }
        }

        for(final SalesPerson person: salesPersonList){
            final DocumentReference productDocRef = FirestoreUtil.getSalesPersonCollectionRef().document(person.getSalesPersonDocId());
            productDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final SalesPerson dbProduct = documentSnapshot.toObject(SalesPerson.class);
                    dbProduct.setSalesPersonId(person.getSalesPersonId());
                    dbProduct.setFirstName(person.getFirstName());
                    dbProduct.setLastName(person.getLastName());

                    productDocRef.set(dbProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Sales Person Record "+ dbProduct.getSalesPersonId() +" has been updated Successfully!");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Sales Person Record  " + dbProduct.getSalesPersonId() +" has not been updated Successfully!", e);
                        }
                    });
                }
            });
        }
    }
    @OnClick(R.id.cancel)
    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
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
                    this.getResources().getString(R.string.sales_persons_list),
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
    private void updateInLocalDatabase(List<SalesPerson> aList){
        salesPersonDao.deleteAll();
        for(SalesPerson aProduct:aList){
            aProduct.setId(null);
            salesPersonDao.insert(aProduct);
        }
    }
}
