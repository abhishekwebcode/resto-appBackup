package com.loftysys.starbites.PaymentIntegrationMethods;

import android.content.Intent;
import android.os.Bundle;
import com.loftysys.starbites.Activities.BaseActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmed extends BaseActivity {

    @BindView(R.id.continueShopping)
    Button continueShopping;
    @BindView(R.id.order_confirmed)
    ImageView orderConfirmed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
        ButterKnife.bind(this);
        if (getIntent().hasExtra("Delivery")) {
            if (getIntent().getStringExtra("Delivery").equals("Delivery")) {
                orderConfirmed.setImageResource(R.drawable.delivery_sucess_icon);
            } else {
                orderConfirmed.setImageResource(R.drawable.dine_in_takeout_sucess_icon);
            }
        }
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
