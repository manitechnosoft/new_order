package com.mobile.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mobile.order.R;
import com.mobile.order.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter  extends BaseAdapter implements Filterable {
    Context c;
    List<Product> actualContactsList;
    List<Product> contactsList = new ArrayList<>();
    private OnItemClickListener mListener;

    // View Type for Separators
    private static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    // View Type for Regular rows
    private static final int ITEM_VIEW_TYPE_REGULAR = 1;
    // Types of Views that need to be handled
    // -- Separators and Regular rows --
    private static final int ITEM_VIEW_TYPE_COUNT = 2;

    public ProductListAdapter(Context c, List<Product> contactsList, OnItemClickListener listener) {
        this.c = c;
        this.contactsList = contactsList;
        this.mListener = listener;
        prepareActualContactList(contactsList);
    }

    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.display_product,parent,false);
        ProductViewHolder holder=new ProductViewHolder(v);
        return holder;
    }

    @Override
    public int getCount() {
        if(contactsList==null)
            return 0;
        else
        return contactsList.size();
    }
    private void prepareActualContactList(List<Product> contactsList){
        this.actualContactsList  = new ArrayList<>();
        actualContactsList.addAll(contactsList);

    }
    @Override
    public Object getItem(int position) {
        return contactsList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view;

        Product contact = contactsList.get(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (itemViewType == ITEM_VIEW_TYPE_SEPARATOR) {
                // If its a section ?
                view = inflater.inflate(R.layout.display_product, null);
            }
            else {
                // Regular row
                view = inflater.inflate(R.layout.display_product, null);
            }
        }
        else {
            view = convertView;
        }

        TextView productId = (TextView) view.findViewById(R.id.product_id);

        productId.setText( contact.getProductId() );

        return view;
    }

    @Override
    public Filter getFilter(){
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();

                // If the constraint (search string/pattern) is null
                // or its length is 0, i.e., its empty then
                // we just set the `values` property to the
                // original contacts list which contains all of them
                if (constraint == null || constraint.length() == 0) {
                    results.values = contactsList;
                    results.count = contactsList.size();
                }
                else {
                    // Some search copnstraint has been passed
                    // so let's filter accordingly
                    ArrayList<Product> filteredContacts = new ArrayList<>();
                    contactsList.clear();
                    // We'll go through all the contacts and see
                    // if they contain the supplied string
                    for (Product c : actualContactsList) {
                        if (c.getProductId().toUpperCase().contains( constraint.toString().toUpperCase() )) {
                            // if `contains` == true then add it
                            // to our filtered list
                            contactsList.add(c);
                        }
                    }

                    // Finally set the filtered values and size/count
                    results.values = contactsList;
                    results.count = contactsList.size();
                }

                // Return our FilterResults object
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                contactsList = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(Product aProduct);
    }
}
