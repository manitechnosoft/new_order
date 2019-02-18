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
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.config.AppController;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SalesPersonActivity extends AppCompatActivity implements FirestoreSalesPersons {
    private Query mQuery;
    private ActionBar supportActionBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cancel_product)
    Button cancel;
    DaoSession daoSession;
    SalesPersonDao salesPersonDao;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales_person);
        ButterKnife.bind(this);
        daoSession = ((AppController) getApplication()).getDaoSession();
        salesPersonDao = daoSession.getSalesPersonDao();
        initToolBar();
    }

    private void initToolBar(){
            setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesPersonActivity.this, MainActivity.class);
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
    @OnClick(R.id.add_sales_person)
    public void addPerson(View button) {
        SalesPerson aPerson = prepareSalesPerson();
        validatePerson( aPerson);

        updatePerson(aPerson);
    }
    @OnClick(R.id.cancel_product)
    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
private SalesPerson prepareSalesPerson(){
    final EditText personIdField = findViewById(R.id.sales_person_id);
    final EditText firstNameField = findViewById(R.id.first_name);
    final EditText lastNameField = findViewById(R.id.last_name);
    SalesPerson aPerson = new SalesPerson();
    aPerson.setSalesPersonId(personIdField.getText().toString());
    aPerson.setFirstName(firstNameField.getText().toString());
    aPerson.setLastName(lastNameField.getText().toString());
    return aPerson;
}



    public void updatePerson(final SalesPerson salesPerson) {
        final String TAG = "updatePerson";
        if(validatePerson(salesPerson)){
        Query query = FirestoreUtil.getSalesPersonCollectionRef();
        query = query.whereEqualTo("salesPersonId", salesPerson.getSalesPersonId());

         query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    if(task.getResult().size()==0){
                        FirestoreUtil.getSalesPersonCollectionRef()
                                .add(salesPerson)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(SalesPersonActivity.this, "Added Sales Person with reference ID: "+documentReference.getId(),
                                                Toast.LENGTH_SHORT).show();
                                        //update in local database
                                        FirestoreUtil util=new FirestoreUtil();
                                        util.getSalesPersonList(SalesPersonActivity.this,null);
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
                        Toast.makeText(SalesPersonActivity.this, "Sales Person Existed!",
                                Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot document : task.getResult()) {
                            Toast.makeText(SalesPersonActivity.this, "Reference ID: "+document.getId(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    }

                }
                else {
                    Toast.makeText(SalesPersonActivity.this, "Error getting documents.",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

       /* mFirestore.collection("products").document("products").set(salesPerson)
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

        //productReference.add(salesPerson);
        }
    }

    private boolean validatePerson(final SalesPerson aPerson){
        boolean validFlg = true;
        if(TextUtils.isEmpty(aPerson.getSalesPersonId()))
        {
            AppUtil.longToast(this,"Sales Person Id is required.");
            validFlg = false;
        }
        if(TextUtils.isEmpty(aPerson.getFirstName()))
        {
            AppUtil.longToast(this,"First Name is required.");
            validFlg = false;
        }
        return validFlg;
    }
    public void loadSalesPersons(List<SalesPerson> aList){
        salesPersonDao.deleteAll();
        for(SalesPerson salesPerson:aList){
            salesPerson.setId(null);
            salesPersonDao.insert(salesPerson);
        }
    }

}
