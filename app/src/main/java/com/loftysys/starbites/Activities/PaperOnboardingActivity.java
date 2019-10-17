package com.loftysys.starbites.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.R;
import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class PaperOnboardingActivity extends AppCompatActivity {

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
                "Everything you need from Starbites is now on Mobile. From Customer Service to Food Ordering. Follow three (3) simple steps to get started.",
                color, R.drawable.hotels, R.drawable.key);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(
                "Sign Up",
                "Sign Up using a valid email, wait for a while to receive your verification email from Starbites, open the mail and tap on verify to validate your account, once done go back to app and locate Profile then fill all required fields and tap update, once updated successfully, you’re all set to start placing your orders.",
                color, R.drawable.hotels, R.drawable.key);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(
                "Self-Service",
                "No more waiter frustrations, view menu on mobile place order and choose your preferred delivery method and branch (Dine-In, Take Out, Delivery). If Dine-In then select your table number and wait to be served.",
                color, R.drawable.hotels, R.drawable.key);
        PaperOnboardingPage scr4 = new PaperOnboardingPage(
                "Secured Online Payment",
                "Pay using any Mobile Wallet or Card. All-In-One place.",
                color, R.drawable.hotels, R.drawable.key);
        PaperOnboardingPage scr5 = new PaperOnboardingPage(
                "Customer Service",
                "Got complaint to make? Don’t worry you`ve got three ways to reach customer support right In-App. Call, WhatsApp or via E-mail.",
                color, R.drawable.hotels, R.drawable.key);
        PaperOnboardingPage scr6 = new PaperOnboardingPage(
                "Let`s Get Started",
                "Swipe once more to get started.",
                color, R.drawable.hotels, R.drawable.key);

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