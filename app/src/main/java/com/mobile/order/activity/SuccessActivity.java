package com.mobile.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobile.order.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessActivity extends BaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.ok)
    public void gotoSalesIdActivity(){
        Intent intent = new Intent(SuccessActivity.this, SalesOrderLandActivity.class);
        startActivity(intent);
        finish();
    }
}
