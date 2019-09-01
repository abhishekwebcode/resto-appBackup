package com.loftysys.starbites.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.Activities.SplashScreen;
import com.loftysys.starbites.Adapter.CartListAdapter;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.SignUpResponse;
import com.loftysys.starbites.MVP.UserProfileResponse;
import com.loftysys.starbites.PaymentIntegrationMethods.OrderConfirmed;
import com.loftysys.starbites.PaymentIntegrationMethods.PayPalActivityPayment;
import com.loftysys.starbites.PaymentIntegrationMethods.StripePaymentIntegration;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;
import com.smsgh.hubtelpayment.Class.Environment;
import com.smsgh.hubtelpayment.Exception.MPowerPaymentException;
import com.smsgh.hubtelpayment.Interfaces.OnPaymentResponse;
import com.smsgh.hubtelpayment.MpowerPayments;

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

public class ChoosePaymentMethod extends Fragment {
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
                    case R.id.asoriba:
                        voucherBox.setVisibility(GONE);
                        break;
                    case R.id.voucher:
                        voucherBox.setVisibility(View.VISIBLE);
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
        voucher_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyVoucher(v);
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
                        //TODO  CHECK HERE for migration
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

    private void continueArisoba() {

        try {

            com.smsgh.hubtelpayment.SessionConfiguration sessionConfiguration = new com.smsgh.hubtelpayment.SessionConfiguration()
                    .Builder()
                    .setClientId("l7DnBx5")
                    .setSecretKey("5243eee047414979a41a138f5de61117")
                    .setEndPointURL("http://www.comidaghana.com/starbitesgh_app/callback_hubtel")
                    .setEnvironment(Environment.TEST_MODE)
                    .build();
            MpowerPayments mpowerPayments = new MpowerPayments(sessionConfiguration);
            mpowerPayments.setPaymentDetails(10, "This is a demo payment");
            mpowerPayments.Pay(getActivity());
            mpowerPayments.setOnPaymentCallback(new OnPaymentResponse() {
                @Override
                public void onFailed(String token, String reason) {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), reason, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(String token) {
                    Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "canceled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccessful(String token) {
                    Toast.makeText(getActivity(), "SUCESS", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MPowerPaymentException e) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (true) return;

        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        MainActivity reference = (MainActivity) getActivity();


        Api.getClient().addOrderVoucher(MainActivity.userId,
                MyCartList.cartistResponseData.getCartid(),
                ChoosePaymentMethod.address,
                ChoosePaymentMethod.mobileNo,
                "Hubtel Payment for user " + MainActivity.userId,
                "Pending",
                CartListAdapter.totalAmountPayable,
                "Hubtel",
                -2,
                "-1",
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                "",
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
                        Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                        ((Activity) getActivity()).finish();
                    }
                });
    }

    private void moveNext() {
        paymentMethod = "";
        switch (paymentMethodsGroup.getCheckedRadioButtonId()) {
            case R.id.asoriba:
                voucherBox.setVisibility(GONE);
                continueArisoba();
                break;
            case R.id.voucher:
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
                            CartListAdapter.totalAmountPayable,
                            "Voucher",
                            delivery,
                            "-1",
                            reference.branch == null ? "" : reference.branch,
                            reference.tableNumber == null ? "" : reference.tableNumber,
                            reference.deliveryType == null ? "" : reference.deliveryType,
                            voucher,
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
                                    Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                                    ((Activity) getActivity()).finish();
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
                intent = new Intent(getActivity(), PayPalActivityPayment.class);
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
                CartListAdapter.totalAmountPayable,
                "COD",
                delivery,
                "-1",
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                voucher,
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
                        Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                        ((Activity) getActivity()).finish();
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

    public void verifyVoucher(View v) {
        if (isVoucherDone) {
            isVoucherDone = false;
            voucher_field.setEnabled(true);
            voucher_field.setText("");
            voucher = "";
            voucher_verified.setText("Verify Voucher");
        } else {
            voucher = voucher_field.getText().toString();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
            Api.getClient().check_voucher(MainActivity.userId, voucher, new ResponseCallback() {
                @Override
                public void success(Response response) {
                    progressDialog.cancel();
                    try {
                        String res = Converter.getString(response);
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("status").equals("1")) {
                            voucher_field.setEnabled(false);
                            voucher_verified.setText("Voucher Applied");
                            isVoucherDone = true;
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        Log.d("jj", res);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progressDialog.cancel();
                }
            });
        }
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
