package com.loftysys.starbites.PaymentIntegrationMethods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmed extends AppCompatActivity {

    @BindView(R.id.continueShopping)
    Button continueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.continueShopping)
    public void onClick(View view) {
        System.out.println("GOING TO MAIN ACTIVITY");
        Intent intent = new Intent(OrderConfirmed.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        System.out.println("GOING TO MAIN ACTIVITY");
        Intent intent = new Intent(OrderConfirmed.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
