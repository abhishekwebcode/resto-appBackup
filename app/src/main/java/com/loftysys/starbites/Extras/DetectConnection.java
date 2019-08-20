package com.loftysys.starbites.Extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;


public class DetectConnection {
    public static boolean checkInternetConnection(Context context) {
        // detect internet connection
        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.d("Sdf", "checkInternetConnection: "+String.valueOf(con_manager.getActiveNetworkInfo() != null));
        Log.d("Sdf", "checkInternetConnection: "+String.valueOf(con_manager.getActiveNetworkInfo().isAvailable()));
        Log.d("Sdf", "checkInternetConnection: "+String.valueOf(con_manager.getActiveNetworkInfo().isConnected()));
        if (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}