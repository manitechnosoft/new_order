package com.mobile.order.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.adapter.FirestoreProductCallback;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.adapter.SalesOrderAdapter;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.ProductDao;
import com.mobile.order.model.SalesOrder;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SalesOrderActivity extends BaseActivity implements
		FirestoreProductCallback, FirestoreSalesPersons {
	String pattern = "dd-MMM-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	@BindView(R.id.sales_date)
	TextView salesDate;

    @BindView(R.id.product_name)
    Spinner products;
	@BindView(R.id.sales_person)
	Spinner salesPersons;
	@BindView(R.id.customer_name)
	EditText customerName;
	@BindView(R.id.mobile)
	EditText mobile;
    @BindView(R.id.add_sales_detail)
    FloatingActionButton addSalesDetail;
	@BindView(R.id.mrp)
	TextView mrp;
    @BindView(R.id.quantity)
    EditText quantity;
	@BindView(R.id.price)
	EditText price;
    @BindView(R.id.type)
    Spinner productType;

	@BindView(R.id.total)
	TextView orderTotal;
	@BindView(R.id.sales_id)
	TextView salesId;
@BindView(R.id.btn_confirm_save)
Button confirmSalesOrder;

	DaoSession daoSession;
	ProductDao productDao;
    SalesPersonDao salesPersonDao;
	//List<Product> productsList = new ArrayList<>();
	boolean createNew = false;
	FirestoreUtil fireHelper=new FirestoreUtil();
	private RecyclerView.Adapter salesDetailArrayAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	List<Product> productsDetailList =new ArrayList<>();
	RecyclerView listDetails;

	DocumentReference salesDocRef;
	SalesOrder salesOrder,salesToViewOrUpdate;
	boolean monitorChangeFlg = false;
	ArrayAdapter<SalesPerson> salesPersonAdapter;
	//SalesCashDialogFragment mCashDialog = new SalesCashDialogFragment();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_order);
		ButterKnife.bind(this);
		listDetails = findViewById(R.id.list_details);
		daoSession = ((BaseApplication) getApplication()).getDaoInstance();
		productDao = daoSession.getProductDao();
        salesPersonDao = daoSession.getSalesPersonDao();
		loadProductsSpinner(productDao.loadAll());
		loadSalesPersonSpinner(salesPersonDao.loadAll());
		setClickEventListener();

		salesDetailArrayAdapter = new SalesOrderAdapter(productsDetailList, true);
		//productsDetailList.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		listDetails.setLayoutManager(mLayoutManager);
		listDetails.setAdapter(salesDetailArrayAdapter);
		listDetails.addOnItemTouchListener(new RecyclerTouchListener(this,
				listDetails, new ClickListener() {
			@Override
			public void onClick(View view, final int position) {
				//Values are passing to activity & to fragment as well
				showOptions(position);
				Toast.makeText(SalesOrderActivity.this, "Single Click on position        :"+position,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLongClick(View view, int position) {
				Toast.makeText(SalesOrderActivity.this, "Long press on position :"+position,
						Toast.LENGTH_LONG).show();
			}
		}));
		handleIntent(getIntent());
	}
	private void handleIntent(Intent intent) {
		createNew = intent.getBooleanExtra("create",false);

		//// This means we are editing a grocery item
		if(!createNew){
			String salesIdStrValue = (String)intent.getSerializableExtra("salesId");
			if(null!=salesIdStrValue && !salesIdStrValue.isEmpty()){
				Long salesIdValue = Long.valueOf(salesIdStrValue);
				String salesDocIdValue = (String) intent.getSerializableExtra("salesDocId");
				initOrLoadSalesItem(false,salesIdValue,salesDocIdValue);
			}
			else{
				initOrLoadSalesItem(true,null,null);
			}
		}
		else{
			initOrLoadSalesItem(true,null,null);
		}
	}
	private void initOrLoadSalesItem(boolean newFlg , Long id, String docId){
		clearFields();
		final String TAG = "initOrLoadSalesItem";
		if(null!=docId && !docId.isEmpty()){
			confirmSalesOrder.setText("Update Order");
			final DocumentReference docRef = FirestoreUtil.getSalesCollectionRef().document(docId);
			docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<DocumentSnapshot> task) {
					if (task.isSuccessful()) {
						DocumentSnapshot document = task.getResult();
						if (document.exists()) {
							salesDocRef =docRef;
							SalesOrder salesOrder = document.toObject(SalesOrder.class);
							salesToViewOrUpdate = salesOrder;
							//deleteSales(aSale);
							//salesDao.insert(aSale);
							String date = (null!=salesOrder.getUpdatedOn()?simpleDateFormat.format(salesOrder.getUpdatedOn()):"");
							salesDate.setText(date);
							salesId.setText(salesOrder.getId().toString());
							selectSalesPerson(salesOrder.getSalesPersonId());
							customerName.setText(salesOrder.getCustomerName());
							mobile.setText(salesOrder.getCustomerPhone());
							productsDetailList.addAll(salesOrder.getProductList());
							salesDetailArrayAdapter.notifyDataSetChanged();
							calculateTotal();
							//fetchSalesList(false, aSale.getId(), aSale.getSalesDetails());
							//fetchSalesList(true, aSale.getId(),null);
							Log.d(TAG, "DocumentSnapshot data: " + document.getData());
						} else {
							Log.d(TAG, "No such document");
						}
					} else {
						Log.d(TAG, "get failed with ", task.getException());
					}
				}
			});
		}
		else{
			String date = simpleDateFormat.format(new Date());
			salesDate.setText(date);
		}

		//finish();


	}
	public void loadSalesPersonSpinner(List<SalesPerson> personsList){
        if(personsList.size()==0){
            FirestoreUtil util=new FirestoreUtil();
            util.getSalesPersonList(this,null);
        }
        else{
            initSalesPersonSpinner(personsList);
        }

    }
	public void loadProductsSpinner(List<Product> productList){
		if(productList.size()==0){
			FirestoreUtil util=new FirestoreUtil();
			util.getProducts(this,null, true);
		}
		else{
			initProductsSpinner(productList);
		}

	}
    private void initSalesPersonSpinner(List<SalesPerson> personsList){
        SalesPerson defaultProduct=new SalesPerson();
        defaultProduct.setSalesPersonId("Select Sales Person");
        personsList.add(0,defaultProduct);

		salesPersonAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, personsList);
		salesPersonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesPersons.setAdapter(salesPersonAdapter);

    }
    public void loadSalesPersons(List<SalesPerson> aList){
        salesPersonDao.deleteAll();
        for(SalesPerson aProduct:aList){
            aProduct.setId(null);
            salesPersonDao.insert(aProduct);
        }
        initSalesPersonSpinner(aList);
        Toast.makeText(SalesOrderActivity.this, "Products Synchronized Successfully!",
                Toast.LENGTH_SHORT).show();

    }

	private void initProductsSpinner(List<Product> productList){
		Product defaultProduct=new Product();
		defaultProduct.setProductName("Select Product");
		defaultProduct.setManufacturer("");
		productList.add(0,defaultProduct);

		ArrayAdapter<Product> dataAdapter = new ArrayAdapter<Product>(this,android.R.layout.simple_spinner_item, productList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		products.setAdapter(dataAdapter);

	}
	public void loadProducts(List<Product> productList){
		productDao.deleteAll();
		for(Product aProduct:productList){
			aProduct.setId(null);
			productDao.insert(aProduct);
		}
		initProductsSpinner(productList);
		Toast.makeText(SalesOrderActivity.this, "Products Synchronized Successfully!",
				Toast.LENGTH_SHORT).show();
	}

    private void insertSalesDetailItem(){
        if(validateSalesDetails()) {
			Float userQuantity = new Float(quantity.getText().toString());
            Product product1 = new Product();
            Product product = (Product) products.getSelectedItem();
            product1.setProductDocId(product.getProductDocId());
            product1.setProductName(product.getProductName());
			product1.setProductId(product.getProductId());
			product1.setManufacturer(product.getManufacturer());

			Double total =  product.getRetailSalePrice() * userQuantity;
			Double roundedPrice = AppUtil.round(total,3);

			product1.setRetailSalePrice(roundedPrice);
			product1.setRetailSaleType(product.getRetailSaleType());
			product1.setQuantity(userQuantity);
			boolean existFlg = false;
			int i=0;
            for (Product aDetail : productsDetailList) {
                if (product.getProductDocId().equalsIgnoreCase(aDetail.getProductDocId())) {
                    showOptions(product1, i);
                    existFlg = true;
                }
                i++;
            }
            if (!existFlg) {
				productsDetailList.add(product1);
				salesDetailArrayAdapter.notifyDataSetChanged();
                monitorChangeFlg = true;
                calculateTotal();
                Toast.makeText(this, "Sales Detail Inserted", Toast.LENGTH_SHORT).show();
            }
        }
        clearFields();

    }
    private void clearFields(){
        products.setSelection(0);
        quantity.setText("");
        productType.setSelection(0);
        price.setText("");
    }
	private void calculateTotal(){
		Double total=0D;
		for (Product aDetail : productsDetailList) {
			Float userQuantity = aDetail.getQuantity();
			Double productPrice = aDetail.getRetailSalePrice() * userQuantity;
			Double roundedPrice = AppUtil.round(productPrice,3);
			total = total+roundedPrice;
		}
		orderTotal.setText(total.toString());
	}
	private boolean validateSales(){
		boolean flg=true;
		if(salesPersons.getSelectedItemId()==0){
			flg=false;
			Toast.makeText(this, "Please select sales person!", Toast.LENGTH_SHORT).show();
		}
		/*if(customerLocation.getText().toString().isEmpty()){
			flg=false;
			Toast.makeText(this, "Customer Location can not be empty!", Toast.LENGTH_SHORT).show();
		}*/
		if(productsDetailList.size()==0){
			flg=false;
			Toast.makeText(this, "Sales Detail can not be empty!", Toast.LENGTH_SHORT).show();
		}
		if(!flg){
			confirmSalesOrder.setEnabled(true);
		}
		return flg;
	}
    private boolean validateSalesDetails(){
        boolean flg=true;
        if(quantity.getText().toString().isEmpty()){
            flg=false;
            Toast.makeText(this, "Quantity can not be empty!", Toast.LENGTH_SHORT).show();
        }
        if(products.getSelectedItemPosition()==0){
            flg=false;
            Toast.makeText(this, "Please select product!", Toast.LENGTH_SHORT).show();
        }
        return flg;
    }
	private void showOptions(final Product product,final int position) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		String[] options ;
		options = new String[2];

		options[0] = "Yes";
		options[1] = "Cancel";
		alertDialogBuilder.setTitle("Product Already Available, do you want to update?");
		alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					productsDetailList.set(position, product);
					monitorChangeFlg = true;
					salesDetailArrayAdapter.notifyDataSetChanged();
					calculateTotal();
					Toast.makeText(SalesOrderActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();
				}
			}
		});
		alertDialogBuilder.create().show();
	}

	private void showOptions(final int position) {
		final Product selectedSalesDetailItem = productsDetailList.get(position);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		String[] options = new String[1];

		options[0] = "Delete " + selectedSalesDetailItem.getProductName();

		alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					monitorChangeFlg = true;
					productsDetailList.remove(position);
					salesDetailArrayAdapter.notifyDataSetChanged();
					calculateTotal();
					Toast.makeText(SalesOrderActivity.this, "Detail has been removed!", Toast.LENGTH_SHORT).show();
				}

			}
		});
		alertDialogBuilder.create().show();
	}
    private void setClickEventListener() {
		products.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(products.getSelectedItemId()>0){
					Product aProduct= (Product) products.getSelectedItem();
					mrp.setText(aProduct.getRetailSalePrice().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
        addSalesDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSalesDetailItem();
            }
        });
		quantity.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			public void onTextChanged(CharSequence cs, int start, int before, int count) {
				if(null!=cs){
					try{
						Float number = Float.parseFloat(cs.toString());

							Product aProduct= (Product) products.getSelectedItem();
						if(null!=number && null!=aProduct && null!=aProduct.getRetailSalePrice()){
							Double total =  number *aProduct.getRetailSalePrice();
							Double roundedPrice = AppUtil.round(total,3);
							price.setText(roundedPrice.toString());
						}

					}
					catch(NumberFormatException ex){
						price.setText("");
					}

				}

			}
		});
	}
	@OnClick(R.id.btn_cancel)
	public void cancel() {
        String user= AppUtil.getFromLoginPref(this);
        if(user.equals("SALES")) {
            Intent intent = new Intent(this, SalesOrderLandActivity.class);
            this.startActivity(intent);
            finish();
        }
        if(user.equals("ADMIN")) {
            Intent intent = new Intent(this, SalesCallbackOrderDisplayActivity.class);
            this.startActivity(intent);
            finish();
        }
	}
	@OnClick(R.id.btn_confirm_save)
	public void generateOrder() {
		final String TAG = "generateOrder";
		confirmSalesOrder.setEnabled(false);
		if(!AppUtil.isNullOrEmpty(salesId.getText().toString())) {
			if(validateSales()){
				monitorChangeFlg = false;
				salesToViewOrUpdate.setSalesPersonId(((SalesPerson)salesPersons.getSelectedItem()).getSalesPersonId());
				salesToViewOrUpdate.setCustomerName(customerName.getText().toString());
				salesToViewOrUpdate.setCustomerPhone(customerName.getText().toString());
				salesToViewOrUpdate.setSync(true);
				salesToViewOrUpdate.setProductList(productsDetailList);
				salesToViewOrUpdate.setUpdatedOn(new Date());
				calculateTotal();
				Double salesAmount = Double.valueOf(orderTotal.getText().toString());

				salesToViewOrUpdate.setTotal(salesAmount);
				salesDocRef.set(salesToViewOrUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						Log.d(TAG, "Sales Updated Successfully!");
						confirmSalesOrder.setEnabled(true);
						Toast.makeText(SalesOrderActivity.this, "Sales Order Updated Successfully!",
								Toast.LENGTH_SHORT).show();
						String user= AppUtil.getFromLoginPref(SalesOrderActivity.this);
						if("ADMIN".equals(user)){
							Intent intent = new Intent(SalesOrderActivity.this, SalesCallbackOrderDisplayActivity.class);
							startActivity(intent);
							finish();
						}
						else if("SALES".equals(user)){
							Intent intent = new Intent(SalesOrderActivity.this, SalesOrderLandActivity.class);
							startActivity(intent);
							finish();
						}

					}
				})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.w(TAG, "Sales Order has not updated.", e);
								confirmSalesOrder.setEnabled(true);
								Toast.makeText(SalesOrderActivity.this, "Sales has not updated.",
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		}else{

			if(validateSales()){
				monitorChangeFlg = false;
				final SalesOrder salesOrder = new SalesOrder();
				salesOrder.setSalesPersonId(((SalesPerson)salesPersons.getSelectedItem()).getSalesPersonId());
				salesOrder.setCustomerName(customerName.getText().toString());
				salesOrder.setCustomerPhone(mobile.getText().toString());
				salesOrder.setSync(true);
				salesOrder.setProductList(productsDetailList);
				salesOrder.setUpdatedOn(new Date());
				calculateTotal();
				Double salesAmount = Double.valueOf(orderTotal.getText().toString());

				salesOrder.setTotal(salesAmount);
				if(null==salesOrder.getId()){
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

							createSalesOrder(newCounter, salesOrder);
							// Success
							return null;
						}
					}).addOnSuccessListener(new OnSuccessListener<Void>() {
						@Override
						public void onSuccess(Void aVoid) {
							Log.d(TAG, "Counter Incremented successfully!");
							confirmSalesOrder.setEnabled(true);

						}
					})
							.addOnFailureListener(new OnFailureListener() {
								@Override
								public void onFailure(@NonNull Exception e) {
									Log.w(TAG, "Transaction failure.", e);
									confirmSalesOrder.setEnabled(true);
								}
							});

				}
			}
		}
	}
	private void createSalesOrder(final Long newCounter,final SalesOrder updatedSales){
		final String TAG = "updateSales";
		updatedSales.setId(newCounter);
		FirestoreUtil.getSalesCollectionRef()
				.add(updatedSales)
				.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
					@Override
					public void onSuccess(DocumentReference documentReference) {

						salesId.setText(newCounter.toString());
						salesDocRef =documentReference;
						documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
							@Override
							public void onComplete(@NonNull Task<DocumentSnapshot> task) {
								if (task.isSuccessful()) {
									DocumentSnapshot document = task.getResult();
									if (document.exists()) {
										//salesDocRef =documentReference;
										SalesOrder aSales = document.toObject(SalesOrder.class);
										salesToViewOrUpdate = aSales;
										confirmSalesOrder.setText("Update Order");
									} else {
										Log.d(TAG, "No such document");
									}
								} else {
									Log.d(TAG, "get failed with ", task.getException());
								}
							}
						});
						Toast.makeText(SalesOrderActivity.this, "Updated with reference ID: "+documentReference.getId(),
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

	private void selectSalesPerson(String personId){
		for(int i = 1; i < salesPersonAdapter.getCount(); i++){
			SalesPerson aPerson = salesPersonAdapter.getItem(i);
			if(aPerson.getSalesPersonId().equals(personId)){
				salesPersons.setSelection(i);
			}
		}
	}
	public static interface ClickListener{
		public void onClick(View view,int position);
		public void onLongClick(View view,int position);
	}

	class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

		private ClickListener clicklistener;
		private GestureDetector gestureDetector;

		public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

			this.clicklistener=clicklistener;
			gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return true;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					View child=recycleView.findChildViewUnder(e.getX(),e.getY());
					if(child!=null && clicklistener!=null){
						clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
					}
				}
			});
		}

		@Override
		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
			View child=rv.findChildViewUnder(e.getX(),e.getY());
			if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
				clicklistener.onClick(child,rv.getChildAdapterPosition(child));
			}

			return false;
		}

		@Override
		public void onTouchEvent(RecyclerView rv, MotionEvent e) {

		}

		@Override
		public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

		}
	}
}
