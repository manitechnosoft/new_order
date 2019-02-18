package com.mobile.order.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.mobile.order.activity.SalesOrderActivity;
import com.mobile.order.activity.SalesOrderDisplayActivity;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.adapter.FirestoreSales;
import com.mobile.order.adapter.FirestoreSalesFilter;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.model.Product;
import com.mobile.order.model.SalesFilter;
import com.mobile.order.model.SalesOrder;
import com.mobile.order.model.SalesPerson;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUtil {
    final private String TAG ="FirestoreUtil";
    final private static String STORE_NAME ="ajay";
    final private static String SALESINCREMENTKEY = STORE_NAME+"_salesincrementkey";

    final private String pattern = "dd/MM/yyyy";
    final public SimpleDateFormat filterDateFormat = new SimpleDateFormat(pattern);
    private static FirebaseFirestore mFirestore;
    public static FirebaseFirestore getFirestoreDB(){
        if(null==mFirestore){
            mFirestore = FirebaseFirestore.getInstance();
        }
        return mFirestore;
    }
    public static DocumentReference getSalesOrderIncrementCollectionRef(){
        return getFirestoreDB().collection(SALESINCREMENTKEY).document("uniquekey");
    }
    public static DocumentReference getPurchaseIncrementCollectionRef(){
        return getFirestoreDB().collection("purchaseincrementkey").document("uniquekey");
    }
    public static CollectionReference getProductCollectionRef(){
        return getFirestoreDB().collection("products");
    }
    public static CollectionReference getSalesPersonCollectionRef(){
        return getFirestoreDB().collection("salesperson");
    }
    public static CollectionReference getSalesCollectionRef(){
        return getFirestoreDB().collection("sales");
    }
    public static CollectionReference getPurchaseCollectionRef(){
        return getFirestoreDB().collection("purchase");
    }
    public static CollectionReference getLocationCollectionRef(){
        return getFirestoreDB().collection("locations");
    }
    public void getProducts(final Context ctx, final Fragment currentFragment){
        final List<Product> productList=new ArrayList<Product>();

        Query query = FirestoreUtil.getProductCollectionRef();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    if(task.getResult().size()==0){
                        Toast.makeText(ctx, "Product Not Existed!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{

                        for (DocumentSnapshot document : task.getResult()) {
                            Product aProduct = document.toObject(Product.class);
                            aProduct.setProductDocId(document.getId());
                            productList.add(aProduct);

                            Log.i(TAG,"Retrieved product "+aProduct.getProductName());
                        }
                        FirestoreProducts firestoreInterface = null;
                        if(null!=currentFragment){
                            firestoreInterface = (FirestoreProducts)currentFragment;
                        }
                        else{
                            firestoreInterface = (FirestoreProducts)ctx;
                        }
                        firestoreInterface.loadProducts(productList);
                    }
                }
                else {
                    Toast.makeText(ctx, "Error getting documents.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void getSalesPersonList(final Context ctx, final Fragment currentFragment){
        final List<SalesPerson> personList=new ArrayList<>();

        Query query = FirestoreUtil.getSalesPersonCollectionRef();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    if(task.getResult().size()==0){
                        Toast.makeText(ctx, "Sales Person Not Existed!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{

                        for (DocumentSnapshot document : task.getResult()) {
                            SalesPerson aPerson = document.toObject(SalesPerson.class);
                            aPerson.setSalesPersonDocId(document.getId());
                            personList.add(aPerson);
                        }
                        FirestoreSalesPersons firestoreInterface = null;
                        if(null==currentFragment){
                            firestoreInterface = (FirestoreSalesPersons)ctx;
                        }
                        else{
                            firestoreInterface = (FirestoreSalesPersons)currentFragment;
                        }
                        firestoreInterface.loadSalesPersons(personList);
                    }
                }
                else {
                    Toast.makeText(ctx, "Error getting documents.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getSalesWithFilter(final Object ctx, SalesFilter selectionFilter, final Boolean loadCustomerSpinner) {

        final List<SalesOrder> salesList = new ArrayList<>();
        Query query = FirestoreUtil.getSalesCollectionRef()
                .whereGreaterThanOrEqualTo("updatedOn",selectionFilter.getFromDate());
        query = query.whereLessThanOrEqualTo("updatedOn",selectionFilter.getToDate());
        if(selectionFilter.getCustomerName()!=null && !selectionFilter.getSalesPersonId().isEmpty()){
            query = query.whereEqualTo("salesPersonId",selectionFilter.getSalesPersonId());
        }
        if(selectionFilter.isFullySettled()!=null && selectionFilter.isFullySettled()){
            query = query.whereEqualTo("fullSettlement",true);
        }
        else if(selectionFilter.isFullySettled()!=null && !selectionFilter.isFullySettled()){
            query = query.whereEqualTo("fullSettlement",false);
        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    if(task.getResult().size()>=0){
                        for (DocumentSnapshot document : task.getResult()) {
                            SalesOrder aSale = document.toObject(SalesOrder.class);
                            aSale.setSalesOrderDocId(document.getId());
                            salesList.add(aSale);
                        }
                        if(loadCustomerSpinner){
                            FirestoreSalesFilter firestoreInterface = (FirestoreSalesFilter)ctx;
                            firestoreInterface.refreshCustomerSpinner(salesList);
                        }
                        else{
                            FirestoreSales firestoreSales = (FirestoreSales) ctx;
                            firestoreSales.loadSalesItems(salesList);
                        }

                    }
                }
                else {
                    String err = task.getException().getMessage();
                    err = err+"";

                    Toast.makeText((Context)ctx, err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static void incrementSalesOrderCounterAndCreateOrder(final Context context,final SalesOrder insertSales){
        FirestoreUtil.getFirestoreDB().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(FirestoreUtil.getSalesOrderIncrementCollectionRef());
                Long newCounter  = 0L;
                if(null!=snapshot.getLong("counter")){
                    newCounter  = snapshot.getLong("counter") + 1;
                    transaction.update(FirestoreUtil.getSalesOrderIncrementCollectionRef(), "counter", newCounter);
                }
                else{
                    Map<String, Object> data = new HashMap<>();
                    data.put("counter", newCounter);
                    FirestoreUtil.getSalesOrderIncrementCollectionRef().set(data);
                }
                FirestoreUtil.createSalesOrder(context, newCounter, insertSales);
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //confirmSalesOrder.setEnabled(true);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w(TAG, "Transaction failure.", e);
                       // confirmSalesOrder.setEnabled(true);
                    }
                });
    }
    public static void createSalesOrder(final Context context, final Long newCounter,final SalesOrder updatedSales){
        final String TAG = "updateSales";
        updatedSales.setId(newCounter);
        FirestoreUtil.getSalesCollectionRef()
                .add(updatedSales)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //salesDocRef =documentReference;
                                        SalesOrder aSales = document.toObject(SalesOrder.class);
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                        Toast.makeText(context, "Updated with reference ID: "+documentReference.getId(),
                                Toast.LENGTH_SHORT).show();

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
}
