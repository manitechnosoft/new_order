package com.mobile.order.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.adapter.FirestoreProducts;
import com.mobile.order.adapter.ProductListAdapter;
import com.mobile.order.adapter.SalesOrderAdapter;
import com.mobile.order.filter.MoneyValueFilter;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.listener.RecyclerTouchListener;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.Product;
import com.mobile.order.model.ProductDao;
import com.mobile.order.model.SalesOrder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSalesProductFragment extends Fragment implements FirestoreProducts {
  DaoSession daoSession;
  ProductDao productDao;
  List<Product> productsList;
  ArrayAdapter<String> itemsAdapter;
  private View mViewHolder;
 /* @BindView(R.id.enter_product)
  EditText enterProduct;*/
  @BindView(R.id.mrp)
  EditText mrp;

  @BindView(R.id.quantity)
  EditText quantity;

  @BindView(R.id.price)
  EditText price;

  @BindView(R.id.type)
  Spinner productType;

    @BindView(R.id.add_sales_product)
    Button addProduct;

  @BindView(R.id.product_autoComplete)
  AutoCompleteTextView productAutoComplete;

  Button nextButton;
  List<Product> productsDetailList =new ArrayList<>();
  private RecyclerView.Adapter salesDetailArrayAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  @BindView(R.id.list_details)
  RecyclerView listDetails;
  SalesOrder newOrder = null;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
    Bundle bund = getArguments();
    if(null!=bund && bund.containsKey("newOrder")){
      newOrder = (SalesOrder)getArguments().getSerializable("newOrder");
    }
    // Inflate the xml file for the fragment
    if (mViewHolder == null) {
      mViewHolder = inflater.inflate(R.layout.fragment_addsales_product, parent, false);
      ButterKnife.bind(this, mViewHolder);
      price.setEnabled(false);
      daoSession = ((BaseApplication) getActivity().getApplication()).getDaoInstance();
      productDao = daoSession.getProductDao();
      productsList = productDao.loadAll();
      if(productsList.size()==0){
        FirestoreUtil util=new FirestoreUtil();
        util.getProducts(getContext(),AddSalesProductFragment.this, true);
      }
      Activity currentActivity = getActivity();
      nextButton = currentActivity.findViewById(R.id.next_button);
      nextButton.setText("Next  ");
      initializeCustomerAutoComplete();
//quantity.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7, 2)});
    }
    return mViewHolder;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    productsList = productDao.loadAll();
    if(productsList.size()==0){
      FirestoreUtil util=new FirestoreUtil();
      util.getProducts(getContext(),AddSalesProductFragment.this, true);
    }
    /*enterProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          Product selectedProduct = getProduct(enterProduct.getText().toString());
          if(null!=selectedProduct){
            mrp.setText(selectedProduct.getRetailSalePrice().toString());
          }
          Toast.makeText(getActivity(), "Finding product info for id "+enterProduct.getText().toString()+".",
                  Toast.LENGTH_SHORT).show();
        }
      }
    });*/

    addProduct.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              insertSalesDetailItem();
          }
      });
    //quantity.addTextChangedListener(new DecimalFilter(quantity, getActivity(),8,2));
    quantity.setKeyListener(new MoneyValueFilter());
    mrp.setKeyListener(new MoneyValueFilter());
    quantity.addTextChangedListener(new TextWatcher() {
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String strUserQuantity = s.toString();
        if(!strUserQuantity.isEmpty() && !mrp.getText().toString().isEmpty()){
          Double lQuantity = Double.valueOf(strUserQuantity);
          Double salesPrice = Double.valueOf(mrp.getText().toString());
          Double total =  salesPrice * lQuantity;
          Double roundedPrice = AppUtil.round(total,3);
          price.setText(roundedPrice.toString());
        }
      }
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      public void afterTextChanged(Editable s) {
      }
    } );
    salesDetailArrayAdapter = new SalesOrderAdapter(productsDetailList, true);
    //productsDetailList.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(getContext());
    listDetails.setLayoutManager(mLayoutManager);
    listDetails.setAdapter(salesDetailArrayAdapter);
    listDetails.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
            listDetails, new RecyclerTouchListener.ClickListener() {
      @Override
      public void onClick(View view, final int position) {
        //Values are passing to activity & to fragment as well
        showOptions(position);
        Toast.makeText(getActivity(), "Single Click on position        :"+position,
                Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onLongClick(View view, int position) {
        Toast.makeText(getActivity(), "Long press on position :"+position,
                Toast.LENGTH_LONG).show();
      }
    }));
      displaySelectedProducts();
  }
  private void displaySelectedProducts(){
    if(null!=newOrder){
      productsDetailList.clear();
      productsDetailList.addAll(newOrder.getProductList());
      salesDetailArrayAdapter.notifyDataSetChanged();
    }
  }
