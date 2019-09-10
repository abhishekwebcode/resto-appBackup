package com.loftysys.starbites.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.loftysys.starbites.Activities.Login;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.SignUpResponse;
import com.loftysys.starbites.PaymentIntegrationMethods.OrderConfirmed;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VoucherSelect extends Fragment {
    View view;
    @BindView(R.id.tag_group)
    TagView tagView;
    @BindView(R.id.redeem)
    AppCompatButton redeem;
    ArrayList<Tag> tags;
    ArrayList<VoucherItem> voucherItems;
    public String currentVoucher="No Voucher Selected";
    @BindView(R.id.voucher_current)
    AppCompatTextView currentv;
    Drawable drawable;
    static String TAG = "show vouchers";
    Boolean activeRedeem  = false;
    public void redeem(View v) {
        if (!activeRedeem) {
            Toast.makeText(getActivity(),"Please select a voucher",Toast.LENGTH_LONG).show();
            return;
        }
        addOrder();
    }

    public void changeCurrentVoucher(String text) {
        currentv.setText(text);
    }
    public void addOrder() {
        if (currentVoucher.equals("")) {
            Toast.makeText(getActivity(), "No Voucher Selected", Toast.LENGTH_SHORT).show();
            return;
        }
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
                "Voucher " + currentVoucher + " Applied for " + MainActivity.userId,
                "Complete",
                reference.totalAmountPayable,
                "Voucher",
                delivery,
                reference.tax,
                reference.branch == null ? "" : reference.branch,
                reference.tableNumber == null ? "" : reference.tableNumber,
                reference.deliveryType == null ? "" : reference.deliveryType,
                currentVoucher,
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
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Error placing your order", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        view = inflater.inflate(R.layout.voucher_select_layout, container, false);
        ButterKnife.bind(this, view);
        MainActivity.title.setText("Available Vouchers");
        if (!MainActivity.userId.equalsIgnoreCase("")) {
            getVouchers();
        } else {
            Config.moveTo(getActivity(), Login.class);
        }
        drawable = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        return view;
    }

    private void getVouchers() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        Api.getClient().getVouchers(MainActivity.userId, new ResponseCallback() {
            @Override
            public void success(Response response) {
                pDialog.dismissWithAnimation();
                try {
                    String res = Converter.getString(response);
                    JSONObject response1 = new JSONObject(res);
                    JSONArray jsonArray = response1.getJSONArray("data");
                    if (jsonArray.length() == 0) {
                        Toast.makeText(getActivity(), "No Vouchers Found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    voucherItems = getVouchers(jsonArray);
                    tags = getTags(voucherItems);
                    tagView.addTags(tags);
                    tagView.setOnTagClickListener(new TagView.OnTagClickListener() {
                        @Override
                        public void onTagClick(Tag tag, int position) {
                            checkVoucher(tag.text);
                        }
                    });
                    redeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            redeem(v);
                        }
                    });
                    Log.d("Vouchers", res);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't fetch your vouchers", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).loadFragment(new MainFragment(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Couldn't fetch your vouchers", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Config.getCartList(getActivity(), true);
    }

    public class VoucherItem {
        public String voucher_name;
        public String voucher_image;

        public VoucherItem(String a, String b) {
            this.voucher_image = b;
            this.voucher_name = a;
        }

    }

    public ArrayList<Tag> getTags(ArrayList<VoucherItem> voucherItemArrayList) throws Exception {
        ArrayList<Tag> arrayList = new ArrayList<>();
        for (int i = 0; i < voucherItemArrayList.size(); i++) {
            Tag temp = new Tag(voucherItemArrayList.get(i).voucher_name);
            temp.background=drawable;
            temp.isDeletable = false;
            arrayList.add(temp);
        }
        return arrayList;
    }

    public void checkVoucher(final String voucher) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getActivity().getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        final MainActivity reference = (MainActivity) getActivity();
        Api.getClient().check_voucher(MainActivity.userId, voucher, Double.parseDouble(reference.totalAmountPayable), new ResponseCallback() {
            @Override
            public void success(Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(Converter.getString(response));
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        ChoosePaymentMethod.lastVoucher = voucher;
                        changeCurrentVoucher(voucher);
                        VoucherSelect.this.currentVoucher=voucher;
                        activeRedeem=true;
                        pDialog.dismiss();
                        //((MainActivity) getActivity()).loadFragment(new ChoosePaymentMethod(), true);
                    } else {
                        activeRedeem=false;
                        changeCurrentVoucher("No Voucher Applied");
                        Toast.makeText(reference, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                } catch (Throwable e) {
                    pDialog.dismiss();
                    Toast.makeText(reference, "Couldn't apply your voucher", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(reference, "Couldn't apply your voucher", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                pDialog.dismiss();
            }
        });
    }

    public ArrayList<VoucherItem> getVouchers(JSONArray array) throws Exception {
        ArrayList<VoucherItem> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            arrayList.add(
                    new VoucherItem(
                            array.getJSONObject(i).getString("voucher_name"),
                            array.getJSONObject(i).getString("voucher_image")
                    )
            );
        }
        return arrayList;
    }

}
