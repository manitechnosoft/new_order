package com.mobile.order.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.R;
import com.mobile.order.fragment.AddSalesProductFragment;
import com.mobile.order.fragment.CustomerInfoFragment;
import com.mobile.order.fragment.ReprintFragment;
import com.mobile.order.fragment.SalesIdFragment;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.Product;
import com.mobile.order.model.SalesOrder;
import com.mobile.order.model.SalesPerson;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SalesOrderLandActivity extends AppCompatActivity {
    @BindView(R.id.prev_button)
    Button prevButton;

    @BindView(R.id.next_button)
    Button nextButton;

    @BindView(R.id.flContainer)
    FrameLayout container;

    //@BindView(R.id.print_button)
    //Button printButton;
    private String SALED_ID_TAG = "SALESID";
    private String REPRINT_TAG = "REPRINT";
    private String PRODUCTS_TAG = "PRODUCTS";
    private String CUSTOMER_INFO_TAG = "CUSTOMERINFO";
    SalesOrder newOrder= new SalesOrder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        ButterKnife.bind(this);

        Log.d("DEBUG", getResources().getConfiguration().orientation + "");

        if (savedInstanceState == null) {
            // Instance of first fragment
            SalesIdFragment firstFragment = new SalesIdFragment();

            // Add Fragment to FrameLayout (flContainer), using FragmentManager
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();// begin  FragmentTransaction
            ft.replace(R.id.flContainer, firstFragment,SALED_ID_TAG);                                // add    Fragment
            ft.commit();                                                            // commit FragmentTransaction
        }

   /* if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
      PizzaDetailFragment secondFragment = new PizzaDetailFragment();
      Bundle args = new Bundle();
      args.putInt("position", 0);
      secondFragment.setArguments(args);          // (1) Communicate with Fragment using Bundle
      FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();// begin  FragmentTransaction
      ft2.add(R.id.flContainer2, secondFragment);                               // add    Fragment
      ft2.commit();                                                            // commit FragmentTransaction
    }*/
    }
    public void onAttachFragment(Fragment fragment) {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        // printButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
        prevButton.setVisibility(View.VISIBLE);
        if (fragment instanceof SalesIdFragment) {
            prevButton.setEnabled(false);
            prevButton.setVisibility(View.GONE);
            //headlinesFragment.setOnHeadlineSelectedListener(this);
        }
        if (fragment instanceof CustomerInfoFragment) {
            nextButton.setEnabled(false);
            //nextButton.setVisibility(View.GONE);
            //printButton.setVisibility(View.VISIBLE);
            //headlinesFragment.setOnHeadlineSelectedListener(this);
        }
    }

    @OnClick({R.id.next_button})
    public void nextClick(){
        //AddSalesProductFragment secondFragment = new AddSalesProductFragment();
        FragmentManager frgMgr =  getSupportFragmentManager();
        Fragment currentFragment = frgMgr.findFragmentById(R.id.flContainer);

        Fragment nextFragment = null;
        String tag = "";
        Bundle bundle = new Bundle();
        int entry = frgMgr.getBackStackEntryCount();
        if (currentFragment instanceof SalesIdFragment) {
            SalesIdFragment headlinesFragment = (SalesIdFragment) currentFragment;
            tag = SALED_ID_TAG;
            Spinner salesId = headlinesFragment.getView().findViewById(R.id.sales_person);
            if(salesId.getSelectedItemId()>0)
            {
                SalesPerson selectedSalesPerson = (SalesPerson)salesId.getSelectedItem();
                newOrder.setSalesPersonId(selectedSalesPerson.getSalesPersonId());
            }
            else{
                Toast.makeText(getApplicationContext(), "Please Select Your ID!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Fragment productsFragment = frgMgr.findFragmentByTag(REPRINT_TAG);
            if(null!=productsFragment){
                nextFragment =  productsFragment;
            }
            else{
                nextFragment = new ReprintFragment();
            }
            nextFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
        }
        if (currentFragment instanceof ReprintFragment) {
            ReprintFragment rePrintFragment = (ReprintFragment) currentFragment;
            tag = REPRINT_TAG;

            Fragment productsFragment = frgMgr.findFragmentByTag(PRODUCTS_TAG);
            if(null!=productsFragment){
                nextFragment =  productsFragment;
            }
            else{
                nextFragment = new AddSalesProductFragment();
            }
            nextFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
        }
        else if (currentFragment instanceof AddSalesProductFragment) {
            AddSalesProductFragment salesProductFragment = (AddSalesProductFragment) currentFragment;
            tag = PRODUCTS_TAG;
            List<Product> addedProducts =  salesProductFragment.getProductsDetailList();
            if(addedProducts.size()==0){
                Toast.makeText(getApplicationContext(), "Please Add Products!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                newOrder.getProductList().clear();
                newOrder.getProductList().addAll(addedProducts);
            }
            Fragment customerFragment = frgMgr.findFragmentByTag(CUSTOMER_INFO_TAG);
            if(null!=customerFragment){
                nextFragment =  customerFragment;
            }
            else{
                nextFragment = new CustomerInfoFragment();
            }
            nextFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
        }
        else if (currentFragment instanceof CustomerInfoFragment) {
            // nextFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
            CustomerInfoFragment customerInfo = (CustomerInfoFragment) currentFragment;
            tag = CUSTOMER_INFO_TAG;
            EditText mobile = customerInfo.getView().findViewById(R.id.mobile);
            if(!mobile.getText().toString().isEmpty() && mobile.getText().toString().length()<10){
                Toast.makeText(getApplicationContext(), "Please enter 10 digit mobile number!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String customerNo=mobile.getText().toString();
            newOrder.setCustomerPhone(null==customerNo || customerNo.isEmpty()?"":customerNo);
            //Create new sales order before print with product details, sales person id and customer mobile.
            generateOrder(newOrder);
            return;
        }
        frgMgr.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        frgMgr.beginTransaction()
                .replace(R.id.flContainer, nextFragment)
                .disallowAddToBackStack()
                //.addToBackStack(tag)// replace flContainer
                .commit();
        //disallowAddToBackStack() will avoid back press
    }
    public void wireMethodInReprintFragment(){

    }
    @OnClick(R.id.prev_button)
    public void previousClick(){
        Bundle bundle = new Bundle();
        //AddSalesProductFragment secondFragment = new AddSalesProductFragment();
        FragmentManager frgMgr =  getSupportFragmentManager();
        Fragment currentFragment = frgMgr.findFragmentById(R.id.flContainer);

        Fragment prevFragment = null;
        String tag = "";
        if (currentFragment instanceof SalesIdFragment) {
            // SalesIdFragment headlinesFragment = (SalesIdFragment) fragment;
            tag = SALED_ID_TAG;
            return;
        }
        if (currentFragment instanceof ReprintFragment) {
            // SalesIdFragment headlinesFragment = (SalesIdFragment) fragment;
            tag = REPRINT_TAG;
            ReprintFragment reprintFragment = (ReprintFragment) currentFragment;
            Fragment salesIdFragment = frgMgr.findFragmentByTag(SALED_ID_TAG);
            tag = PRODUCTS_TAG;
            if(null!=salesIdFragment){
                prevFragment =  salesIdFragment;
            }
            else{
                prevFragment = new SalesIdFragment();
            }
            prevFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
        }
        else if (currentFragment instanceof AddSalesProductFragment) {
            AddSalesProductFragment salesProductFragment = (AddSalesProductFragment) currentFragment;
            Fragment reprintFrag = frgMgr.findFragmentByTag(REPRINT_TAG);
            tag = PRODUCTS_TAG;
            if(null!=reprintFrag){
                prevFragment =  reprintFrag;
            }
            else{
                prevFragment = new ReprintFragment();
            }
            List<Product> addedProducts =  salesProductFragment.getProductsDetailList();
            if(addedProducts.size()>0){
                newOrder.getProductList().clear();
                newOrder.getProductList().addAll(addedProducts);
            }
            prevFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
        }
        else if (currentFragment instanceof CustomerInfoFragment) {
            CustomerInfoFragment customerInfo = (CustomerInfoFragment) currentFragment;
            tag = CUSTOMER_INFO_TAG;
            EditText custMobile = customerInfo.getView().findViewById(R.id.mobile);
            Fragment productsFragment = frgMgr.findFragmentByTag(PRODUCTS_TAG);
            if(null!=productsFragment){
                prevFragment =  productsFragment;
            }
            else{
                prevFragment = new AddSalesProductFragment();
            }
            if(!custMobile.getText().toString().isEmpty()){
                newOrder.setCustomerPhone(custMobile.getText().toString());
            }
            prevFragment.setArguments(bundle);
            bundle.putSerializable("newOrder",newOrder);
        }
        frgMgr.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        frgMgr.beginTransaction()
                .replace(R.id.flContainer, prevFragment) // replace flContainer
                .disallowAddToBackStack()
                //.addToBackStack(tag)// replace flContainer
                .commit();

    }
    private void generateOrder(SalesOrder salesOrder){
        salesOrder.setSync(true);
        salesOrder.setUpdatedOn(new Date());
        Double total =  calculateTotal(salesOrder.getProductList());
        salesOrder.setTotal(total);
        if(null==salesOrder.getId()){
            FirestoreUtil.incrementSalesOrderCounterAndCreateOrder(getApplicationContext(), salesOrder);
        }
        AlertDialog alert = prepareDialog();
        alert.show();
    }
    private Double calculateTotal(List<Product> productList){
        Double total=0D;
        for (Product aDetail : productList) {
            total = total+aDetail.getRetailSalePrice();
        }
        return  AppUtil.round(total,3);
    }

    private AlertDialog prepareDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Print process initiated!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       /* Intent intent = new Intent(SalesOrderLandActivity.this, SalesOrderLandActivity.class);
                        startActivity(intent);*/
                    }
                });

        return  builder1.create();
    }
}