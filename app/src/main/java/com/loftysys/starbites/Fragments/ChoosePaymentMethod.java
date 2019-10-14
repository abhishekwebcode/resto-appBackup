package com.loftysys.starbites.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.norris.paywithslydepay.core.PayWithSlydepay;
import com.apps.norris.paywithslydepay.core.SlydepayPayment;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.Activities.SplashScreen;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.SignUpResponse;
import com.loftysys.starbites.MVP.UserProfileResponse;
import com.loftysys.starbites.PaymentIntegrationMethods.OrderConfirmed;
import com.loftysys.starbites.PaymentIntegrationMethods.StripePaymentIntegration;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.view.View.GONE;

//import com.loftysys.starbites.PaymentIntegrationMethods.PayPalActivityPayment;

public class ChoosePaymentMethod extends Fragment {
    public static String lastVoucher = "";
    public static String location;
    static boolean dontgo;
    public AlertDialog browser;
    ProgressDialog progressDialog;
    View view;
    @BindView(R.id.voucher_box)
    LinearLayout voucherBox;
    @BindView(R.id.addNewAddressLayout)
    LinearLayout addNewAddressLayout;
    @BindView(R.id.addressCheckBox)
    CheckBox addressCheckBox;
    @BindView(R.id.addNewAddress)
    TextView addNewAddress;
    @BindView(R.id.fillAddress)
    TextView fillAddress;
    @BindView(R.id.paymentMethodsGroup)
    RadioGroup paymentMethodsGroup;
    @BindView(R.id.makePayment)
    Button makePayment;
    String voucher;
    String paymentMethod;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.choosePaymentLayout)
    LinearLayout choosePaymentLayout;
    @BindViews({R.id.fullNameEdt, R.id.mobEditText, R.id.cityEditText, R.id.areaEditText, R.id.buildingEditText, R.id.pincodeEditText, R.id.stateEditText, R.id.landmarkEditText,})
    List<EditText> editTexts;
    @BindView(R.id.voucher_field)
    EditText voucher_field;
    @BindView(R.id.voucher_verified)
    AppCompatButton voucher_verified;
    public static String address, mobileNo, userEmail, profilePinCode;
    Intent intent;
    Boolean isVoucherDone = false;
    @BindView(R.id.amountPayable)
    TextView amountPayable;


    public void payI_Pay() {
        try {
            ((MainActivity) getActivity()).loadFragment(new iPay(), true);
        } catch (Throwable e) {
            Log.d("iPAY", "payI_Pay: "+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int layout = R.layout.fragment_choose_payment_method;
        view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);
        MainActivity.title.setText("Choose Payment Method");
        MainActivity.cart.setVisibility(GONE);
        MainActivity.cartCount.setVisibility(GONE);
        getUserProfileData();
        String total = ((MainActivity) getActivity()).totalAmountPayable;
        amountPayable.setText(MainActivity.currency + total);
        addressCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    addNewAddressLayout.setVisibility(GONE);
                    addNewAddress.setText("Add New Address");
                }
            }
        });

        choosePaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);

            }
        });
        paymentMethodsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentMethodsGroup.getCheckedRadioButtonId()) {
                    case R.id.slydepay:
                        voucherBox.setVisibility(GONE);
                    case R.id.asoriba:
                        voucherBox.setVisibility(GONE);
                        break;
                    case R.id.voucher:
                        paymentMethodsGroup.clearCheck();
                        ((MainActivity) getActivity()).loadFragment(new VoucherSelect(), true);
                        //voucherBox.setVisibility(View.VISIBLE);
                        break;
                    case R.id.cod:
                        voucherBox.setVisibility(GONE);
                        break;
                    default:
                        voucherBox.setVisibility(GONE);
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.cart.setVisibility(View.VISIBLE);
        MainActivity.cartCount.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.addNewAddress, R.id.makePayment, R.id.fillAddress})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.addNewAddress:
                    addNewAddressLayout.setVisibility(View.VISIBLE);
                    addressCheckBox.setChecked(false);
                    addNewAddress.setText("Use This Address");
                    break;
                case R.id.makePayment:
                    if (!addressCheckBox.isChecked()) {
                        if (addNewAddressLayout.getVisibility() == View.VISIBLE) {
                            if (validate(editTexts.get(0))
                                    && validate(editTexts.get(1))
                                    && validate(editTexts.get(2))
                                    && validate(editTexts.get(3))
                                    && validate(editTexts.get(4))
                                    && validatePinCode(editTexts.get(5))
                                    && validate(editTexts.get(6))) {
                                String s = "";
                                if (editTexts.get(6).getText().toString().trim().length() > 0) {
                                    s = ", " + editTexts.get(6).getText().toString().trim();
                                }
                                address = editTexts.get(0).getText().toString().trim()
                                        + ", "
                                        + editTexts.get(4).getText().toString().trim()
                                        + s
                                        + ", " + editTexts.get(3).getText().toString().trim()
                                        + ", " + editTexts.get(2).getText().toString().trim()
                                        + ", " + editTexts.get(6).getText().toString().trim()
                                        + ", " + editTexts.get(5).getText().toString().trim()
                                        + "\n" + editTexts.get(1).getText().toString().trim();
                                mobileNo = editTexts.get(1).getText().toString().trim();
                                Log.i("NO", "NO ERROR IN DATA");
                                moveNext();
                            }
                        } else {

                            Config.showCustomAlertDialog(getActivity(),
                                    "Please choose your saved address or add new to make payment",
                                    "",
                                    SweetAlertDialog.ERROR_TYPE);
                        }
                    } else {
                        // TODO  CHECK HERE for migration
                        if (true || SplashScreen.restaurantDetailResponseData.getDeliverycity().contains(profilePinCode.trim()))
                            moveNext();
                        else {
                            Config.showPincodeCustomAlertDialog1(getActivity(),
                                    "Not Available",
                                    "We have stopped food delivery in your area. Please change your pincode.",
                                    SweetAlertDialog.WARNING_TYPE);

                        }
                    }

                    break;
                case R.id.fillAddress:
                    ((MainActivity) getActivity()).loadFragment(new MyProfile(), true);
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Please check all details carefully", Toast.LENGTH_SHORT).show();
        }

    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void continueHubtel() {
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
        Api.getClient().addOrderVoucherHuntel(MainActivity.userId,
                MyCartList.cartistResponseData.getCartid(),
                ChoosePaymentMethod.address,
                ChoosePaymentMethod.mobileNo,
                "COD Applied for " + MainActivity.userId,
                "Complete",
                reference.totalAmountPayable,
                "COD",
                delivery,
                reference.tax,
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                voucher, "true",
                new ResponseCallback() {
                    @Override
                    public void success(Response response) {
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(Converter.getString(response));
                            if (jsonObject.getBoolean("success")) {
                                checkout(jsonObject.getString("url"));
                            } else {
                                Toast.makeText(reference, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Couldn't load your payment", Toast.LENGTH_SHORT).show();
                        }
                        if (true) return;
                        Intent intent = new Intent(getActivity(), OrderConfirmed.class);
                        intent.putExtra("Delivery", ((MainActivity) getActivity()).deliveryType);
                        getActivity().startActivity(intent);
                        ((Activity) getActivity()).finishAffinity();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void checkout(String url) {
        final WebView webView = new WebView(getActivity());
        final WebSettings webSettings = webView.getSettings();
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).setCancelable(false).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getActivity(), "Payment Window was closed", Toast.LENGTH_SHORT).show();
                webView.destroy();
                dialog.dismiss();
            }
        }).setNeutralButton("Cancel Payment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Payment Window was closed", Toast.LENGTH_SHORT).show();
                webView.destroy();
                dialog.dismiss();
            }
        }).setView(webView);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                webView.destroy();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.addJavascriptInterface(new WebAppInterface(this), "post");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Toast.makeText(getActivity(), "Payment Window was closed", Toast.LENGTH_SHORT).show();
                    webView.destroy();
                }
            });
        webView.loadUrl(url);
        if (browser != null) {
            browser.cancel();
            browser = null;
        }
        browser = builder.create();
        browser.setCanceledOnTouchOutside(false);
        browser.show();
    }

    public class WebAppInterface {
        ChoosePaymentMethod mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(ChoosePaymentMethod c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void complete(String status) {
            Toast.makeText(mContext.getActivity(), status, Toast.LENGTH_SHORT).show();
            mContext.browser.dismiss();
            switch (status) {
                case "Success":
                    Intent intent = new Intent(getActivity(), OrderConfirmed.class);
                    intent.putExtra("Delivery", ((MainActivity) getActivity()).deliveryType);
                    getActivity().startActivity(intent);
                    ((Activity) getActivity()).finishAffinity();
                    break;
                case "Fail":
                    Toast.makeText(getActivity(), "Your payment failed, if money was deducted please contact your bank ", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }


    public void SlydePay() {
        MainActivity reference = (MainActivity) getActivity();
        try {
            SlydepayPayment slydepayPayment = new com.apps.norris.paywithslydepay.core.SlydepayPayment(getActivity());
            slydepayPayment.initCredentials("chief.nuamahfc@starbitesgh.com", "1567419825087");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        PayWithSlydepay.Pay(getActivity(), "Your order with Starbites", Double.parseDouble(reference.totalAmountPayable), "Starbites order", MainActivity.userId, "hillsontechnology@outlook.com", MyCartList.cartistResponseData.getCartid(), "", 2);
    }


    private void moveNext() {
        paymentMethod = "";
        switch (paymentMethodsGroup.getCheckedRadioButtonId()) {
            case R.id.card:
                Toast.makeText(getActivity(), "Will be available in next version", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ipay:
                payI_Pay();
                break;
            case R.id.asoriba:
                voucherBox.setVisibility(GONE);
                /*
                Arisoba was migrated to hubtel
                 */
                continueHubtel();
                break;
            case R.id.slydepay:
                SlydePay();
                break;
            case R.id.voucher:
                if (true) break;
                voucherBox.setVisibility(View.VISIBLE);
                if (isVoucherDone) {
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
                            "Voucher " + voucher + " Applied for " + MainActivity.userId,
                            "Complete",
                            reference.totalAmountPayable,
                            "Voucher",
                            delivery,
                            reference.tax,
                            reference.branch == null ? "" : reference.branch,
                            reference.tableNumber == null ? "" : reference.tableNumber,
                            reference.deliveryType == null ? "" : reference.deliveryType,
                            voucher,
                            location,
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
                                    //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Please apply a voucher", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cod:
                voucherBox.setVisibility(GONE);
                paymentMethod = "cod";
                doCOd();
                if (true) return;
                Config.addOrder(getActivity(),
                        "COD",
                        "COD", ((MainActivity) getActivity()).deliveryType);
                break;
            case R.id.satellite:
                //paypal was here
                paymentMethod = "paypal";
                //intent = new Intent(getActivity(), PayPalActivityPayment.class);
                startActivity(intent);
                break;
            case R.id.search:
                paymentMethod = "stripe";
                intent = new Intent(getActivity(), StripePaymentIntegration.class);
                startActivity(intent);
                break;
            default:
                voucherBox.setVisibility(GONE);
                paymentMethod = "";
                Config.showCustomAlertDialog(getActivity(),
                        "Payment Method",
                        "Select your payment method to make payment",
                        SweetAlertDialog.NORMAL_TYPE);
                break;


        }

    }

    private void doCOd() {
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
                "COD Applied for " + MainActivity.userId,
                "Complete",
                reference.totalAmountPayable,
                "COD",
                delivery,
                reference.tax,
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                voucher,
                location,
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
                        Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean validate(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return true;
        }
        editText.setError("Please Fill This");
        editText.requestFocus();
        return false;
    }


    private boolean validatePinCode(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            if (SplashScreen.restaurantDetailResponseData.getDeliverycity().contains(editText.getText().toString().trim()))
                return true;
            else {
                Config.showPincodeCustomAlertDialog(getActivity(),
                        "Not Available",
                        "We currently don't deliver in your area.",
                        SweetAlertDialog.WARNING_TYPE);
                editText.setError("Not available");
                editText.requestFocus();
                return false;
            }
        }
        editText.setError("Please Fill This");
        editText.requestFocus();
        return false;
    }

    public void getUserProfileData() {
        progressBar.setVisibility(View.VISIBLE);
        makePayment.setClickable(false);
        Api.getClient().getUserProfile(
                MainActivity.userId, new Callback<UserProfileResponse>() {
                    @Override
                    public void success(UserProfileResponse userProfileResponse, Response response) {
                        makePayment.setClickable(true);
                        progressBar.setVisibility(GONE);
                        userEmail = userProfileResponse.getEmail();
                        String s = "";
                        if (!userProfileResponse.getLandmark().equalsIgnoreCase("")) {
                            s = ", " + userProfileResponse.getLandmark();
                        }
                        if (userProfileResponse.getFlat().equalsIgnoreCase("")) {
                            addressCheckBox.setChecked(false);
                            addressCheckBox.setVisibility(GONE);
                            fillAddress.setVisibility(View.VISIBLE);
                        } else {
                            address = userProfileResponse.getName()
                                    + ", "
                                    + userProfileResponse.getFlat()
                                    + s
                                    + ", " + userProfileResponse.getLocality()
                                    + ", " + userProfileResponse.getCity()
                                    + ", " + userProfileResponse.getState()
                                    + ", " + userProfileResponse.getPincode()
                                    + "\n" + userProfileResponse.getMobile();
                            addressCheckBox.setText(address);
                            mobileNo = userProfileResponse.getMobile();
                            profilePinCode = userProfileResponse.getPincode();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        makePayment.setClickable(true);
                        progressBar.setVisibility(GONE);

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
