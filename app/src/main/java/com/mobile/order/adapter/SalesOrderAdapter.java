package com.mobile.order.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.R;
import com.mobile.order.activity.SalesOrderActivity;
import com.mobile.order.model.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.MyViewHolder> {
    private List<Product> productList;
    private Context ctx;
private boolean enableDeleteFlg;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_id)
        public TextView productId;

        @BindView(R.id.type)
        public TextView retailType;

        @BindView(R.id.quantity)
        public TextView quantity;

        @BindView(R.id.price)
        public TextView retailPrice;

        @BindView(R.id.delete)
        ImageView delete;

        public View view;
        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SalesOrderAdapter(List<Product> products,boolean enableDeleteFlg) {
        this.productList = products;
        this.enableDeleteFlg = enableDeleteFlg;
    }

    // Create new views (invoked by the layout manager)
    public SalesOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        this.ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new SalesOrderAdapter.MyViewHolder(inflater.inflate(R.layout.display_added_sales_item, parent, false));

    }

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.productId.setText(productList.get(position).getProductId());
        holder.quantity.setText(productList.get(position).getQuantity().toString());
        holder.retailType.setText(productList.get(position).getRetailSaleType());
        holder.retailPrice.setText(null!=productList.get(position).getRetailSalePrice() ?productList.get(position).getRetailSalePrice().toString():"0");
        if(!enableDeleteFlg){
            holder.delete.setVisibility(View.GONE);
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