private Product getProduct(String productCode){
    //if(productCode.length()==6) {
      for (Product aProduct : productsList) {
        if (productCode.equals(aProduct.getProductId())) {
          return aProduct;
        }
      }
    /*}
    else{
      Toast.makeText(getActivity(), "Entered wrong product code!.",
              Toast.LENGTH_SHORT).show();
  }*/
  return null;
}
  public void loadProducts(List<Product> productList){
    productDao.deleteAll();
    for(Product aProduct:productList){
      aProduct.setId(null);
      productDao.insert(aProduct);
    }
    Toast.makeText(getContext(), "Products Synchronized Successfully!",
            Toast.LENGTH_SHORT).show();
  }

  //--OnItemSelectedListener listener;
  // This event fires 1st, before creation of fragment or any views
  // The onAttach method is called when the Fragment instance is associated with an Activity.
  // This does not mean the Activity is fully initialized.
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

  }


  // Define the events that the fragment will use to communicate
  public interface OnItemSelectedListener {
    // This can be any number of events to be sent to the activity
    void onPizzaItemSelected(int position);
  }
  private void insertSalesDetailItem(){
      AppUtil.hideKeyboard(getActivity(),quantity);
    if(validateSalesDetails()) {
      Float userQuantity = new Float(quantity.getText().toString());
      Product product1 = new Product();
      Product storedProduct =  getProduct(productAutoComplete.getText().toString());
      if(null!=storedProduct){
        product1.setProductDocId(storedProduct.getProductDocId());
        product1.setProductName(storedProduct.getProductName());
        product1.setProductId(storedProduct.getProductId());
        product1.setManufacturer(storedProduct.getManufacturer());

        Double total =  storedProduct.getRetailSalePrice() * userQuantity;
        Double roundedPrice = AppUtil.round(total,3);

        product1.setRetailSalePrice(roundedPrice);
        product1.setRetailSaleType(storedProduct.getRetailSaleType());
      }
      product1.setQuantity(userQuantity);
      boolean existFlg = false;
      int i=0;
      for (Product aDetail : productsDetailList) {
        if (storedProduct.getProductDocId().equalsIgnoreCase(aDetail.getProductDocId())) {
          showOptions(product1, i);
          existFlg = true;
        }
        i++;
      }
      if (!existFlg) {
        productsDetailList.add(product1);
        salesDetailArrayAdapter.notifyDataSetChanged();
       // monitorChangeFlg = true;
       // calculateTotal();
      }
    }
    clearFields();

  }
  private void showOptions(final Product product,final int position) {

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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
          //monitorChangeFlg = true;
          salesDetailArrayAdapter.notifyDataSetChanged();
         // calculateTotal();
          Toast.makeText(getContext(), "Details Updated", Toast.LENGTH_SHORT).show();
        }
      }
    });
    alertDialogBuilder.create().show();
  }

  private void showOptions(final int position) {
    final Product selectedSalesDetailItem = productsDetailList.get(position);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

    String[] options = new String[1];

    options[0] = "Delete " + selectedSalesDetailItem.getProductName();

    alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(which == 0){
         // monitorChangeFlg = true;
          productsDetailList.remove(position);
          salesDetailArrayAdapter.notifyDataSetChanged();
         // calculateTotal();
          Toast.makeText(getActivity(), "Detail has been removed!", Toast.LENGTH_SHORT).show();
        }

      }
    });
    alertDialogBuilder.create().show();
  }
  private void clearFields(){
   // products.setSelection(0);
    quantity.setText("");
    //enterProduct.setText("");
    productAutoComplete.setText("");
    productType.setSelection(0);
    price.setText("");
    mrp.setText("");
  }

  private boolean validateSalesDetails(){
    boolean flg=true;
    String toastMsg = "";
    if(quantity.getText().toString().isEmpty()){
      flg=false;
      toastMsg = toastMsg+ "\nQuantity can not be empty!";
    }
   /* if(enterProduct.getText().toString().isEmpty()){
      flg=false;
      toastMsg = toastMsg+ "\nProduct code can not be empty!";
    }
    if(!enterProduct.getText().toString().isEmpty() && enterProduct.getText().toString().length()==6){
      flg=false;
      toastMsg = toastMsg+ "\nEntered wrong product code!";
    }*/
    if(mrp.getText().toString().isEmpty()){
      flg=false;
      toastMsg = toastMsg+ "\nMRP can not be empty!";
    }
    if(productAutoComplete.getText().toString().isEmpty()){
      flg=false;
      toastMsg = toastMsg+ "\nProduct code can not be empty!";
    }
    if(!productAutoComplete.getText().toString().isEmpty() && productAutoComplete.getText().toString().length()!=6){
      flg=false;
      toastMsg = toastMsg+ "\nEntered wrong product code!";
    }
    if(!flg)
    {
      Toast.makeText(getActivity(), toastMsg,
              Toast.LENGTH_SHORT).show();
    }
    return flg;
  }
  public List<Product> getProductsDetailList(){
    return productsDetailList;
  }
  private void initializeCustomerAutoComplete(){
    productAutoComplete.setThreshold(2);//will start working from first character
    ProductListAdapter contactsListAdapter =new ProductListAdapter(getContext(),productsList,new ProductListAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(Product aContact) {
        Toast.makeText(getContext(), aContact.getProductId()+" Clicked", Toast.LENGTH_LONG).show();
      }
    });
    productAutoComplete.setAdapter(contactsListAdapter);//setting the adapter data into the AutoCompleteTextView
    productAutoComplete.setTextColor(Color.RED);

    productAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), productsList.get(position).getProductId()+" Clicked", Toast.LENGTH_LONG).show();
        productAutoComplete.setText(productsList.get(position).getProductId());
        mrp.setText(productsList.get(position).getRetailSalePrice().toString());
      }
    });
  }
}
