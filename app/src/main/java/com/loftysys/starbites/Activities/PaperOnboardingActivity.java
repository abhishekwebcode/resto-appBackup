package com.loftysys.starbites.Activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.loftysys.starbites.Activities.BaseActivity;
import com.loftysys.starbites.Activities.BaseActivity;

import android.view.View;
import android.widget.Toast;

import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.R;
import com.ramotion.paperonboarding.PaperOnboardingEngine;

import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class PaperOnboardingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);
        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());
        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                Config.moveTo(PaperOnboardingActivity.this, Login.class);
            }
        });
    }

    static int color = Color.parseColor("#FF0000");

    // Just example data for Onboarding
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage(
                "Welcome to Starbites Mobility Experience",
                "Starbites Food & Drink now on Mobile.",
                color, R.drawable.welcome, R.drawable.bottom_welcome
        );
        PaperOnboardingPage scr2 = new PaperOnboardingPage(
                "Sign Up",
                "Sign Up using a valid email, update your profile and you`re all set.",
                color, R.drawable.sign_up, R.drawable.bottom_signup);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(
                "Self-Service",
                "No more waiter frustrations, view menu on mobile place order and choose your preferred delivery method and branch (Dine-In, Take Out, Delivery).",
                color, R.drawable.self_service, R.drawable.bottom_self_service);
        PaperOnboardingPage scr4 = new PaperOnboardingPage(
                "Secured Online Payment",
                "Pay using any Mobile Wallet or Card. All-In-One place.",
                color, R.drawable.payment, R.drawable.bottom_payment);
        PaperOnboardingPage scr5 = new PaperOnboardingPage(
                "Customer Service",
                "Got complaint to make? Don’t worry you`ve got three ways to reach customer support right In-App.",
                color, R.drawable.customer_service, R.drawable.bottom_customer_service);
        PaperOnboardingPage scr6 = new PaperOnboardingPage(
                "Let`s Get Started",
                "Swipe once more to get started.",
                color, R.drawable.lets_get_started, R.drawable.bottom_lgs);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        elements.add(scr5);
        elements.add(scr6);
        return elements;
    }
}