package com.mobile.order.async;

import android.content.Context;
import android.os.AsyncTask;

import com.mobile.order.helper.FirestoreUtil;

public class RefreshSalesPerson extends AsyncTask<String, String, String> {
    Context context;
    public RefreshSalesPerson(Context context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        FirestoreUtil fireUtil = new FirestoreUtil();
        fireUtil.getSalesPersonList(context, null);
        return null;
    }
}
