/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mobile.order.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.SalesFilter;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog Fragment containing filter form.
 */
public class FilterSalesDialogFragment extends DialogFragment implements FirestoreSalesPersons {

    public static final String TAG = "FilterSalesDialogFragment";


    interface FilterListener {

        void onFilter(SalesFilter filters);

    }

    private View mRootView;

  /*  @BindView(R.id.spinner_sort)
    Spinner mSortSpinner;*/

    @BindView(R.id.spinner_sales_person)
    Spinner salesPerson;

    @BindView(R.id.from_date)
    EditText fromDate;

    @BindView(R.id.to_date)
    EditText toDate;

    DaoSession daoSession;
    SalesPersonDao salesPersonDao;
    List<SalesPerson> salesPersonDaoList = new ArrayList<>();

    List<String> salesPersonList = new ArrayList<>();
    ArrayAdapter<String> customerAdapter;
    private FilterSalesDialogFragment.FilterListener mFilterListener;
    FirestoreUtil util=new FirestoreUtil();
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            return mRootView;
        }

        mRootView = inflater.inflate(R.layout.dialog_sales_filters, container, false);

        ButterKnife.bind(this, mRootView);
        SalesFilter aFilter= new SalesFilter();
        aFilter.setToDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(aFilter.getToDate());
        cal.add(Calendar.DATE, -90);
        aFilter.setFromDate(cal.getTime());
        SalesCallbackOrderDisplayActivity parentActivity = (SalesCallbackOrderDisplayActivity) getActivity();
        daoSession = ((BaseApplication) getActivity().getApplication()).getDaoInstance();
        salesPersonDao = daoSession.getSalesPersonDao();
        salesPersonDaoList = salesPersonDao.loadAll();
        loadSpinnerData();
        loadSalesPersonSpinner(salesPersonDaoList);
         //util.getSalesPersonList(parentActivity,this);


        fromDate.setInputType(InputType.TYPE_NULL);
        fromDate.setOnClickListener(AppUtil.addCalendarDialog(fromDate, getActivity()));

        toDate.setInputType(InputType.TYPE_NULL);
        toDate.setOnClickListener(AppUtil.addCalendarDialog(toDate, getActivity()));

        return mRootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.button_search)
    public void onSearchClicked() {
        SalesFilter aFilter= AppUtil.getSalesFilters(null, salesPerson, fromDate, toDate, null,0);
        SalesCallbackOrderDisplayActivity parentActivity = (SalesCallbackOrderDisplayActivity) getActivity();
        util.getSalesWithFilter(parentActivity,aFilter,false);
        /*if (mFilterListener != null) {
            mFilterListener.onFilter(aFilter);
        }*/

        dismiss();
    }

    @OnClick(R.id.button_clear_filter)
    public void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.button_clear)
    public void onClearClicked() {
        clearFields();
    }
    @OnClick(R.id.close_dialog)
    public void onCloseClicked() {
        dismiss();
    }
    private void clearFields(){
        salesPerson.setSelection(0);
        fromDate.setText("");
        toDate.setText("");
    }
  /*  @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Restaurant.FIELD_AVG_RATING;
        } if (getString(R.string.sort_by_price).equals(selected)) {
            return Restaurant.FIELD_PRICE;
        } if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Restaurant.FIELD_POPULARITY;
        }

        return null;
    }*/

   /* @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        } if (getString(R.string.sort_by_price).equals(selected)) {
            return Query.Direction.ASCENDING;
        } if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }*/

    public void resetFilters() {
         if (mRootView != null) {
        fromDate.setText("");
        toDate.setText("");
        salesPerson.setSelection(0);
        }
    }

    private void loadSpinnerData() {

        customerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, salesPersonList);
        customerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        salesPerson.setAdapter(customerAdapter);


    }
    public void loadSalesPersons(List<SalesPerson> aList){
        List<String> fileteredCustomerList=new ArrayList<>();
        fileteredCustomerList.add("Select Name");
        for(SalesPerson aSale:aList){
            fileteredCustomerList.add(aSale.getSalesPersonId()+" "+aSale.getFirstName());
        }
        salesPersonList.clear();

        salesPersonList.addAll(fileteredCustomerList);
        customerAdapter.notifyDataSetChanged();
    }
    public void loadSalesPersonSpinner(List<SalesPerson> personsList){
        if(personsList.size()==0){
            FirestoreUtil util=new FirestoreUtil();
            util.getSalesPersonList(getActivity(), FilterSalesDialogFragment.this);
        }
        else{
            loadSalesPersons(personsList);
        }

    }
}
