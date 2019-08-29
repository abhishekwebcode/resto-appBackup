package com.loftysys.starbites.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loftysys.starbites.Activities.AccountVerification;
import com.loftysys.starbites.Activities.Login;
import com.loftysys.starbites.Activities.MainActivity;
import com.loftysys.starbites.Activities.SignUp;
import com.loftysys.starbites.Activities.SplashScreen;
import com.loftysys.starbites.Extras.Common;
import com.loftysys.starbites.Extras.Config;
import com.loftysys.starbites.Extras.Converter;
import com.loftysys.starbites.MVP.SignUpResponse;
import com.loftysys.starbites.MVP.UserProfileResponse;
import com.loftysys.starbites.R;
import com.loftysys.starbites.Retrofit.Api;
import com.loftysys.starbites.utilities.AutoDrop;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyProfile extends Fragment {

    View view;
    @BindViews({R.id.fullNameEdt, R.id.mobEditText, R.id.cityEditText, R.id.areaEditText, R.id.buildingEditText, R.id.pincodeEditText1, R.id.stateEditText, R.id.landmarkEditText,})
    List<EditText> editTexts;
    UserProfileResponse userProfileResponseData;
    @BindView(R.id.submitBtn)
    Button submitBtn;
    @BindViews({R.id.male, R.id.female})
    List<CircleImageView> circleImageViews;
    String gender = "";
    @BindView(R.id.profileLayout)
    LinearLayout profileLayout;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.logout)
    Button logout;
    @BindView(R.id.pincodeEditText)
    AutoDrop pincode;
    @BindView(R.id.verifyEmailLayout)
    LinearLayout verifyEmailLayout;
    List<Converter.pin> pins;
    ArrayAdapter<Converter.pin> adapter;
    String id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);
        MainActivity.title.setText("My Profile");
        if (!MainActivity.userId.equalsIgnoreCase("")) {
            getUserProfileData();
        } else {
            profileLayout.setVisibility(View.INVISIBLE);
            loginLayout.setVisibility(View.VISIBLE);
        }
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);

            }
        });
        return view;
    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void dismissDialog() {
    }

    public void showPincodeSelector() {
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
        dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    pincode.setText(adapter.getItem(which).pincode);
                    id = adapter.getItem(which).pincode;
                } catch (Throwable e ) {e.printStackTrace();}
            }
        });
        //editText.setAdapter(adapter);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void setUserProfileData() {
        editTexts.get(0).setText(userProfileResponseData.getName());
        editTexts.get(1).setText(userProfileResponseData.getMobile());
        editTexts.get(2).setText(userProfileResponseData.getCity());
        editTexts.get(3).setText(userProfileResponseData.getLocality());
        editTexts.get(4).setText(userProfileResponseData.getFlat());
        adapter = new ArrayAdapter<Converter.pin>(getActivity(), android.R.layout.simple_dropdown_item_1line, pins);
        pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showPincodeSelector();
            }
        });
        pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPincodeSelector();
            }
        });
        //pincode.setText(userProfileResponseData.getPincode());
        //pincode.setAdapter();
        //editTexts.get(5).setText();
        //Toast.makeText(getActivity(), "Pin "+userProfileResponseData.getPincode(), Toast.LENGTH_SHORT).show();
        pincode.setText(userProfileResponseData.getPincode());
        id = (userProfileResponseData.getPincode());
        editTexts.get(6).setText(userProfileResponseData.getState());
        editTexts.get(7).setText(userProfileResponseData.getLandmark());
        try {
            if (userProfileResponseData.getGender().equalsIgnoreCase("Female")) {
                circleImageViews.get(0).setImageResource(R.drawable.male_unselect);
                circleImageViews.get(1).setImageResource(R.drawable.female_select);
                gender = "female";
            } else if (userProfileResponseData.getGender().equalsIgnoreCase("male")) {
                circleImageViews.get(0).setImageResource(R.drawable.male_select);
                circleImageViews.get(1).setImageResource(R.drawable.female_unselect);
                gender = "male";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.male, R.id.female, R.id.submitBtn, R.id.logout, R.id.loginNow, R.id.txtSignUp, R.id.verfiyNow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.male:
                circleImageViews.get(0).setImageResource(R.drawable.male_select);
                circleImageViews.get(1).setImageResource(R.drawable.female_unselect);
                gender = "male";
                break;
            case R.id.female:
                circleImageViews.get(0).setImageResource(R.drawable.male_unselect);
                circleImageViews.get(1).setImageResource(R.drawable.female_select);
                gender = "female";
                break;
            case R.id.submitBtn:
                if (gender.equalsIgnoreCase("")) {
                    Config.showCustomAlertDialog(getActivity(), "Please choose your gender to update your profile", "",
                            SweetAlertDialog.ERROR_TYPE);
                } else if (validate(editTexts.get(0))
                        && validate(editTexts.get(1))
                        && validate(editTexts.get(2))
                        && validate(editTexts.get(3))
                        && validate(editTexts.get(4))
                        //&& validatePinCode(editTexts.get(5))
                        && validate(editTexts.get(6))) {
                    String pinText = pincode.getText().toString();
                    Boolean acceptPincode = false;
                    for (int i = 0; i < pins.size(); i++) {
                        if (pins.get(i).pincode.equals(pinText)) acceptPincode = true;
                    }
                    if (!acceptPincode) {
                        Toast.makeText(getActivity(), "Please select a valid location", Toast.LENGTH_SHORT).show();
                    }
                    updateProfile();
                }
                break;
            case R.id.logout:
                logout();
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

    private void logout() {

        final SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        alertDialog.setTitleText("Are you sure you want to logout?");
        alertDialog.setCancelText("Cancel");
        alertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                alertDialog.dismissWithAnimation();
            }
        });
        alertDialog.show();
        Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
        btn.setBackground(getResources().getDrawable(R.drawable.custom_dialog_button));
        btn.setText("Logout");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.saveUserData(getActivity(), "email", "");
                Common.saveUserData(getActivity(), "userId", "");
                Config.moveTo(getActivity(), Login.class);
                getActivity().finishAffinity();

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

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Config.getCartList(getActivity(), true);
    }


    public void getUserProfileData() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Api.getClient().getPinCodes(new ResponseCallback() {
            @Override
            public void success(Response response) {
                try {
                    pins = Converter.pin.getPins(new JSONArray(Converter.getString(response)));
                    Api.getClient().getUserProfile(MainActivity.userId, new Callback<UserProfileResponse>() {
                        @Override
                        public void success(UserProfileResponse userProfileResponse, Response response) {
                            pDialog.dismiss();
                            try {
                                userProfileResponseData = userProfileResponse;
                                if (userProfileResponse.getSuccess().equalsIgnoreCase("false")) {
                                    profileLayout.setVisibility(View.INVISIBLE);
                                    verifyEmailLayout.setVisibility(View.VISIBLE);
                                } else
                                    setUserProfileData();
                            } catch (Exception e) {
                                pDialog.dismiss();
                                Log.d("profileResponse", "NULL");
                            }


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            pDialog.dismiss();
                            profileLayout.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Couldn't load your profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    Toast.makeText(getActivity(), "Couldn't fetch your profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                pDialog.dismiss();                            profileLayout.setVisibility(View.INVISIBLE);

                Toast.makeText(getActivity(), "Cannot load your profile", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void updateProfile() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        //editTexts.get(5).getText().toString().trim(),
        Api.getClient().updateProfile(
                MainActivity.userId,
                editTexts.get(0).getText().toString().trim(),
                editTexts.get(2).getText().toString().trim(),
                editTexts.get(6).getText().toString().trim(),
                id,
                editTexts.get(3).getText().toString().trim(),
                editTexts.get(4).getText().toString().trim(),
                gender,
                editTexts.get(1).getText().toString().trim(),
                editTexts.get(7).getText().toString().trim(),
                new Callback<SignUpResponse>() {
                    @Override
                    public void success(SignUpResponse signUpResponse, Response response) {
                        pDialog.dismiss();
                        if (signUpResponse.getSuccess().equalsIgnoreCase("true")) {
                            Config.showCustomAlertDialog(getActivity(),
                                    "Profile Status",
                                    "Profile updated",
                                    SweetAlertDialog.SUCCESS_TYPE);
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pDialog.dismiss();

                    }
                });
    }

}
