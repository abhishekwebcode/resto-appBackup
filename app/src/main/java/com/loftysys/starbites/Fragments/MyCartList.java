package com.loftysys.starbites.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.gms.common.util.IOUtils;
import com.loftysys.starbites.Activities.AccountVerification;
import com.loftysys.starbites.Adapter.CartListAdapter;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Activities.Login;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.BranchResponse;
import com.loftysys.starbites.MVP.CartProducts;
import com.loftysys.starbites.MVP.CartistResponse;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;
import com.loftysys.starbites.Activities.SignUp;
import com.loftysys.starbites.Activities.SplashScreen;
import com.loftysys.starbites.utilities.AutoDrop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyCartList extends Fragment {
    public AppCompatSpinner deliverySpinner;
    ArrayList<Converter.Branch> branches;
    ArrayList<Converter.Table> tables;
    public AppCompatSpinner selectBranch;
    View view;
    @BindView(R.id.categoryRecyclerView)
    RecyclerView productsRecyclerView;
    public static List<CartProducts> productsData = new ArrayList<>();
    public static CartistResponse cartistResponseData;
    @BindView(R.id.proceedToPayment)
    Button proceedToPayment;
    public static Context context;
    @BindView(R.id.emptyCartLayout)
    LinearLayout emptyCartLayout;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.continueShopping)
    Button continueShopping;
    CartListAdapter wishListAdapter;
    String deliveryCharge="0";

    @BindView(R.id.verifyEmailLayout)
    LinearLayout verifyEmailLayout;
    String deliveryType=null;
    String tableNumber=null;
    String branch=null;
    public void onDeliveryChanged(int position) {
        onDeliverySelect();
        switch (position) {
            case 0:
                tableNumber=null;
                deliveryType=null;
                break;
            case 1:
                showTableSelector();
            case 2:
                deliveryType="Take Away";
                tableNumber=null;
                break;
            case 3:
                deliveryType="Delivery";
                tableNumber=null;
                break;
        }
    }

    public void dismissDialog() {
        deliverySpinner.setSelection(0);
        tableNumber=null;
    }

    private void changeDeliveryPrice(String price) {
        deliveryCharge=price;
        wishListAdapter.changeDeliveryPriceText(price);
    }

    public void onDeliverySelect() {
        if (deliveryType!=null && deliveryType.equals("Delivery")) {
            if (selectBranch.getSelectedItemPosition()!=0) {
                changeDeliveryPrice(branches.get(selectBranch.getSelectedItemPosition()).price);
            }
            else {
                changeDeliveryPrice("0");
            }
        }
        else {
            changeDeliveryPrice("0");
        }
    }
    public void showTableSelector() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog();
                dialog.dismiss();
            }
        });
        dialogBuilder.setCancelable(false);
        //LayoutInflater inflater = this.getLayoutInflater();
        //final View dialogView = inflater.inflate(R.layout.select_table, null);
        //dialogBuilder.setView(dialogView);
        //final AutoDrop editText = (AutoDrop) dialogView.findViewById(R.id.autoCompleteTextView);
        Log.i("YDFG", "showTableSelector: "+Converter.Table.getTableLi(tables).toString());
        final ArrayAdapter adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, Converter.Table.getTableLi(tables) );
        dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tableNumber=tables.get(which).id;
                deliveryType="Dine-In";
            }
        });
        //editText.setAdapter(adapter);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
    public void setAllNulls() {
        deliveryType=null;
        tableNumber=null;
        branch=null;
    }

    public void onBranchChanged(int pos) {
        if (pos!=0) {
            branch = branches.get(pos).id;
        } else{branch=null;}
        onDeliverySelect();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setAllNulls();
        setAllNulls();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart_list, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        MainActivity.title.setText("My Cart");
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)Double.parseDouble(CartListAdapter.totalAmountPayable) >= Integer.parseInt(SplashScreen.restaurantDetailResponseData.getMinorder()))
                {
                    if (deliveryType==null) {
                        Toast.makeText(getActivity(), "Please select delivery type", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (branch==null) {
                        Toast.makeText(getActivity(), "Please select branch", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (deliveryType.equals("Dine-In")) {
                        if (tableNumber==null) {
                            Toast.makeText(getActivity(), "Your delivery type is set as Dine-In, please select a table number to dine.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    ((MainActivity) getActivity()).deliveryType=deliveryType;
                    ((MainActivity) getActivity()).branch=branch;
                    ((MainActivity) getActivity()).tableNumber=tableNumber;
                    ((MainActivity) getActivity()).totalAmountPayable=wishListAdapter.getTotalAmountPayable();
                    ((MainActivity) getActivity()).loadFragment(new ChoosePaymentMethod(), true);
                }
                else
                    Config.showCustomAlertDialog(getActivity(),
                            "",
                            "Minimum order value must be atleast " + SplashScreen.restaurantDetailResponseData.getMinorder(),
                            SweetAlertDialog.WARNING_TYPE);

            }
        });

        return view;
    }

    @OnClick({R.id.continueShopping, R.id.loginNow, R.id.txtSignUp, R.id.verfiyNow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continueShopping:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
                break;
            case R.id.loginNow:
                Config.moveTo(getActivity(), Login.class);
                break;
            case R.id.txtSignUp:
                Config.moveTo(getActivity(), SignUp.class);
                break;
            case R.id.verfiyNow:
                Config.moveTo(getActivity(), AccountVerification.class);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.cart.setVisibility(View.VISIBLE);
    }

    public void getCartList() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        Api.getClient().getTables(new ResponseCallback() {
            @Override
            public void success(Response response) {
                try {
                    JSONArray jsonObject = new JSONArray(Converter.getString(response));
                    if (jsonObject.getJSONObject(0).getString("success").equalsIgnoreCase("false")) {
                        Toast.makeText(getActivity(),"Error loading cart", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tables = Converter.Table.gettables(jsonObject.getJSONObject(0).getJSONArray("message"));
                    Log.d("TABLES", "success: "+tables.toString());
                } catch (Throwable e ) {e.printStackTrace();}
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Error getting your cart", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });

        Api.getClient().getBranches(new ResponseCallback() {
            @Override
            public void success(Response response) {
                try {
                    JSONArray jsonObject = new JSONArray(Converter.getString(response));
                    branches=Converter.getBranches(jsonObject.getJSONObject(0).getJSONArray("message").toString());
                    Log.d("BRANCHES",branches.toString());

                    Api.getClient().getCartList(MainActivity.userId, new Callback<CartistResponse>() {
                        @Override
                        public void success(CartistResponse cartistResponse, Response response) {
                            cartistResponseData = cartistResponse;
                            pDialog.dismiss();
                            productsData = new ArrayList<>();
                            productsData = cartistResponse.getProducts();

                            if (cartistResponse.getSuccess().equalsIgnoreCase("false")) {
                                verifyEmailLayout.setVisibility(View.VISIBLE);
                                proceedToPayment.setVisibility(View.GONE);
                            } else {
                                try {
                                    Log.d("cartId", cartistResponse.getCartid());
                                    cartistResponse.getProducts().size();
                                    proceedToPayment.setVisibility(View.VISIBLE);
                                    setProductsData();
                                } catch (Exception e) {
                                    proceedToPayment.setVisibility(View.GONE);
                                    emptyCartLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("errorInCartList", error.toString());

                            pDialog.dismiss();

                        }
                    });


                } catch (Throwable e ) {e.printStackTrace();     pDialog.dismiss();}
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("ERROR");
                pDialog.dismiss();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setAllNulls();
        setAllNulls();
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        MainActivity.cart.setVisibility(View.GONE);
        MainActivity.cartCount.setVisibility(View.GONE);
        Config.getCartList(getActivity(), false);
        if (!MainActivity.userId.equalsIgnoreCase("")) {
            getCartList();
        } else {
            proceedToPayment.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setProductsData() {

        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        productsRecyclerView.setLayoutManager(gridLayoutManager);
        wishListAdapter = new CartListAdapter(getActivity(), productsData,branches,this);
        productsRecyclerView.setAdapter(wishListAdapter);

    }
}
