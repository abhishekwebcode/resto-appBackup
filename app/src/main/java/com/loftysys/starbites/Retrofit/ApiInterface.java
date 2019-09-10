package com.loftysys.starbites.Retrofit;


import android.telecom.Call;

import com.loftysys.starbites.MVP.AddToWishlistResponse;
import com.loftysys.starbites.MVP.BranchResponse;
import com.loftysys.starbites.MVP.CartistResponse;
import com.loftysys.starbites.MVP.CategoryListResponse;
import com.loftysys.starbites.MVP.FAQResponse;
import com.loftysys.starbites.MVP.MyOrdersResponse;
import com.loftysys.starbites.MVP.Product;
import com.loftysys.starbites.MVP.RecommendedProductsResponse;
import com.loftysys.starbites.MVP.RegistrationResponse;
import com.loftysys.starbites.MVP.RestaurantDetailResponse;
import com.loftysys.starbites.MVP.SignUpResponse;
import com.loftysys.starbites.MVP.StripeResponse;
import com.loftysys.starbites.MVP.TermsResponse;
import com.loftysys.starbites.MVP.UserProfileResponse;
import com.loftysys.starbites.MVP.VoucherResponse;
import com.loftysys.starbites.MVP.WishlistResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ApiInterface {

    // API's endpoints
    @GET("/allitem.php")
    public void getAllProducts(
            Callback<List<Product>> callback);

    @GET("/recom.php")
    public void getRecommendedProducts(
            Callback<RecommendedProductsResponse> callback);

    @GET("/pbyc.php")
    public void getCategoryList(Callback<List<CategoryListResponse>> callback);

    @GET("/get_branch.php")
    public void getBranches(Callback<Response> callback);


    @GET("/get_table.php")
    public void getTables(Callback<Response> callback);

    @GET("/resdetails.php")
    public void getRestaurantDetail(Callback<RestaurantDetailResponse> callback);

    @GET("/faq.php")
    public void getFAQ(Callback<FAQResponse> callback);

    @GET("/terms.php")
    public void getTerms(Callback<TermsResponse> callback);

    @FormUrlEncoded
    @POST("/pushadd.php")
    public void sendAccessToken(@Field("accesstoken") String accesstoken, Callback<RegistrationResponse> callback);

    @FormUrlEncoded
    @POST("/addwishlist.php")
    public void addToWishList(@Field("product_id") String product_id, @Field("user_id") String user_id, Callback<AddToWishlistResponse> callback);


    @FormUrlEncoded
    @POST("/add_cart.php")
    public void addToCart(@Field("product_id") String product_id, @Field("userid") String user_id,
                          @Field("varient_id") String varient_id, @Field("varient_quantity") String varient_quantity,
                          @Field("json_param") String json_param, @Field("varient_name") String varient_name,
                          @Field("varient_price") String varient_price, @Field("product_name") String product_name,
                          Callback<AddToWishlistResponse> callback);

    @GET("/voucher.php")
    public void getVouchers(
            @Query("user_id") String user_id,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/deletecart.php")
    public void deleteCartItem(@Field("user_id") String user_id,
                               @Field("varient_id") String varient_id,
                               @Field("product_id") String product_id, Callback<AddToWishlistResponse> callback);

    @FormUrlEncoded
    @POST("/wishcheck.php")
    public void checkWishList(@Field("product_id") String product_id, @Field("user_id") String user_id, Callback<AddToWishlistResponse> callback);


    @FormUrlEncoded
    @POST("/wishlist.php")
    public void getWishList(@Field("user_id") String user_id, Callback<WishlistResponse> callback);

    @FormUrlEncoded
    @POST("/vieworders.php")
    public void getMyOrders(@Field("user_id") String user_id, Callback<MyOrdersResponse> callback);


    @FormUrlEncoded
    @POST("/viewcart.php")
    public void getCartList(@Field("user_id") String user_id, Callback<CartistResponse> callback);


    @FormUrlEncoded
    @POST("/userprofile.php")
    public void getUserProfile(@Field("user_id") String user_id, Callback<UserProfileResponse> callback);


    @GET("/all_location.php")
    public void getPinCodes(Callback<Response> callback);

    @FormUrlEncoded
    @POST("/updateprofile.php")
    public void updateProfile(@Field("user_id") String user_id,
                              @Field("name") String name,
                              @Field("city") String city,
                              @Field("state") String state,
                              @Field("pincode") String pincode,
                              @Field("local") String local,
                              @Field("flat") String flat,
                              @Field("gender") String gender,
                              @Field("phone") String phone,
                              @Field("landmark") String landmark,
                              Callback<SignUpResponse> callback);


    @FormUrlEncoded
    @POST("/resentmail.php")
    public void resentEmail(@Field("email") String email, Callback<SignUpResponse> callback);


    @FormUrlEncoded
    @POST("/login.php")
    public void login(@Field("email") String email, @Field("password") String password, @Field("logintype") String logintype, Callback<SignUpResponse> callback);


    @FormUrlEncoded
    @POST("/paystripe.php")
    public void stripePayment(@Field("stripeToken") String stripeToken,
                              @Field("total") String total,
                              @Field("user_id") String user_id,
                              @Field("cart_id") String cart_id,
                              @Field("address") String address,
                              @Field("phone") String phone,
                              Callback<StripeResponse> callback);


    @FormUrlEncoded
    @POST("/check_voucher.php")
    public void check_voucher(
            @Field("user_id") String user_id,
            @Field("voucher") String voucher,
            @Field("item_price") Double item_price,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/addorders.php")
    public void addOrder(@Field("user_id") String user_id,
                         @Field("cart_id") String cart_id,
                         @Field("address") String address,
                         @Field("phone") String phone,
                         @Field("paymentref") String paymentref,
                         @Field("paystatus") String paystatus,
                         @Field("total") String total,
                         @Field("paymentmode") String paymentmode,
                         Callback<SignUpResponse> callback);
    @FormUrlEncoded
    @POST("/addorders.php")
    public void addOrderVoucher(@Field("user_id") String user_id,
                         @Field("cart_id") String cart_id,
                         @Field("address") String address,
                         @Field("phone") String phone,
                         @Field("paymentref") String paymentref,
                         @Field("paystatus") String paystatus,
                         @Field("total") String total,
                         @Field("paymentmode") String paymentmode,
                         @Field("delivery") Integer delivery,
                         @Field("restax") String restax,
                         @Field("choose_branch") String chosen_branch,
                         @Field("chosen_table") String chosen_table,
                         @Field("delivery_method") String delivery_method,
                         @Field("voucher") String voucher,
                         @Field("location") String location,
                         Callback<SignUpResponse> callback);

    @FormUrlEncoded
    @POST("/addorders.php")
    public void addOrderVoucherHuntel(@Field("user_id") String user_id,
                                @Field("cart_id") String cart_id,
                                @Field("address") String address,
                                @Field("phone") String phone,
                                @Field("paymentref") String paymentref,
                                @Field("paystatus") String paystatus,
                                @Field("total") String total,
                                @Field("paymentmode") String paymentmode,
                                @Field("delivery") Integer delivery,
                                @Field("restax") String restax,
                                @Field("choose_branch") String chosen_branch,
                                @Field("chosen_table") String chosen_table,
                                @Field("delivery_method") String delivery_method,
                                @Field("voucher") String voucher,
                                @Field("hubtel") String hubtel,
                                Callback<Response> callback);


    @FormUrlEncoded
    @POST("/forgot.php")
    public void forgotPassword(@Field("email") String email, Callback<SignUpResponse> callback);


    @FormUrlEncoded
    @POST("/register1.php")
    public void registration(@Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("logintype") String logintype, Callback<SignUpResponse> callback);


}
