package com.mobile.order.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.order.BaseApplication;
import com.mobile.order.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.zxing.integration.android.BarCodeIntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mobile.order.model.Config;

public class MainActivity extends BaseActivity {
    @BindView(R.id.scanned_text)
    TextView scannedText;
    private Config config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        config = ((BaseApplication) getApplication()).getConfig();
    }
    @OnClick(R.id.config)
    public void landConfig() {
        Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
        startActivity(intent);
    }
 /*   @OnClick(R.id.sales_order)
    public void landSalesOrder() {
        Intent intent = new Intent(MainActivity.this, SalesOrderActivity.class);
        startActivity(intent);
    }*/
    @OnClick(R.id.add_product)
    public void landAddProducts() {
        Intent intent = new Intent(MainActivity.this, ProductActivity.class);
        this.startActivity(intent);
    }
    @OnClick(R.id.display_product)
    public void landDisplayAndUpdateProducts() {
        Intent intent = new Intent(MainActivity.this, DisplayAndUpdateProductActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.add_sales_person)
    public void landAddSalesPerson() {
        Intent intent = new Intent(MainActivity.this, SalesPersonActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.display_sales_person)
    public void landDisplayAndUpdateSalesPerson() {
        Intent intent = new Intent(MainActivity.this, DisplayAndUpdateSalesPersonActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.display_sales_order)
    public void landDisplaySalesOrder() {
        Intent intent = new Intent(MainActivity.this, SalesCallbackOrderDisplayActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.display_today_sales_order)
    public void landDisplaySimapleSalesOrder() {
        Intent intent = new Intent(MainActivity.this, SalesCallbackOrderSimpleDisplayActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.new_design)
    public void landNewDesign() {
        Intent intent = new Intent(MainActivity.this, SalesOrderLandActivity.class);
        startActivity(intent);
    }
   /* @OnClick(R.id.barcode_reader)
    public void scanBarcode(View view) {
        new BarCodeIntentIntegrator((Activity)this).initiateScan();
    }*/
   /* @OnClick(R.id.turn_light)
    public void onAndOff() {
        Intent intent = new Intent(MainActivity.this, OnOffCameraFlashlight.class);
        startActivity(intent);
    }*/
    @OnClick(R.id.log_out)
    public void logout() {
        config.logoutUser(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = BarCodeIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                scannedText.setText(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
