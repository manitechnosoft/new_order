package com.mobile.order.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.order.R;
import com.mobile.order.activity.SalesOrderDisplayActivity;
import com.mobile.order.model.SalesOrder;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SalesDetailSubAdapter extends RecyclerView.Adapter<SalesDetailSubAdapter.ViewHolder>  {
    String pattern = "dd-MMM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private List<SalesOrder> salesList;
    private SalesOrderDisplayActivity.SalesItemListener mItemListener;
    private Context context;
    public SalesDetailSubAdapter(List<SalesOrder> salesList, SalesOrderDisplayActivity.SalesItemListener itemListener) {
        setList(salesList);
        mItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_item,parent, false);

        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(v, mItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        SalesOrder aSale = salesList.get(position);

        viewHolder.salesId.setText(null!=aSale.getSalesOrderDocId()? aSale.getId().toString():"");
        viewHolder.salesPerson.setText(aSale.getSalesPersonId());
        viewHolder.customerName.setText(aSale.getCustomerName());
        viewHolder.salesAmount.setText(aSale.getTotal().toString());
        viewHolder.totalItems.setText(aSale.getProductList()!=null ? String.valueOf(aSale.getProductList().size()):"0");
        viewHolder.docId.setText(aSale.getSalesOrderDocId());

       /* SalesDetailSubAdapter childAdapter=new SalesDetailSubAdapter(aSale.getProductList(),mItemListener);
        viewHolder.salesListByMonth.setAdapter(childAdapter);
        viewHolder.salesListByMonth.addOnItemTouchListener(new RecyclerItemListener(context, viewHolder.salesListByMonth,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        CardView cardView=(CardView)v;
                        LinearLayout linearLayout = (LinearLayout)cardView.getChildAt(0);
                        LinearLayout innerlinearLayout = (LinearLayout)linearLayout.getChildAt(0);
                        TextView salesIdElement = (TextView)innerlinearLayout.getChildAt(0);
                        TextView custNameElement = (TextView)linearLayout.getChildAt(1);
                        TextView docIdElement = (TextView)linearLayout.getChildAt(6);
                        showOptions(salesIdElement.getText().toString() , custNameElement.getText().toString() ,docIdElement.getText().toString() );
                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));
        childAdapter.notifyDataSetChanged();
*/
       // viewHolder.salesAmount.setText(aSale.getTotal()!=null?aSale.getTotal().toString():"");
        String date = (null!=aSale.getUpdatedOn()?simpleDateFormat.format(aSale.getUpdatedOn()):"");
        viewHolder.salesDate.setText(date);

    }

    public void replaceData(List<SalesOrder> notes) {
        setList(notes);
        notifyDataSetChanged();
    }

    private void setList(List<SalesOrder> notes) {
        salesList = checkNotNull(notes);
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    public SalesOrder getItem(int position) {
        return salesList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView salesId;
        public TextView salesDate;
        public TextView customerName;
        public TextView salesPerson;
        public TextView salesAmount;
        public TextView docId;
        public TextView totalItems;
        public LinearLayout cardView;
        public RecyclerView rvProductList;
        //private SalesActivity.SalesItemListener mItemListener;

        public ViewHolder(View itemView, SalesOrderDisplayActivity.SalesItemListener listener) {
            super(itemView);
            mItemListener = listener;
            salesId = itemView.findViewById(R.id.sales_id);
            salesDate = itemView.findViewById(R.id.sales_date);
            customerName = itemView.findViewById(R.id.customer_name);
            salesAmount = itemView.findViewById(R.id.sales_amount);
            salesPerson = itemView.findViewById(R.id.sales_person);
            docId  = itemView.findViewById(R.id.doc_id);
            totalItems = itemView.findViewById(R.id.total_items);
            rvProductList =itemView.findViewById(R.id.list_details);
            //itemView.setOnClickListener(this);
        }

    }
}
