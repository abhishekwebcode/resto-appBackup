package com.loftysys.starbites.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loftysys.starbites.Activities.Login;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.SignUpResponse;
import com.loftysys.starbites.PaymentIntegrationMethods.OrderConfirmed;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;

import java.net.URLEncoder;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.loftysys.starbites.Fragments.showVouchers.TAG;

public class iPay extends Fragment {

    public void iPayFailed() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        final MainActivity reference = (MainActivity) getActivity();
        Integer delivery = 0;
        try {
            delivery = Integer.parseInt(reference.deliveryCharge);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Api.getClient().addOrderVoucher(MainActivity.userId,
                MyCartList.cartistResponseData.getCartid(),
                ChoosePaymentMethod.address,
                ChoosePaymentMethod.mobileNo,
                "iPay Order for " + MainActivity.userId,
                "Failed",
                reference.totalAmountPayable,
                "iPay",
                delivery,
                reference.tax,
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                "",
                ChoosePaymentMethod.location,
                new Callback<SignUpResponse>() {
                    @Override
                    public void success(SignUpResponse signUpResponse, Response response) {
                        pDialog.dismiss();
                        try {
                            Log.d("RESPONSE", Converter.getString(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ((MainActivity)reference.getActivity()).getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Error registering your order", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void bookIPay() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        MainActivity reference = (MainActivity) getActivity();
        Integer delivery = 0;
        try {
            delivery = Integer.parseInt(reference.deliveryCharge);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Api.getClient().addOrderVoucher(MainActivity.userId,
                MyCartList.cartistResponseData.getCartid(),
                ChoosePaymentMethod.address,
                ChoosePaymentMethod.mobileNo,
                "iPay Order for " + MainActivity.userId,
                "Complete",
                reference.totalAmountPayable,
                "iPay",
                delivery,
                reference.tax,
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                "",
                ChoosePaymentMethod.location,
                new Callback<SignUpResponse>() {
                    @Override
                    public void success(SignUpResponse signUpResponse, Response response) {
                        pDialog.dismiss();
                        try {
                            Log.d("RESPONSE", Converter.getString(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("GOING TO MAIN ACTIVITY");
                        Intent intent = new Intent(getActivity(), OrderConfirmed.class);
                        intent.putExtra("Delivery", ((MainActivity) getActivity()).deliveryType);
                        getActivity().startActivity(intent);
                        ((Activity) getActivity()).finishAffinity();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Error registering your order", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public View view;
    public class paymentInterceptor {
        iPay iPay;
        public paymentInterceptor(iPay iPay) {
            this.iPay = iPay;
        }
        @JavascriptInterface
        public void complete(String status) {
            switch (status) {
                case "Success":
                    Toast.makeText(iPay.getActivity(), "Your payment is successfully complete", Toast.LENGTH_SHORT).show();
                    bookIPay();
                    break;
                case "Fail":
                    Toast.makeText(iPay.getActivity(), "Sorry, your payment was not successful", Toast.LENGTH_SHORT).show();
                    iPayFailed();
                    break;
                default:
                    Toast.makeText(iPay.getActivity(), "Sorry, something went wrong", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public ProgressDialog progressDialog;

    WebView webview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        view = inflater.inflate(R.layout.fragment_i_pay, container, false);
        webview=view.findViewById(R.id.webview);
        MainActivity.title.setText("iPay");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webview.addJavascriptInterface(new paymentInterceptor(this), "husidhfisd");
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "onConsoleMessage: " + consoleMessage.message());

                return super.onConsoleMessage(consoleMessage);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                closeDialog();
            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        String url = "https://manage.ipaygh.com/gateway/checkout";
        String postData,total,invoice_id;
        total =  ( (MainActivity)getActivity() ).totalAmountPayable;
        invoice_id=MyCartList.cartistResponseData.getCartid()+String.valueOf(new Date().getTime());
        try {
            postData =
                    "merchant_key=" + URLEncoder.encode("df3c2d50-e04a-11e9-ac72-f23c9170642f", "UTF-8") +
                            "&cancelled_url=" + URLEncoder.encode("https://www.comidaghana.com/starbitesgh_app/JSON/terminated.php", "UTF-8") +
                            "&success_url=" + URLEncoder.encode("https://www.comidaghana.com/starbitesgh_app/JSON/true.php", "UTF-8") +
                            "&invoice_id=" + URLEncoder.encode(invoice_id, "UTF-8") +
                            "&total=" + URLEncoder.encode(total, "UTF-8") +
                            "&ipn_url=" + URLEncoder.encode("https://www.comidaghana.com/starbitesgh_app/JSON/ipn.php", "UTF-8");
        } catch (Throwable e) {
            postData = "";
            e.printStackTrace();
        }
        webview.postUrl(url, postData.getBytes());
        Log.d(TAG, "onCreate: the end ipay ");
        return view;
    }



    public void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
