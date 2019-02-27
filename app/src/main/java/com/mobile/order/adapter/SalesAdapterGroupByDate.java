package com.mobile.order.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.activity.SalesOrderActivity;
import com.mobile.order.activity.SalesOrderDisplayActivity;
import com.mobile.order.helper.FirestoreUtil;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.SalesOrder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class SalesAdapterGroupByDate extends RecyclerView.Adapter<SalesAdapterGroupByDate.ViewHolder>  {
    public static final String TAG = "SalesAdapterGroupByDate";
    String pattern = "dd-MMM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private List<SalesOrder> salesList;
    private Map<String,List<SalesOrder>> salesMapByMonthAndYear;
    private List<String> monthAndYearKeyList;
    private SalesOrderDisplayActivity.SalesItemListener mItemListener;
    private Context context;
    private AppCompatActivity currentActivity;
    DaoSession daoSession;
    public SalesAdapterGroupByDate(AppCompatActivity activity, Map<String,List<SalesOrder>> salesMapByMonthAndYear , List<SalesOrder> salesList, SalesOrderDisplayActivity.SalesItemListener itemListener) {
        setList(salesMapByMonthAndYear, salesList);
        currentActivity = activity;
        mItemListener = itemListener;
        this.salesMapByMonthAndYear = salesMapByMonthAndYear;
        monthAndYearKeyList = new ArrayList<>(salesMapByMonthAndYear.keySet());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_item_by_month_year_sub_view,parent, false);

        LayoutInflater inflater = LayoutInflater.from(context);
        //View noteView = inflater.inflate(R.layout.sales_item_by_date, parent, false);
        daoSession = ((BaseApplication) currentActivity.getApplication()).getDaoInstance();
       // salesDetailDao = daoSession.getSalesOrderDao();
        return new ViewHolder(v, mItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.parentDate.setText(monthAndYearKeyList.get(position));
        SalesOrderDisplayActivity.SalesItemListener mItemListener = new SalesOrderDisplayActivity.SalesItemListener() {
            @Override
            public void onNoteClick(SalesOrder clickedSale) {
                //mActionsListener.openNoteDetails(clickedNote);
            }
        };
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(viewHolder.salesListByMonth.getContext());
        //viewHolder.salesListByMonth.setLayoutManager(mLayoutManager);
        viewHolder.salesListByMonth.setItemAnimator(new DefaultItemAnimator());

        int numColumns = 1;
        viewHolder.salesListByMonth.setHasFixedSize(true);
        viewHolder.salesListByMonth.setLayoutManager(new GridLayoutManager(viewHolder.salesListByMonth.getContext(), numColumns));

        final List<SalesOrder> childSalesList = salesMapByMonthAndYear.get(monthAndYearKeyList.get(position));
        SalesDetailSubAdapter childAdapter=new SalesDetailSubAdapter(childSalesList,mItemListener);
        viewHolder.salesListByMonth.setAdapter(childAdapter);
        viewHolder.salesListByMonth.addOnItemTouchListener(new RecyclerItemListener(context, viewHolder.salesListByMonth,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        CardView cardView=(CardView)v;
                        LinearLayout linearLayout = (LinearLayout)cardView.getChildAt(0);
                        TextView docIdElement = (TextView)linearLayout.getChildAt(0);

                        LinearLayout innerlinearLayout =(LinearLayout)((ScrollView) ((LinearLayout)linearLayout.getChildAt(1)).getChildAt(0)).getChildAt(0);
                        LinearLayout salesIdLayout = (LinearLayout)innerlinearLayout.getChildAt(0);
                        TextView salesIdElement = (TextView)salesIdLayout.getChildAt(1);

                        LinearLayout customerLayout = (LinearLayout)innerlinearLayout.getChildAt(2);
                        TextView custNameElement = (TextView)customerLayout.getChildAt(1);

                        showOptions(salesIdElement.getText().toString() , custNameElement.getText().toString() ,docIdElement.getText().toString() );
                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));
        childAdapter.notifyDataSetChanged();
        //Sales aSale = salesList.get(position);

    }

    private void showOptions(final String salesId, String custName, final String docId) {
       // final Sales selectedSalesItem = childSalesList;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String[] options ;
            options = new String[3];

            options[0] = "Print " + salesId+ " "+custName;
            options[1] = "Edit " + salesId+ " "+custName;
            options[2] = "Delete " + salesId+ " "+custName;


        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //Toast.makeText(get)
                }
                else if(which == 1){
                    proceedToUpdateItem(salesId, docId);
                }else if(which == 2){
                    deleteSales(salesId, docId);
                }

            }
        });
        alertDialogBuilder.create().show();
    }

    private void proceedToUpdateItem(String salesId, String docId){
        // Pass grocery id to the next screen
        Intent intent = new Intent(context, SalesOrderActivity.class);
        intent.putExtra("create",false);
        intent.putExtra("salesId", salesId);
        intent.putExtra("salesDocId", docId);
        context.startActivity(intent);
    }

    private void deleteSales(final String salesId, final String docId){
        // Pass grocery id to the next screen
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String[] options ;
        options = new String[2];
        alertDialogBuilder.setTitle("Are you sure?");
        options[0] = "Yes ";
        options[1] = "Cancel";


        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    CollectionReference collRef = FirestoreUtil.getSalesCollectionRef();
                    collRef.document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Deleted "+ docId+ " Successfully!");
                            Toast.makeText(context, "Deleted Successfully!",
                                    Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            Activity activity = (Activity) context;
                            activity.recreate();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG,  docId+ " Delete failure!");
                                    Toast.makeText(context, docId+ " Delete failure!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                    /*final DeleteQuery<Sales> salesDeleteQuery = salesDao.queryBuilder().where(SalesDao.Properties.Id.eq(salesId)).buildDelete();
                    salesDeleteQuery.executeDeleteWithoutDetachingEntities();
                    deleteSalesDetails(salesId, docId);*/
                }


            }
        });
        alertDialogBuilder.create().show();

       // fetchSalesList();
    }
    private void deleteSalesDetails(String salesId, String docId){
        /*final DeleteQuery<SalesDetail> salesDetailsDeleteQuery = salesDetailDao.queryBuilder().where(SalesDetailDao.Properties.SalesId.eq(salesId)).buildDelete();
        salesDetailsDeleteQuery.executeDeleteWithoutDetachingEntities();*/
    }

    private void setList(Map<String,List<SalesOrder>> salesMapByMonthAndYearArg , List<SalesOrder> notes) {
        salesList = checkNotNull(notes);
        salesMapByMonthAndYear = salesMapByMonthAndYearArg;
        monthAndYearKeyList = new ArrayList<>(salesMapByMonthAndYear.keySet());
    }

    @Override
    public int getItemCount() {
        monthAndYearKeyList = new ArrayList<>(salesMapByMonthAndYear.keySet());
        return salesMapByMonthAndYear.size();
    }

    public SalesOrder getItem(int position) {
        return salesList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RecyclerView salesListByMonth;
public TextView parentDate;
        //private SalesOrderDisplayActivity.SalesItemListener mItemListener;

        public ViewHolder(View itemView, SalesOrderDisplayActivity.SalesItemListener listener) {
            super(itemView);
            mItemListener = listener;
            salesListByMonth = itemView.findViewById(R.id.sales_list_by_month_year_holder);
            parentDate = itemView.findViewById(R.id.event_list_parent_date);

            //itemView.setOnClickListener(this);
        }

    }
}
