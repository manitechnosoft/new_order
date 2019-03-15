package com.mobile.order.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.R;
import com.mobile.order.activity.DisplayAndUpdateProductActivity;
import com.mobile.order.filter.MoneyValueFilter;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayUpdateProductsAdapter extends RecyclerView.Adapter<DisplayUpdateProductsAdapter.MyViewHolder> {
    private List<Product> productList;
    private DisplayAndUpdateProductActivity displayProductActivity;
    private Context ctx;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.product_doc_id)
        public TextView productDocId;

        @BindView(R.id.product_name)
        public EditText productName;

        @BindView(R.id.product_id)
        public EditText productId;

            @BindView(R.id.retail_type)
        public Spinner retailType;

        @BindView(R.id.retail_price)
        public EditText retailPrice;

        @BindView(R.id.product_name_error)
        public TextView productNameErr;

        @BindView(R.id.delete)
        public ImageView imgDelete;

        ArrayAdapter retailTypeAdapter;
        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            retailTypeAdapter = (ArrayAdapter) retailType.getAdapter();
            imgDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //v.getId() will give you the image id
                    if(displayProductActivity.isEditFlag()){
                        deleteProduct(productId.getText().toString(), productDocId.getText().toString());
                    }
                    else{
                        Toast msg = Toast.makeText(ctx,
                                "Please wait! Existing process in progress!",
                                Toast.LENGTH_LONG);
                        msg.show();
                    }
                }
            });
        }
    }
    private void deleteProduct(final String productId, final String productDocRefId){
        // Pass grocery id to the next screen
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(displayProductActivity);
        String[] options ;
        options = new String[2];
        alertDialogBuilder.setTitle("Do you want to delete "+ productId + " record?");
        options[0] = "Yes ";
        options[1] = "Cancel";
        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    FirestoreUtil.deleteProductInFirebase(displayProductActivity,productDocRefId);
                    displayProductActivity.setEditFlag(false);
                }
            }
        });
        alertDialogBuilder.create().show();

        // fetchSalesList();
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public DisplayUpdateProductsAdapter(DisplayAndUpdateProductActivity paramDisplayProductActivity, List<Product> products) {
        this.productList = products;
        this.displayProductActivity = paramDisplayProductActivity;
    }

    // Create new views (invoked by the layout manager)
    public DisplayUpdateProductsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        this.ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DisplayUpdateProductsAdapter.MyViewHolder(inflater.inflate(R.layout.display_update_products_item, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.productDocId.setText(productList.get(position).getProductDocId());
        holder.productName.setText(productList.get(position).getProductName());
        holder.productId.setText(productList.get(position).getProductId());
        holder.retailPrice.setText(productList.get(position).getRetailSalePrice()!=null?productList.get(position).getRetailSalePrice().toString():"");
        int retailTypeSpinnerPosition = holder.retailTypeAdapter.getPosition(productList.get(position).getRetailSaleType());
        holder.retailType.setSelection(retailTypeSpinnerPosition);

        holder.productName.addTextChangedListener( new TextChangeListener("PRODUCT_NAME", position));
        holder.retailPrice.addTextChangedListener( new TextChangeListener("RETAIL_PRICE", position));
        holder.retailPrice.setKeyListener(new MoneyValueFilter());
        holder.retailType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                Product aProduct = productList.get(position);
                String selectedType = holder.retailType.getSelectedItem().toString();
                aProduct.setRetailSaleType(selectedType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if(productList.get(position).getProductName().isEmpty()){
            holder.productNameErr.setVisibility(View.VISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return productList.size();
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
            Product aProduct = productList.get(position);
            if (null != charSequence) {
            if("PRODUCT_NAME".equalsIgnoreCase(this.fieldType)){

                if(charSequence.toString().isEmpty()){
                    Toast productErr = Toast.makeText(ctx,
                            "Product Name can not be empty",
                            Toast.LENGTH_LONG);
                    productErr.show();
                    aProduct.setProductName(charSequence.toString());
                }
                else{
                    aProduct.setProductName(charSequence.toString());
                }
            }

            if("RETAIL_PRICE".equalsIgnoreCase(this.fieldType)){
                aProduct.setRetailSalePrice(!charSequence.toString().isEmpty()? Double.valueOf(charSequence.toString()):0);
            }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
