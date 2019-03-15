package com.mobile.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mobile.order.BaseApplication;
import com.mobile.order.R;
import com.mobile.order.async.RefreshProduct;
import com.mobile.order.async.RefreshSalesPerson;
import com.mobile.order.helper.AppUtil;
import com.mobile.order.model.Config;
import com.mobile.order.model.DaoSession;
import com.mobile.order.model.ProductDao;
import com.mobile.order.model.SalesPersonDao;
import com.mobile.order.model.User;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends BaseActivity {

    //private ImageView bookIconImageView;
    //private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    private String ADMIN="admin";
    private String SALES="sales";
    private String DELIVERY = "delivery";
    @BindView(R.id.emailEditText)
    TextInputEditText email;

    @BindView(R.id.passwordEditText)
    TextInputEditText pwd;
    private Config config;
    DaoSession daoSession;
    SalesPersonDao salesPersonDao;
    ProductDao productDao;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        daoSession = ((BaseApplication) getApplication()).getDaoInstance();
        salesPersonDao = daoSession.getSalesPersonDao();
        productDao = daoSession.getProductDao();
        initViews();
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                //bookITextView.setVisibility(GONE);
                loadingProgressBar.setVisibility(GONE);
                rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorSplashText));
                //bookIconImageView.setImageResource(R.drawable.background_color_book);
                startAnimation();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void initViews() {
        //bookIconImageView = findViewById(R.id.bookIconImageView);
        //bookITextView = findViewById(R.id.bookITextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
        afterAnimationView.setVisibility(VISIBLE);
        config = ((BaseApplication) getApplication()).getConfig();
        User loggedInUser = config.getLoggedInUser();
        if(null!=loggedInUser){
            landActivity(null,null,loggedInUser);
        }

    }

    private void startAnimation() {
        /*ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });*/
    }
    @OnClick(R.id.loginButton)
    public void login(View button) {
        boolean validFlg = true;
        String userEmail = email.getText().toString();
        String userPwd = pwd.getText().toString();
        landActivity(userEmail,userPwd, null);
    }
    private void landActivity(String userEmail, String userPwd, User loggedInUser){
        boolean validFlg = true;
        Intent intent = null;
        if(null!=loggedInUser){
            userEmail = loggedInUser.getEmail();
        }
        if(StringUtils.isEmpty(userEmail)){
            Toast.makeText(LoginActivity.this, "User Id is empty! Please provide User Id.",
                    Toast.LENGTH_SHORT).show();
            validFlg = false;
        }
        else if(null==loggedInUser && StringUtils.isEmpty(userPwd)){
            Toast.makeText(LoginActivity.this, "Password is empty! Please provide password.",
                    Toast.LENGTH_SHORT).show();
            validFlg = false;
        }
        if(null==loggedInUser){
            if(validFlg && ADMIN.equals(userEmail) && userPwd.equals("ajay")){
                intent = prepareIntent(ADMIN);
            }
            if(validFlg && SALES.equals(userEmail) && userPwd.equals("ajay")){
                intent = prepareIntent(SALES);
            }
            if(validFlg && DELIVERY.equals(userEmail) && userPwd.equals("ajay")){
                intent = prepareIntent(DELIVERY);
            }
        }
        else{
            intent = prepareIntent(userEmail);
        }
        if(null!=intent){
            User user=new User();
            user.setEmail(userEmail);
            config.setUserLoggedIn(true);
            config.setLoggedInUser(user);
            startActivity(intent);
            finish();
            RefreshProduct asyncRefreshProduct = new RefreshProduct(this);
            asyncRefreshProduct.execute();
            RefreshSalesPerson asyncRefreshSalesPerson = new RefreshSalesPerson(this);
            asyncRefreshSalesPerson.execute();
        }
    }
    private Intent prepareIntent(String userId){
        Intent intent = null;
        switch (userId){
            case "admin":
                intent = new Intent(LoginActivity.this, MainActivity.class);
                AppUtil.putInLoginPref(this, "ADMIN");
                break;
            case "sales":
                intent = new Intent(LoginActivity.this, SalesOrderLandActivity.class);
                AppUtil.putInLoginPref(this, "SALES");
                break;
            case "delivery":
                intent = new Intent(LoginActivity.this, SalesCallbackOrderSimpleDisplayActivity.class);
                AppUtil.putInLoginPref(this, "DELIVERY");
                break;
        }
        return  intent;
    }

}
