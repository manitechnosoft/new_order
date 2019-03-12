package com.mobile.order.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.mobile.order.BaseApplication;
import com.mobile.order.activity.DisplayAndUpdateProductActivity;
import com.mobile.order.activity.DisplayAndUpdateSalesPersonActivity;
import com.mobile.order.activity.MainActivity;
import com.mobile.order.activity.ProductActivity;
import com.mobile.order.activity.SalesOrderActivity;
import com.mobile.order.activity.SalesOrderDisplayActivity;
import com.mobile.order.activity.SalesOrderLandActivity;
import com.mobile.order.activity.SalesOrderSimpleDisplayActivity;
import com.mobile.order.activity.SuccessActivity;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.adapter.FirestoreSales;
import com.mobile.order.adapter.FirestoreSalesFilter;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.ProductDao;
import com.mobile.order.model.SalesFilter;
import com.mobile.order.model.SalesOrder;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;


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
    public static void updateProductInFirebase(final Activity addProduct, Product aProduct){
        FirestoreUtil.getProductCollectionRef()
                .add(aProduct)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(addProduct, "Added Newly with reference ID: "+documentReference.getId(),
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(addProduct, MainActivity.class);
                        addProduct.startActivity(intent);
                        //Insert into local database
                        FirestoreUtil util=new FirestoreUtil();
                        util.getProducts(addProduct,null, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addProduct, "Error adding document "+e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void deleteSalesPersonInFirebase(final DisplayAndUpdateSalesPersonActivity activity, final String salesPersonDocId){
        FirestoreUtil.getSalesPersonCollectionRef().document(salesPersonDocId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "Deleted Successfully!",
                                Toast.LENGTH_SHORT).show();
                        activity.setEditFlag(true);
                        FirestoreUtil util=new FirestoreUtil();
                        util.getSalesPersonList(activity, null);
                       /*  notifyDataSetChanged();
                        Activity activity = (Activity) context;
                        activity.recreate();*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, salesPersonDocId+ " Delete failure!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void deleteProductInFirebase(final DisplayAndUpdateProductActivity activity,final String productDocId){
        FirestoreUtil.getProductCollectionRef().document(productDocId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Toast.makeText(activity, "Deleted Successfully!",
                                Toast.LENGTH_SHORT).show();
                        activity.setEditFlag(true);
                        FirestoreUtil util=new FirestoreUtil();
                        util.getProducts(activity, null, true);
                       /*  notifyDataSetChanged();
                        Activity activity = (Activity) context;
                        activity.recreate();*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, productDocId+ " Delete failure!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static CollectionReference getSalesPersonCollectionRef(){
        CollectionReference salesPersonRef = getFirestoreDB().collection("salesperson");
        salesPersonRef.addSnapshotListener(getSalesPersonCollectionListener());
        return salesPersonRef;
    }
    public static CollectionReference getSalesCollectionRefToDisplay(Context ctx){
        CollectionReference salesRef = getFirestoreDB().collection("sales");

        //salesRef.addSnapshotListener(getSalesCollectionListener());

        return salesRef;
    }
    public static CollectionReference getSalesCollectionRef(){
        CollectionReference salesRef = getFirestoreDB().collection("sales");
        return salesRef;
    }
    private static EventListener<QuerySnapshot> getSalesCollectionListener(){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                //If something went wrong
                if (e != null)

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        //Instead of simply using the entire query snapshot
                        //See the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (doc.getType()) {
                                case ADDED:{
                                    refreshSalesOrdersList();
                                    break;
                                }
                                case MODIFIED:
                                {
                                    refreshSalesOrdersList();
                                    break;
                                }
                                case REMOVED:
                                {
                                    refreshSalesOrdersList();
                                    break;
                                }
                            }
                        }
                    }

            }
        };
    }
    private static void refreshSalesOrdersList(){
        Activity currentActivity = AppUtil.getRunningActivity();
        if(currentActivity instanceof SalesOrderSimpleDisplayActivity){
            SalesOrderSimpleDisplayActivity salesOrderDisplay = (SalesOrderSimpleDisplayActivity)currentActivity;
            salesOrderDisplay.fetchSalesList();
        }
    }
    private static EventListener<QuerySnapshot> getSalesPersonCollectionListener(){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                //If something went wrong
                if (e != null)
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        //Instead of simply using the entire query snapshot
                        //See the actual changes to query results between query snapshots (added, removed, and modified)
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (doc.getType()) {
                                case ADDED:{
                                    refreshSalesPersonDBList();
                                    break;
                                }
                                case MODIFIED:
                                {
                                    refreshSalesPersonDBList();
                                    break;
                                }
                                case REMOVED:
                                {
                                    refreshSalesPersonDBList();
                                    break;
                                }
                            }
                        }
                    }

            }
        };
    }
    private static void refreshSalesPersonDBList(){
        FirestoreUtil util=new FirestoreUtil();
        util.getSalesPersonList(null,null);
    }
    public static CollectionReference getPurchaseCollectionRef(){
        return getFirestoreDB().collection("purchase");
    }
    public static CollectionReference getLocationCollectionRef(){
        return getFirestoreDB().collection("locations");
    }
    public void getProducts(final Context ctx, final Fragment currentFragment,final boolean loadProductsFlg){
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
                        DaoSession daoSession = ((BaseApplication)ctx.getApplicationContext()).getDaoInstance();
                        ProductDao productDao = daoSession.getProductDao();
                        productDao.deleteAll();
                        for(Product aProduct:productList){
                            aProduct.setId(null);
                            productDao.insert(aProduct);
                        }
                        FirestoreProducts firestoreInterface = null;
                        if(null!=currentFragment){
                            firestoreInterface = (FirestoreProducts)currentFragment;
                        }
                        else if(null!=ctx){
                            firestoreInterface = (FirestoreProducts)ctx;
                        }
                        if(null!= firestoreInterface && loadProductsFlg)
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
                        Toast.makeText(ctx, "Sales Person Not is not existed in database!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{

                        for (DocumentSnapshot document : task.getResult()) {
                            SalesPerson aPerson = document.toObject(SalesPerson.class);
                            aPerson.setSalesPersonDocId(document.getId());
                            personList.add(aPerson);
                        }
                        DaoSession daoSession = ((BaseApplication)ctx.getApplicationContext()).getDaoInstance();
                        SalesPersonDao salesPersonDao = daoSession.getSalesPersonDao();
                        salesPersonDao.deleteAll();
                        for(SalesPerson aProduct:personList){
                            aProduct.setId(null);
                            salesPersonDao.insert(aProduct);
                        }
                        FirestoreSalesPersons firestoreInterface = null;

                        if(null==currentFragment){
                            firestoreInterface = (FirestoreSalesPersons)ctx;
                        }
                        else if(null!=currentFragment) {
                            firestoreInterface = (FirestoreSalesPersons)currentFragment;
                        }
                        if(null!=firestoreInterface)
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
        CollectionReference collectionRef =FirestoreUtil.getSalesCollectionRef();
        Query query = collectionRef.whereGreaterThanOrEqualTo("updatedOn",selectionFilter.getFromDate());
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
        query = query.orderBy("updatedOn", Query.Direction.DESCENDING);
        query = query.orderBy("id", Query.Direction.DESCENDING);
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

                    if(ctx instanceof Context)
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
                        SalesOrderLandActivity landActivity=(SalesOrderLandActivity)context;
                        landActivity.gotoSuccessActivity();
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
