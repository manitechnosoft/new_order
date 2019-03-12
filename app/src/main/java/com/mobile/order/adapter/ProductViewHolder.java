package com.mobile.order.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobile.order.R;
import com.mobile.order.model.Product;

public class ProductViewHolder extends RecyclerView.ViewHolder {

public TextView productId;
public ProductViewHolder(View itemView) {
        super(itemView);
    productId =  itemView.findViewById(R.id.product_id);
        }

public void bind(final Product item, final ProductListAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        listener.onItemClick(item);
        }
        });
        }
        }