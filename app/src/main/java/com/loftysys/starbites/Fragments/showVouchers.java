package com.loftysys.starbites.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.system.ErrnoException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.loftysys.starbites.Activities.AccountVerification;
import com.loftysys.starbites.Activities.EditCart;
import com.loftysys.starbites.Adapter.MyWishListAdapter;
import com.loftysys.starbites.Extras.Common;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Activities.Login;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.Product;
import com.loftysys.starbites.MVP.VoucherResponse;
import com.loftysys.starbites.MVP.WishlistResponse;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;
import com.loftysys.starbites.Activities.SignUp;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class showVouchers extends Fragment {
    View view;
    @BindView(R.id.listview)
    ListView listView;
    static String TAG = "show vouchers";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        view = inflater.inflate(R.layout.fragment_myvouchers, container, false);
        ButterKnife.bind(this, view);
        MainActivity.title.setText("My Vouchers");
        if (!MainActivity.userId.equalsIgnoreCase("")) {
            getVouchers();
        }
        else {
            Config.moveTo(getActivity(), Login.class);
        }
        listView=view.findViewById(R.id.listview);
        getVouchers();
        return view;
    }
    public SweetAlertDialog alertDialog1;
    public void showNoVouchers() {
        System.out.println("CALLING ON SHOW NO COUVHER");
        if (alertDialog1!=null) {
            alertDialog1.dismiss();
            alertDialog1=null;
        }
        alertDialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        alertDialog1.setCanceledOnTouchOutside(false);
        final SweetAlertDialog alertDialog = alertDialog1;
        alertDialog.setTitleText("Oops");
        alertDialog.setContentText("You don't have any vouchers");
        alertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                alertDialog.dismissWithAnimation();
                ((MainActivity)getActivity()).loadFragment(new MainFragment(),false);
            }
        });
        alertDialog.show();
        Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(getResources().getDrawable(R.drawable.custom_dialog_button));
        btn.setText("Shop More");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismissWithAnimation();
                ((MainActivity)getActivity()).loadFragment(new MainFragment(),false);
            }
        });
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
                    if (response1.isNull("data")) {
                        showNoVouchers();
                        return;
                    }
                    JSONArray jsonArray = response1.getJSONArray("data");
                    if (jsonArray.length()==0) {
                        showNoVouchers();
                        return;
                    }
                    ArrayList<VoucherItem> vouchers=new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        vouchers.add(new VoucherItem(
                           jsonArray.getJSONObject(i).getString("voucher_name"),
                           jsonArray.getJSONObject(i).getString("voucher_image")
                        ));
                    }
                    listView.setAdapter(new adapter(getActivity(),R.layout.voucher_item,vouchers));
                    Log.d("VOuchers",res);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't fetch your vouchers", Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).loadFragment(new MainFragment(),false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Couldn't fetch your vouchers", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).loadFragment(new MainFragment(),false);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Config.getCartList(getActivity(), true);
    }

    class VoucherItem {
        public String voucher_name;
        public String voucher_image;
        public VoucherItem(String a, String b){
            this.voucher_image=b;
            this.voucher_name=a;
        }
    }
    static public class adapter extends ArrayAdapter<VoucherItem> {
        private int resourceLayout;
        private Context mContext;
        List<VoucherItem> list;

        public adapter(Context context, int resource, List<VoucherItem> items) {
            super(context, resource, items);
            this.resourceLayout = resource;
            this.mContext = context;
            this.list=items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
                LayoutInflater vi;
                vi = LayoutInflater.from(mContext);
                v = vi.inflate(resourceLayout, null);

            VoucherItem p = getItem(position);
            ((AppCompatTextView)v.findViewById(R.id.textview)).setText(p.voucher_name);
            ImageView imageView = ((ImageView)v.findViewById(R.id.imageview));
            Picasso.with(mContext)
                    .load("https://comidaghana.com/starbitesgh_app/"+p.voucher_image)
                    .placeholder(R.drawable.defaultimage)
                    .into(imageView);
            return v;
        }

    }


}
