package com.mobile.order.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.adapter.FirestoreSalesPersons;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.SalesOrder;
import com.mobile.order.model.SalesPerson;
import com.mobile.order.model.SalesPersonDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */
public class SalesIdFragment extends Fragment implements FirestoreSalesPersons {

  @BindView(R.id.sales_person)
  Spinner salesPersons;

  Button nextButton;

  private View mViewHolder;
  ArrayAdapter<String> itemsAdapter;
  DaoSession daoSession;
  SalesPersonDao salesPersonDao;
  List<SalesPerson> salesPersonList;
  RecyclerView listDetails;
  ArrayAdapter<SalesPerson> salesPersonAdapter;
  SalesOrder newOrder = null;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
    // Inflate the xml file for the fragment
      Bundle bund = getArguments();
      if(null!=bund && bund.containsKey("newOrder")){
          newOrder = (SalesOrder)getArguments().getSerializable("newOrder");
      }

      if (mViewHolder == null) {
      mViewHolder = inflater.inflate(R.layout.fragment_salesid, parent, false);
      ButterKnife.bind(this, mViewHolder);
      daoSession = ((BaseApplication) getActivity().getApplication()).getDaoInstance();
      salesPersonDao = daoSession.getSalesPersonDao();
      salesPersonList = salesPersonDao.loadAll();
      loadSalesPersonSpinner(salesPersonList);

        salesPersons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                enableNext();
            }

            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        Activity currentActivity = getActivity();
        nextButton = currentActivity.findViewById(R.id.next_button);
          nextButton.setText(" Next ");
        enableNext();
      /*  View view = inflater.inflate(R.layout.activity_main, parent, false);
        nextButton = parent.findViewById(R.id.next_button);
        nextButton.setEnabled(false);*/
    }
    return mViewHolder;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
      enableNext();
  }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
  //private OnItemSelectedListener listener;

    @Override
    public void onResume() {
        super.onResume();
        enableNext();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
private void enableNext(){
    if(salesPersons.getSelectedItemId()>0){
        nextButton.setEnabled(true);
    }
    else{
        nextButton.setEnabled(false);
    }
}
  //--OnItemSelectedListener listener;
  // This event fires 1st, before creation of fragment or any views
  // The onAttach method is called when the Fragment instance is associated with an Activity.
  // This does not mean the Activity is fully initialized.
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  /*  if(context instanceof OnItemSelectedListener){      // context instanceof YourActivity
      this.listener = (OnItemSelectedListener) context; // = (YourActivity) context
    } else {
      throw new ClassCastException(context.toString()
        + " must implement PizzaMenuFragment.OnItemSelectedListener");
    }*/
  }

    public void loadSalesPersonSpinner(List<SalesPerson> personsList){
        if(personsList.size()==0){
            FirestoreUtil util=new FirestoreUtil();
            util.getSalesPersonList(getActivity(),SalesIdFragment.this);
        }
        else{
            initSalesPersonSpinner(personsList);
        }

    }
    private void initSalesPersonSpinner(List<SalesPerson> personsList){
        SalesPerson defaultProduct=new SalesPerson();
        defaultProduct.setSalesPersonId("Select Your ID");
        personsList.add(0,defaultProduct);

        salesPersonAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, personsList);
        salesPersonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesPersons.setAdapter(salesPersonAdapter);

        displaySelectedPerson(personsList);
    }
    private void displaySelectedPerson(List<SalesPerson> personsList){
        if(null!=newOrder){
            int i=0;
            for(SalesPerson aPerson: personsList){
                if(aPerson.getSalesPersonId().equals(newOrder.getSalesPersonId())){
                    salesPersons.setSelection(i);
                }
                i++;
            }
        }
    }
    public void loadSalesPersons(List<SalesPerson> aList){
        salesPersonDao.deleteAll();
        for(SalesPerson aProduct:aList){
            aProduct.setId(null);
            salesPersonDao.insert(aProduct);
        }
        initSalesPersonSpinner(aList);
        Toast.makeText(getActivity(), "SalesPersons Synchronized Successfully!",
                Toast.LENGTH_SHORT).show();

    }
  // Define the events that the fragment will use to communicate
  public interface OnItemSelectedListener {
    // This can be any number of events to be sent to the activity
    void onItemSelected(int position);
  }

}
