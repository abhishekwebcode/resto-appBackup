package com.loftysys.starbites.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.loftysys.starbites.Activities.AccountVerification;
import com.loftysys.starbites.Adapter.MyWishListAdapter;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Activities.Login;
import com.loftysys.starbites.MVP.Product;
import com.loftysys.starbites.MVP.VoucherResponse;
import com.loftysys.starbites.MVP.WishlistResponse;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;
import com.loftysys.starbites.Activities.SignUp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class showVouchers extends Fragment {
    View view;
    ListView listView;
    static String TAG = "show vouchers";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        ButterKnife.bind(this, view);
        MainActivity.title.setText("My Vouchers");
        if (!MainActivity.userId.equalsIgnoreCase("")) {
            getVouchers();
        }
        else {
            Config.moveTo(getActivity(), Login.class);
        }
        listView=view.findViewById(R.id.listview);
        return view;
    }

    private void getVouchers() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        Api.getClient().getVouchers(MainActivity.userId, new Callback<VoucherResponse>() {
            @Override
            public void success(VoucherResponse wishlistResponse, Response response) {
                pDialog.dismiss();
                try {
                    if (wishlistResponse.getStatus().equalsIgnoreCase("1")) {

                        Log.i(TAG, "success: "+wishlistResponse.getData());

                    } else {
                        Config.moveTo(getActivity(),Login.class);
                    }
                } catch (Exception e) {
                    Log.d("wishList", "Not available");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                pDialog.dismiss();
            }
        });
    }

}
