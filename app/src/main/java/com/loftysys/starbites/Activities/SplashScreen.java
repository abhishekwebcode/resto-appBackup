package com.loftysys.starbites.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.loftysys.starbites.Extras.Common;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Extras.DetectConnection;
import com.loftysys.starbites.MVP.CategoryListResponse;
import com.loftysys.starbites.MVP.Product;
import com.loftysys.starbites.MVP.RecommendedProductsResponse;
import com.loftysys.starbites.MVP.RestaurantDetailResponse;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashScreen extends Activity {

    public static List<CategoryListResponse> categoryListResponseData;
    public static RestaurantDetailResponse restaurantDetailResponseData;
    public static List<Product> allProductsData,recommendedProductList;
    public static List<Product> productList;
    String id = "";

    @BindView(R.id.errorText)
    TextView errorText;
    @BindView(R.id.internetNotAvailable)
    LinearLayout internetNotAvailable;
    @BindView(R.id.splashImage)
    ImageView splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // check data from FCM
        try {
            Intent intent = getIntent();
            id = intent.getStringExtra("id");
            Log.d("notification Data", id);
        } catch (Exception e) {
            Log.d("error notification data", e.toString());
        }


        // Check the internet and get response from API's
        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
            getCategoryList();
        } else {
            errorText.setText("Internet Connection Not Available");
            internetNotAvailable.setVisibility(View.VISIBLE);
            splashImage.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tryAgain)
    public void onClick() {
        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
            internetNotAvailable.setVisibility(View.GONE);
            splashImage.setVisibility(View.VISIBLE);
            getCategoryList();
        } else {
            errorText.setText("Internet Connection Not Available");
            internetNotAvailable.setVisibility(View.VISIBLE);
            splashImage.setVisibility(View.GONE);
        }
    }

    public void getCategoryList() {
        // getting category list news data
        Api.getClient().getCategoryList(new Callback<List<CategoryListResponse>>() {
            @Override
            public void success(List<CategoryListResponse> categoryListResponses, Response response) {
                try {
                    categoryListResponseData = categoryListResponses;
                    Log.d("categoryData", categoryListResponses.get(0).getCategory_name());
                    getRestaurantDetail();
                } catch (Exception e) {
                    errorText.setText("No Category Added In This Store!");
                    internetNotAvailable.setVisibility(View.VISIBLE);
                    splashImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.toString());
                errorText.setText("Internet Connection Not Available");
                internetNotAvailable.setVisibility(View.VISIBLE);
                splashImage.setVisibility(View.GONE);
            }
        });
    }


    public void getRestaurantDetail() {

        // getting slider list data
        Api.getClient().getRestaurantDetail(new Callback<RestaurantDetailResponse>() {
            @Override
            public void success(RestaurantDetailResponse restaurantDetailResponse, Response response) {
                restaurantDetailResponseData = restaurantDetailResponse;
                getRecommendedList();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.toString());
                errorText.setText("Internet Connection Not Available");
                internetNotAvailable.setVisibility(View.VISIBLE);
                splashImage.setVisibility(View.GONE);
            }
        });
    }
    public void getRecommendedList() {
        // getting slider list data
        Api.getClient().getRecommendedProducts(new Callback<RecommendedProductsResponse>() {
            @Override
            public void success(RecommendedProductsResponse recommendedProductsResponse, Response response) {
                if (recommendedProductsResponse.getSuccess().equalsIgnoreCase("true")) {
                    recommendedProductList = recommendedProductsResponse.getProductList();
                }else {
                    recommendedProductList=new ArrayList<>();
                }
                getAllProducts();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.toString());
                errorText.setText("Internet Connection Not Available");
                internetNotAvailable.setVisibility(View.VISIBLE);
                splashImage.setVisibility(View.GONE);
            }
        });
    }

    public void getAllProducts() {
        // getting news list data
        Api.getClient().getAllProducts(new Callback<List<Product>>() {
            @Override
            public void success(List<Product> allProducts, Response response) {
                try {
                    allProductsData = allProducts;
                    Log.d("allProductsData", allProducts.get(0).getProductName());
                    moveNext();
                } catch (Exception e) {
                    Log.d("PRODUCTS SPLASH", "success: ALl Prodicts");
                    e.printStackTrace();
                    errorText.setText("No Product Added In This Store!");
                    internetNotAvailable.setVisibility(View.VISIBLE);
                    splashImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.toString());
                errorText.setText("Internet Connection Not Available");
                internetNotAvailable.setVisibility(View.VISIBLE);
                splashImage.setVisibility(View.GONE);
            }
        });
    }

    private void moveNext() {
// redirect to next page after getting data from server

        boolean isFromNotification;
        try {
            productList = new ArrayList<>();
            if (id.length() > 0) {
                for (int j = 0; j < allProductsData.size(); j++) {
                    if (allProductsData.get(j).getProductId().trim().equalsIgnoreCase(id)) {
                        productList.add(allProductsData.get(j));
                    }
                }

                isFromNotification = true;
            } else {
                isFromNotification = false;
            }
        } catch (Exception e) {
            Log.d("error notification data", e.toString());
            isFromNotification = false;
        }
        if (isFromNotification)
        {
            Config.moveTo(SplashScreen.this, MainActivity.class);
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.putExtra("isFromNotification", isFromNotification);
            startActivity(intent);
            finishAffinity();
        } else if (Common.getSavedUserData(SplashScreen.this, "firstTimeLogin").equalsIgnoreCase("")) {
            Config.moveTo(SplashScreen.this, PaperOnboardingActivity.class);
            finishAffinity();
        }
        else if(Common.getSavedUserData(SplashScreen.this, "userId").equalsIgnoreCase("")) {
            Config.moveTo(SplashScreen.this, Login.class);
            finishAffinity();
        }
        else {
            Config.moveTo(SplashScreen.this, MainActivity.class);
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.putExtra("isFromNotification", isFromNotification);
            startActivity(intent);
            finishAffinity();
        }
    }
}