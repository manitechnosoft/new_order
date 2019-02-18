package com.mobile.order.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mobile.order.R;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {

    private ImageView bookIconImageView;
    //private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;

    @BindView(R.id.emailEditText)
    TextInputEditText email;

    @BindView(R.id.passwordEditText)
    TextInputEditText pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViews();
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                //bookITextView.setVisibility(GONE);
                loadingProgressBar.setVisibility(GONE);
                rootView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorSplashText));
                bookIconImageView.setImageResource(R.drawable.background_color_book);
                startAnimation();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void initViews() {
        bookIconImageView = findViewById(R.id.bookIconImageView);
        //bookITextView = findViewById(R.id.bookITextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = bookIconImageView.animate();
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
        });
    }
    @OnClick(R.id.loginButton)
    public void login(View button) {
        boolean validFlg = true;
        String userEmail = email.getText().toString();
        String userPwd = pwd.getText().toString();
      if(StringUtils.isEmpty(userEmail)){
          Toast.makeText(LoginActivity.this, "Email is empty! Please provide email.",
                  Toast.LENGTH_SHORT).show();
          validFlg = false;
      }
       else if(StringUtils.isEmpty(userPwd)){
            Toast.makeText(LoginActivity.this, "Password is empty! Please provide password.",
                    Toast.LENGTH_SHORT).show();
          validFlg = false;
        }
        if(validFlg && "admin".equals(userEmail) && userPwd.equals("ajay")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if(validFlg && "sales".equals(userEmail) && userPwd.equals("ajay")){
            Intent intent = new Intent(LoginActivity.this, SalesOrderDisplayActivity.class);
            intent.putExtra("saleFlg", true);
            startActivity(intent);
        }
        if(validFlg && "delivery".equals(userEmail) && userPwd.equals("ajay")){
            Intent intent = new Intent(LoginActivity.this, SalesOrderDisplayActivity.class);
            intent.putExtra("deliveryFlg", true);
            startActivity(intent);
        }
    }
}
