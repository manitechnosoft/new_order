package com.mobile.order.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.R;
import com.mobile.order.model.Product;
import com.mobile.order.model.SalesPerson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayUpdateSalesPersonAdapter extends RecyclerView.Adapter<DisplayUpdateSalesPersonAdapter.MyViewHolder> {
    private List<SalesPerson> salesPersonsList;
    private Context ctx;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.sales_person_id)
        public EditText salesPersonId;
        @BindView(R.id.first_name)
        public EditText firstName;
        @BindView(R.id.last_name)
        public EditText lastName;


        @BindView(R.id.sales_person_id_error)
        public TextView idError;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DisplayUpdateSalesPersonAdapter(List<SalesPerson> salesPersonList) {
        this.salesPersonsList = salesPersonList;
    }

    // Create new views (invoked by the layout manager)
    public DisplayUpdateSalesPersonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        this.ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DisplayUpdateSalesPersonAdapter.MyViewHolder(inflater.inflate(R.layout.display_update_sales_person_item, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.salesPersonId.setText(salesPersonsList.get(position).getSalesPersonId());
        holder.firstName.setText(salesPersonsList.get(position).getFirstName());
        holder.lastName.setText(salesPersonsList.get(position).getLastName());

        holder.salesPersonId.addTextChangedListener( new TextChangeListener("SALES_PERSON_IS", position));
        holder.firstName.addTextChangedListener( new TextChangeListener("FIRST_NAME", position));
        //holder.retailPrice.addTextChangedListener( new TextChangeListener("RETAIL_PRICE", position));
        /*holder.retailType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                Product aProduct = salesPersonsList.get(position);
                String selectedType = holder.retailType.getSelectedItem().toString();
                aProduct.setRetailSaleType(selectedType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
*/
        if(salesPersonsList.get(position).getSalesPersonId().isEmpty()){
            holder.idError.setVisibility(View.VISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return salesPersonsList.size();
    }
    @Override
    public int getItemViewType(final int position) {
        return position;
    }
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class TextChangeListener implements TextWatcher {
        private int position;
        private String fieldType;
       // private Context ctx;
        TextChangeListener(String fieldName, int position){
            //this.ctx = ctx;
            this.fieldType = fieldName;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            SalesPerson aPerson = salesPersonsList.get(position);
            if (null != charSequence) {
            if("SALES_PERSON_IS".equalsIgnoreCase(this.fieldType)){

                if(charSequence.toString().isEmpty()){
                    Toast productErr = Toast.makeText(ctx,
                            "Sales Person Id can not be empty",
                            Toast.LENGTH_LONG);
                    productErr.show();
                    aPerson.setSalesPersonId(charSequence.toString());
                }
                else{
                    aPerson.setSalesPersonId(charSequence.toString());
                }
            }
                if("FIRST_NAME".equalsIgnoreCase(this.fieldType)){

                    if(charSequence.toString().isEmpty()){
                        Toast productErr = Toast.makeText(ctx,
                                "First name can not be empty",
                                Toast.LENGTH_LONG);
                        productErr.show();
                        aPerson.setFirstName(charSequence.toString());
                    }
                    else{
                        aPerson.setFirstName(charSequence.toString());
                    }
                }

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
